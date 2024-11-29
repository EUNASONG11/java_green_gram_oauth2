package com.green.greengramver2.feed;

import com.green.greengramver2.feed.model.FeedPicDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedPicsMapper {
    int insFeedPics(FeedPicDto p);
}
