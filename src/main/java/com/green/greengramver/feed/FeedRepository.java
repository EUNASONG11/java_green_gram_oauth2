package com.green.greengramver.feed;

import com.green.greengramver.entity.Feed;
import com.green.greengramver.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Optional<Feed> findByFeedIdAndWriterUser(Long feedId, User writerUserId);

    //쿼리 메소드로 delete, update 비추천
    int deleteByFeedIdAndWriterUser(Long feedId, User writerUserId); //select 후 delete 하기 때문에 비추천

    //JPQL(Java Persistence Query Language)
    @Modifying //@Modifying이 있어야 delete or update JPQL, 리턴타입은 void or int 만 가능
    @Query("delete from Feed f where f.feedId=:feedId AND f.writerUser.userId=:writerUserId")
    int deleteFeed(Long feedId, Long writerUserId);

    /*
    Feed(대문자로 시작 - class명으로 작성해야 함)
    :feedId 는 #{feedId}랑 같은 역할
     */
}
