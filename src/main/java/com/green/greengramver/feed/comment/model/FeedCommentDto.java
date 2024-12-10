package com.green.greengramver.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

//Data Transfer Object
@Getter
@Setter
public class FeedCommentDto {
    @JsonIgnore
    private long feedId;
    private long feedCommentId;
    private String comment;
    private long writerUserId;
    private String writerPic;
    private String writerNm;
}
