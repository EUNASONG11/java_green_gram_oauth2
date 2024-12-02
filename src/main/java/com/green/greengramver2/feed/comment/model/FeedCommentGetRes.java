package com.green.greengramver2.feed.comment.model;

import com.green.greengramver2.common.model.Paging;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class FeedCommentGetRes {
    private int isMore;
    private List<FeedCommentDto> commentList;
}
