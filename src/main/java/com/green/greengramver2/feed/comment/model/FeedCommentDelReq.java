package com.green.greengramver2.feed.comment.model;

import lombok.Getter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
public class FeedCommentDelReq {
    private long feedCommentId;
    private long userId;

    public FeedCommentDelReq(@BindParam("feed_comment_id") long feedCommentId, @BindParam("signed_user_id") long userId) {
        this.feedCommentId = feedCommentId;
        this.userId = userId;
    }
}
