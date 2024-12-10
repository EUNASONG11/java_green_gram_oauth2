package com.green.greengramver.feed;

import com.green.greengramver.common.MyFileUtils;
import com.green.greengramver.feed.comment.FeedCommentMapper;
import com.green.greengramver.feed.comment.model.FeedCommentDto;
import com.green.greengramver.feed.comment.model.FeedCommentGetReq;
import com.green.greengramver.feed.comment.model.FeedCommentGetRes;
import com.green.greengramver.feed.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper mapper;
    private final FeedPicMapper feedPicMapper;
    private final MyFileUtils myFileUtils;
    private final FeedCommentMapper feedCommentMapper;

    @Transactional
    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p) {
        int result = mapper.insFeed(p);
        //-------------- 파일 등록
        long feedId = p.getFeedId();
        //저장 폴더 만들기, 저장 위치/feed/${feedId}/파일들을 저장한다.
        String middlePath = String.format("feed/%d", feedId);
        myFileUtils.makeFolders(middlePath);

        //랜덤 파일명 저장용 >> feed_pics 테이블에 저장할 때 사용
        List<String> picNameList  = new ArrayList<>(pics.size());
        for (MultipartFile pic : pics) {
            //각 파일 랜덤파일명 만들기
            String savedPicName = myFileUtils.makeRandomFileName(pic);
            picNameList.add(savedPicName);
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try {
                myFileUtils.transferTo(pic, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        FeedPicDto feedPicDto = new FeedPicDto();
        feedPicDto.setFeedId(feedId);
        feedPicDto.setPics(picNameList);

        int resultPis = feedPicMapper.insFeedPic(feedPicDto);

        return FeedPostRes.builder()
                          .feedId(feedId)
                          .pics(picNameList)
                          .build();
    }

    // N+1 이슈 발생 (만약 피드가 20개면 총 41번의 select가 발생)
    public List<FeedGetRes> getFeedList(FeedGetReq p) {
        List<FeedGetRes> list = mapper.selFeedList(p);

        for (FeedGetRes res : list) {
            //피드 당 사진 리스트
            List<String> pics = feedPicMapper.selFeedPic(res.getFeedId());
            res.setPics(pics);

            //피드당 댓글 4개
            FeedCommentGetReq commentGetReq = new FeedCommentGetReq(res.getFeedId(), 0, 3);
//            commentGetReq.setPage(1);
//            commentGetReq.setFeedId(res.getFeedId());

            List<FeedCommentDto> commentList = feedCommentMapper.selFeedCommentList(commentGetReq);

                FeedCommentGetRes commentGetRes = new FeedCommentGetRes();
                commentGetRes.setCommentList(commentList);
                commentGetRes.setMoreComment(commentList.size() == commentGetReq.getSize());//4개면 true, 4개 아니면 false

                if (commentGetRes.isMoreComment()) {
                    commentList.remove(commentList.size() - 1);
            }
            res.setComment(commentGetRes);
        }
        return list;
    }

    // select 2번
    public List<FeedGetRes> getFeedList2(FeedGetReq p) {


        return null;
    }

    // select 3번, 피드 5,000개 있음, 페이지당 20개씩 가져온다.
    public List<FeedGetRes> getFeedList3(FeedGetReq p) {
        // 피드 리스트
        List<FeedGetRes> list = mapper.selFeedList(p);

        // feed_id를 골라내야 한다.
        List<Long> feedIdList = new ArrayList<>();
        for (FeedGetRes res : list) {
            feedIdList.add(res.getFeedId());
        }
        log.info("feedIds: {}", feedIdList);

        // List<Long> feedIds = list.stream().map(FeedGetRes::getFeedId).collect(Collectors.toList());
        // List<Long> feedIds = list.stream().map(res -> ((FeedGetRes)res).getFeedId()).toList();
        // List<Long> feedIds = list.stream().map(res -> {return ((FeedGetRes)res).getFeedId();}).toList();.


        // 피드와 관련된 사진 리스트
        List<FeedPicSel> feedPicList = feedPicMapper.selFeedPicListByFeedId(feedIdList);
        log.info("feedPicList: {}", feedPicList);

        Map<Long, List<String>> picHashMap = new HashMap<>();
        for (FeedPicSel item : feedPicList) {
            long feedId = item.getFeedId();
            if (!picHashMap.containsKey(feedId)) {
                picHashMap.put(feedId, new ArrayList<String>(2));
            }
            List<String> pics = picHashMap.get(feedId);
            pics.add(item.getPic());
        }

//        int lastIndex  = 0;
//        for (FeedGetRes res : list) {
//            List<String> pics = new ArrayList<>(2);
//            for (int i = lastIndex; i < feedPicList.size(); i++) {
//                FeedPicSel feedPicSel = feedPicList.get(i);
//                if (res.getFeedId() == feedPicSel.getFeedId()) {
//                    pics.add(feedPicSel.getPic());
//                } else {
//                    res.setPics(pics);
//                    lastIndex = i;
//                    break;
//                }
//            }
//        }



        // 피드와 관련된 댓글 리스트
        List<FeedCommentDto> commentDto = feedCommentMapper.selFeedCommentsWithLimit(feedIdList);

        Map<Long, FeedCommentGetRes> commentsMap = new HashMap<>();
        for (FeedCommentDto dto : commentDto) {
            long feedId = dto.getFeedId();
            if (!commentsMap.containsKey(feedId)) {
                FeedCommentGetRes feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
                commentsMap.put(feedId, feedCommentGetRes);
            }
            FeedCommentGetRes feedCommentGetRes = commentsMap.get(feedId);
            feedCommentGetRes.getCommentList().add(dto);
        }

        for (FeedGetRes res : list) {
            res.setPics(picHashMap.get(res.getFeedId()));

            FeedCommentGetRes feedCommentGetRes = commentsMap.get(res.getFeedId());

            if (feedCommentGetRes == null) {
                feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
            } else if (feedCommentGetRes.getCommentList().size() == 4) {
                feedCommentGetRes.setMoreComment(true);
                feedCommentGetRes.getCommentList().remove(feedCommentGetRes.getCommentList().size() - 1);
            }
            res.setComment(feedCommentGetRes);
        }


        log.info("feedPicList: {}", feedPicList);
        return list;
    }

    @Transactional
    public int deleteFeed(FeedDeleteReq p) {
        //피드 사진 삭제 (폴더 삭제)
        String deletePath = String.format("%s/feed/%d", myFileUtils.getUploadPath(), p.getFeedId());
        myFileUtils.deleteFolder(deletePath, true);

        //피드 댓글, 좋아요 삭제
        int affectedRows = mapper.delFeedLikeAndFeedCommentAndFeedPic(p);
        log.info("affectedRows: {}", affectedRows);

        //피드 삭제
        return mapper.delFeed(p);
    }
}
