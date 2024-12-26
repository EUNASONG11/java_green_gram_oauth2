package com.green.greengramver.feed;

import com.green.greengramver.feed.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {
    int insFeed(FeedPostReq p);
    List<FeedGetRes> selFeedList(FeedGetReq p);

    List<FeedAndPicDto> selFeedWithPicList(FeedGetReq p);

    int delFeedLikeAndFeedCommentAndFeedPic(FeedDeleteReq p);
    int delFeed(FeedDeleteReq p);
}
