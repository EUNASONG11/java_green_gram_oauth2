package com.green.greengramver.feed.like;

import com.green.greengramver.feed.like.model.FeedLikeReq;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // yaml 적용되는 파일 선택 (application-test.yaml)
@MybatisTest // Mybatis Mapper Test 이기 때문에 작성 >> Mapper들이 전부 객체화 >> DI를 할 수 있다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// 테스트는 기본적으로 메모리 데이터베이스 (H2)를 사용하는데 메모리 데이터베이스로 교체하지 않겠다.
// 즉, 우리가 원래 쓰는 데이터베이스로 테스트를 진행하겠다.
class FeedLikeMapperTest {
    @Autowired //TODO : 스프링 컨테이너가 DI 해주는 게 맞는 지 확인
    FeedLikeMapper feedLikeMapper; // 필드 주입 방식의 DI가 된다.

    final long FEED_ID_1 = 1L;
    final long FEED_ID_5 = 5L;
    final long USER_ID_2 = 2L;

    @Test
    void insFeedLikeDuplicateDataThrowDuplicateKeyException() {
        // given (준비)

        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(FEED_ID_1);
        givenParam.setUserId(USER_ID_2);

        // when (실행)
        // then (단언, 체크)
        assertThrows(DuplicateKeyException.class, () -> {
            feedLikeMapper.insFeedLike(givenParam);
        });
    }

    @Test
    void insFeedLikeNormal() {
        // given
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(FEED_ID_5);
        givenParam.setUserId(USER_ID_2);

        //when
        int actualAffectedRows = feedLikeMapper.insFeedLike(givenParam);

        //then
        assertEquals(1, actualAffectedRows);
    }
}