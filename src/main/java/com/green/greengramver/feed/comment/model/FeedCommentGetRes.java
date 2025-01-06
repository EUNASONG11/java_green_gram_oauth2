package com.green.greengramver.feed.comment.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
public class FeedCommentGetRes {
    private boolean moreComment;
    private List<FeedCommentDto> commentList;
}
