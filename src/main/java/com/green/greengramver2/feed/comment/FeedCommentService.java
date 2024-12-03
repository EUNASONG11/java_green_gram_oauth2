package com.green.greengramver2.feed.comment;

import com.green.greengramver2.feed.comment.model.FeedCommentDto;
import com.green.greengramver2.feed.comment.model.FeedCommentGetReq;
import com.green.greengramver2.feed.comment.model.FeedCommentGetRes;
import com.green.greengramver2.feed.comment.model.FeedCommentPostReq;
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

    public long postFeedComment(FeedCommentPostReq p) {
        int result = mapper.insFeedComment(p);

        return p.getFeedCommentId();
    }

    public FeedCommentGetRes getFeedComment(FeedCommentGetReq p){
        FeedCommentGetRes res = new FeedCommentGetRes();
        if(p.getPage() < 2) {
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
}
