package com.green.greengramver.feed.comment;

import com.green.greengramver.common.exception.CustomException;
import com.green.greengramver.common.exception.FeedErrorCode;
import com.green.greengramver.config.security.AuthenticationFacade;
import com.green.greengramver.entity.Feed;
import com.green.greengramver.entity.FeedComment;
import com.green.greengramver.entity.User;
import com.green.greengramver.feed.FeedRepository;
import com.green.greengramver.feed.comment.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentService {
    private final FeedCommentMapper mapper;
    private final AuthenticationFacade authenticationFacade;
    private final FeedCommentRepository feedCommentRepository;
    private final FeedRepository feedRepository;

    public long postFeedComment(FeedCommentPostReq p) {
//        p.setUserId(authenticationFacade.getSignedUserId());
//        int result = mapper.insFeedComment(p);
//        return p.getFeedCommentId();
        Feed feed = new Feed();
        feed.setFeedId(p.getFeedId());

        User user = new User();
        user.setUserId(authenticationFacade.getSignedUserId());

        FeedComment feedComment = new FeedComment();
        feedComment.setFeedId(feed);
        feedComment.setUserId(user);
        feedComment.setComment(p.getComment());

        feedCommentRepository.save(feedComment);

        return feedComment.getFeedCommentId();
    }

    public FeedCommentGetRes getFeedComment(FeedCommentGetReq p){
        FeedCommentGetRes res = new FeedCommentGetRes();
        if(p.getStartIdx() < 0) {
            res.setCommentList(new ArrayList<>());
            return res;
        }
        List<FeedCommentDto> commentList = mapper.selFeedCommentList(p);
        res.setCommentList(commentList);
        res.setMoreComment(commentList.size() == p.getSize());

        if (res.isMoreComment()) {
            commentList.remove(commentList.size() - 1);
        }
        return res;
    }

    public void delFeedComment(FeedCommentDelReq p) {
//        p.setUserId(authenticationFacade.getSignedUserId());
//        int result = mapper.delFeedComment(p);
//        return result;
//        User user = new User();
//        user.setUserId(authenticationFacade.getSignedUserId());

//        FeedComment feedComment = feedCommentRepository.findByFeedCommentIdAndUserId(p.getFeedCommentId(), user)
//                .orElseThrow(() -> new CustomException(FeedErrorCode.FAIL_TO_REG));

        FeedComment feedComment = feedCommentRepository.findById(p.getFeedCommentId()).orElse(null);

                                    // 그래프 탐색 : feedComment 테이블 내용을 가져왔는데 User 테이블 정보를 탐색
        if (feedComment == null || feedComment.getUserId().getUserId() != authenticationFacade.getSignedUserId()) {
            throw new CustomException(FeedErrorCode.FAIL_TO_DEL_COMMENT);
        }

        feedCommentRepository.delete(feedComment);
    }
}
