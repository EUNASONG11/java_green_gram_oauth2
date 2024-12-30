package com.green.greengramver.feed.like;

import com.green.greengramver.feed.like.model.FeedLikeReq;
import com.green.greengramver.feed.like.model.FeedLikeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedLikeTestMapper {
    @Select("SELECT * FROM feed_like WHERE feed_id = #{feedId} AND user_id = #{userId}") // PK가 WHERE 절에 들어갔을 때 나올 수 있는 경우의 수는 2가지(null 또는 1개)
    FeedLikeVo selFeedLikeByFeedIdAndUserId(FeedLikeReq p);

    @Select("SELECT * FROM feed_like") //만약 feed_like에 튜플이 하나도 없다면 size가 0인 List가 넘어옴(null이 넘어오지는 않음)
    List<FeedLikeVo> selFeedLikeAll();
}
