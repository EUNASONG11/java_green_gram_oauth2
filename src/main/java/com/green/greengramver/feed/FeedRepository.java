package com.green.greengramver.feed;

import com.green.greengramver.entity.Feed;
import com.green.greengramver.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Optional<Feed> findByFeedIdAndWriterUser(Long feedId, User writerUserId); //equals 값

    //쿼리 메소드로 delete, update 비추천
    int deleteByFeedIdAndWriterUser(Long feedId, User writerUserId); //select 후 delete 하기 때문에 비추천

    //JPQL(Java Persistence Query Language) 유지, 보수성이 좋음(예를 들어 feedId를 리팩토링으로 네임을 바꾸면 자동으로 바뀜)
    @Modifying //@Modifying이 있어야 delete or update JPQL, 리턴타입은 void or int 만 가능
    @Query("delete from Feed f where f.feedId=:feedId AND f.writerUser.userId=:writerUserId")
    int deleteFeed(Long feedId, Long writerUserId);

    //SQL로 작업하고 싶을 때
//    @Modifying
//    @Query(value = "delete from feed f where f.feed_id =:feedId and f.writer_user_id =:writerUserId", nativeQuery = true)
//    int deleteFeedSql(Long feedId, Long writerUserId);

    /*
    Feed(대문자로 시작 - class명으로 작성해야 함)
    :feedId 는 #{feedId}랑 같은 역할, 멤버필드명 작성

    feedId = 1, writerUserid = 2 라는 가정 하에 SQL문이 만들어진다.
    DELETE FROM feed f
     WHERE f.feed_id = 1
       AND f.user_id = 2
     */
}
