package com.green.greengramver2.feed.comment.model;

import lombok.Getter;
import lombok.Setter;

//Data Transfer Object
@Getter
@Setter
public class FeedCommentDto {
    private long feedCommentId;
    private String comment;
    private long writerUserId;
    private String writerPic;
    private String writerNm;
}
