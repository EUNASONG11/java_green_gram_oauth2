package com.green.greengramver2.feed;

import com.green.greengramver2.common.MyFileUtils;
import com.green.greengramver2.feed.comment.FeedCommentMapper;
import com.green.greengramver2.feed.comment.model.FeedCommentDto;
import com.green.greengramver2.feed.comment.model.FeedCommentGetReq;
import com.green.greengramver2.feed.comment.model.FeedCommentGetRes;
import com.green.greengramver2.feed.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper mapper;
    private final FeedPicsMapper feedPicsMapper;
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

        int resultPis = feedPicsMapper.insFeedPics(feedPicDto);

        return FeedPostRes.builder()
                          .feedId(feedId)
                          .pics(picNameList)
                          .build();
    }
    public List<FeedGetRes> getFeedList(FeedGetReq p) {
        List<FeedGetRes> list = mapper.selFeedList(p);

        for (FeedGetRes res : list) {
            //피드 당 사진 리스트
            List<String> pics = feedPicsMapper.selFeedPics(res.getFeedId());
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

}
