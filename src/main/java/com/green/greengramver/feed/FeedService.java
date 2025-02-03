package com.green.greengramver.feed;

import com.green.greengramver.common.MyFileUtils;
import com.green.greengramver.common.exception.CustomException;
import com.green.greengramver.common.exception.FeedErrorCode;
import com.green.greengramver.config.security.AuthenticationFacade;
import com.green.greengramver.entity.Feed;
import com.green.greengramver.entity.FeedPic;
import com.green.greengramver.entity.FeedPicIds;
import com.green.greengramver.entity.User;
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
    private final AuthenticationFacade authenticationFacade;
    private final FeedRepository feedRepository;
    private final FeedPicRepository feedPicRepository;

    @Transactional
    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p) {
        User signedUser = new User();
        signedUser.setUserId(authenticationFacade.getSignedUserId());

        Feed feed = new Feed();
        feed.setWriterUser(signedUser);
        feed.setContents(p.getContents());
        feed.setLocation(p.getLocation());

        //p.setWriterUserId(authenticationFacade.getSignedUserId());
//        int result = mapper.insFeed(p);
//        if (result == 0) {
//            throw new CustomException(FeedErrorCode.FAIL_TO_REG);
//        }

        feedRepository.save(feed);
        //-------------- 파일 등록
        long feedId = feed.getFeedId();

        //저장 폴더 만들기, 저장 위치/feed/${feedId}/파일들을 저장한다.
        String middlePath = String.format("feed/%d", feedId);
        myFileUtils.makeFolders(middlePath);

//        //랜덤 파일명 저장용 >> feed_pics 테이블에 저장할 때 사용
        List<String> picNameList  = new ArrayList<>(pics.size());
        for (MultipartFile pic : pics) {
            //각 파일 랜덤파일명 만들기
            String savedPicName = myFileUtils.makeRandomFileName(pic);
            picNameList.add(savedPicName);
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try {
                FeedPicIds ids = new FeedPicIds();
                ids.setFeedId(feedId);
                ids.setPic(savedPicName);

                FeedPic feedPic = new FeedPic();
                feedPic.setFeedPicIds(ids);
                feedPic.setFeed(feed);

                feedPicRepository.save(feedPic);

                myFileUtils.transferTo(pic, filePath);
            } catch (IOException e) {
                //폴더 삭제 처리
                String delFolderPath = String.format("%s/%s", myFileUtils.getUploadPath(), middlePath);
                myFileUtils.deleteFolder(delFolderPath, true);
                throw new CustomException(FeedErrorCode.FAIL_TO_REG);
            }

        }
//        FeedPicDto feedPicDto = new FeedPicDto();
//        feedPicDto.setFeedId(feedId);
//        feedPicDto.setPics(picNameList);

        //int resultPis = feedPicMapper.insFeedPic(feedPicDto);

        return FeedPostRes.builder()
                          .feedId(feedId)
                          .pics(picNameList)
                          .build();
    }
    // JPA에는 UPDATE가 없음

    // N+1 이슈 발생 (만약 피드가 20개면 총 41번의 select가 발생)
    public List<FeedGetRes> getFeedList(FeedGetReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
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
        List<FeedGetRes> list = new ArrayList<>(p.getSize());
        List<Long> feedIdList = new ArrayList<>(p.getSize());
        //SELECT (1) : feed + feed_pic
        List<FeedAndPicDto> feedAndPicDtoList = mapper.selFeedWithPicList(p);

        FeedGetRes beforeFeedGetRes = new FeedGetRes(); // feedId 값 포함 모든 값을 0으로 세팅
        for (FeedAndPicDto feedAndPicDto : feedAndPicDtoList) {
            if (beforeFeedGetRes.getFeedId() != feedAndPicDto.getFeedId()) { // feedId가 달랐을 때만 새로 담기
                feedIdList.add(feedAndPicDto.getFeedId());

                beforeFeedGetRes = new FeedGetRes(); // feedId 값이 다르다면 새로운 객체 생성
                beforeFeedGetRes.setPics(new ArrayList<>(3));
                list.add(beforeFeedGetRes);
                beforeFeedGetRes.setFeedId(feedAndPicDto.getFeedId());
                beforeFeedGetRes.setContents(feedAndPicDto.getContents());
                beforeFeedGetRes.setLocation(feedAndPicDto.getLocation());
                beforeFeedGetRes.setCreatedAt(feedAndPicDto.getCreatedAt());
                beforeFeedGetRes.setWriterUserId(feedAndPicDto.getWriterUserId());
                beforeFeedGetRes.setWriterPic(feedAndPicDto.getWriterPic());
                beforeFeedGetRes.setWriterNm(feedAndPicDto.getWriterNm());
                beforeFeedGetRes.setIsLike(feedAndPicDto.getIsLike());
            }
            beforeFeedGetRes.getPics().add(feedAndPicDto.getPic());
        }


        //SELECT (2) : feed_comment
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


        return list;
    }

    // select 3번, 피드 5,000개 있음, 페이지당 20개씩 가져온다.
    public List<FeedGetRes> getFeedList3(FeedGetReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        // 피드 리스트
        List<FeedGetRes> list = mapper.selFeedList(p);
        if (list.size() == 0) {
            return list;
        }

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

        Map<Long, List<String>> picHashMap = new HashMap<>(); // Long 타입의 feedId를 연결고리로
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

    //Mybatis
    public List<FeedGetRes> getFeedList4(FeedGetReq p) {
        List<FeedWithPicCommentDto> dtoList = mapper.selFeedWithPicAndCommentLimit4List(p);
        List<FeedGetRes> res = new ArrayList<>(dtoList.size());
        for (FeedWithPicCommentDto dto : dtoList) {
            res.add(new FeedGetRes(dto));
        }
        return res;
    }

    @Transactional
    public int deleteFeed(FeedDeleteReq p) {
        User SignedUser = new User();
        SignedUser.setUserId(authenticationFacade.getSignedUserId());

//        Feed feed = feedRepository.findByFeedIdAndWriterUser(p.getFeedId(), SignedUser)
//                                  .orElseThrow(() -> new CustomException(FeedErrorCode.FAIL_TO_REG));
//        feedRepository.delete(feed);

        //int affectedRows = feedRepository.deleteByFeedIdAndWriterUser(p.getFeedId(), SignedUser);
        int affectedRows = feedRepository.deleteFeed(p.getFeedId(), authenticationFacade.getSignedUserId());

        if(affectedRows == 0) {
            throw new CustomException(FeedErrorCode.FAIL_TO_REG);
        }


//        p.setSignedUserId(authenticationFacade.getSignedUserId());
//        //피드 사진 삭제 (폴더 삭제)
//        String deletePath = String.format("%s/feed/%d", myFileUtils.getUploadPath(), p.getFeedId());
//        myFileUtils.deleteFolder(deletePath, true);
//
//        //피드 댓글, 좋아요 삭제
//        int affectedRows = mapper.delFeedLikeAndFeedCommentAndFeedPic(p);
//        log.info("affectedRows: {}", affectedRows);

        //피드 삭제
        return 1;
    }
}
