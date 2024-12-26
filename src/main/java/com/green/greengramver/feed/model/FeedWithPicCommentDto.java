package com.green.greengramver.feed.model;

import com.green.greengramver.feed.comment.model.FeedCommentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeedWithPicCommentDto {
    private long feedId;
    private String contents;
    private String location;
    private String createdAt;
    private long writerUserId;
    private String writerPic;
    private String writerNm;
    private int isLike;
    private List<String> pics;
    private List<FeedCommentDto> commentList;
}
