package com.green.greengramver.feed;

import com.green.greengramver.feed.model.FeedPicDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedPicMapper {
    int insFeedPic(FeedPicDto p);
    List<String> selFeedPic(long feedId);
}
