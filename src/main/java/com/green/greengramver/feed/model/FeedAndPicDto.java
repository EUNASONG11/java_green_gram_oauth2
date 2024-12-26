package com.green.greengramver.feed.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedAndPicDto {
    private long feedId;
    private String contents;
    private String location;
    private String createdAt;
    private long writerUserId;
    private String writerPic;
    private String writerNm;
    private int isLike;
    private String pic;
}
