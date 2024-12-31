package com.green.greengramver.feed.like;

/*
    TDD - Test Driven Development
    테스트 코드를 작성 > 작업 코드를 검증
    Spring 권장 JUnit 5

    Unit Test (단위 테스트) - 메소드 1개
    Slice Test (계층 테스트) - Controller or Service or Mapper
    Integration Test (통합 테스트) - Controller - Service - Mapper
 */

import com.green.greengramver.TestUtils;
import com.green.greengramver.feed.like.model.FeedLikeReq;
import com.green.greengramver.feed.like.model.FeedLikeVo;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // yaml 적용되는 파일 선택 (application-test.yaml)
@MybatisTest // Mybatis Mapper Test 이기 때문에 작성 >> Mapper들이 전부 객체화 >> DI를 할 수 있다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// 테스트는 기본적으로 메모리 데이터베이스 (H2)를 사용하는데 메모리 데이터베이스로 교체하지 않겠다.
// 즉, 우리가 원래 쓰는 데이터베이스로 테스트를 진행하겠다.
// @TestInstance(TestInstance.Lifecycle.PER_CLASS) // 기본 설정은 PER_METHOD, PER_CLASS로 하면 테스트 객체를 딱 하나만 만든다.
class FeedLikeMapperTest {
    @Autowired
    FeedLikeMapper feedLikeMapper; // 필드 주입 방식의 DI가 된다.

    @Autowired
    FeedLikeTestMapper feedLikeTestMapper;

    static final long FEED_ID_1 = 1L;
    static final long FEED_ID_5 = 5L;
    static final long USER_ID_2 = 2L;

    static final FeedLikeReq existedData = new FeedLikeReq();
    static final FeedLikeReq notExistedData = new FeedLikeReq();
    /*
        @BeforeAll - 모든 테스트 실행 전에 최초 한 번 실행
        ---
        @BeforeEach - 각 테스트 실행 전에 실행
        @Test
        @AfterEach - 각 테스트 실행 후에 실행
        ---
        @AfterAll - 모든 테스트 실행 후에 최초 한 번 실행
     */

    //@BeforeAll - 모든 테스트 메소드 실행 되기 전 최초 딱 한 번만 실행이 되는 메소드
    // 테스트 메소드마다 테스트 객체가 만들어지면 BeforeAll 메소드는 static 메소드여야 한다.
    // (하나의 객체로 만들 수 있음 - @TestInstance(TestInstance.Lifecycle.PER_CLASS))한 테스트 객체가 만들어지면 non-static 메소드일 수 있다.

    @BeforeAll
    static void initData() {
        existedData.setFeedId(FEED_ID_1);
        existedData.setUserId(USER_ID_2);

        notExistedData.setFeedId(FEED_ID_5);
        notExistedData.setUserId(USER_ID_2);
    }

    //@BeforeEach - 테스트 메소드마다 테스트 메소드 실행 전에 실행 되는 before 메소드
    // void before () {}


    @Test
    @DisplayName("중복된 데이터 입력 시 DuplicateKeyException 발생 체크")
    void insFeedLikeDuplicateDataThrowDuplicateKeyException() {
        assertThrows(DuplicateKeyException.class, () -> {
            feedLikeMapper.insFeedLike(existedData);
        }, "데이터 중복 시 에러 발생되지 않음 > Primary key(feed_id, user_id) 확인 바람");
    }

    @Test
    void insFeedLikeNormal() {
        //when
        //select를 한 이유는 insert 전과 후를 비교하기 위해서
        List<FeedLikeVo> actualFeedLikeListBefore = feedLikeTestMapper.selFeedLikeAll(); //insert 전 기존 튜플 수 check
        FeedLikeVo actualFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExistedData); //insert 전 WHERE 절에 PK로 데이터를 가져옴

        int actualAffectedRows = feedLikeMapper.insFeedLike(notExistedData);

        FeedLikeVo actualFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExistedData); //insert 후 WHERE 절에 PK로 데이터를 가져옴
        List<FeedLikeVo> actualFeedLikeListAfter = feedLikeTestMapper.selFeedLikeAll(); //insert 후 튜플 수 check

        //then
        assertAll(
                  () -> TestUtils.assertCurrentTimeStamp(actualFeedLikeVoAfter.getCreatedAt())
                , () -> assertEquals(actualFeedLikeListBefore.size() + 1, actualFeedLikeListAfter.size())
                , () -> assertNull(actualFeedLikeVoBefore) //내가 insert 하려고 한 데이터가 없는지를 단언 > null이어야 한다.
                , () -> assertNotNull(actualFeedLikeVoAfter) //실제 insert가 내가 원하는 데이터로 되었는 지 단언
                , () -> assertEquals(1, actualAffectedRows)
                , () -> assertEquals(notExistedData.getFeedId(), actualFeedLikeVoAfter.getFeedId()) //내가 원하는 데이터로 insert 되었는 지 double check
                , () -> assertEquals(notExistedData.getUserId(), actualFeedLikeVoAfter.getUserId()) //내가 원하는 데이터로 insert 되었는 지 double check
        );
    }

    @Test
    void delFeedLikeNoData() {
        int actualAffectedRows = feedLikeMapper.delFeedLike(notExistedData);

        assertEquals(0, actualAffectedRows);
    }

    @Test
    void delFeedLikeNormal() {
        FeedLikeVo actualFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedData);

        int actualAffectedRows = feedLikeMapper.delFeedLike(existedData);

        FeedLikeVo actualFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedData);

        assertAll(
                  () -> assertEquals(1, actualAffectedRows)
                , () -> assertNotNull(actualFeedLikeVoBefore)
                , () -> assertNull(actualFeedLikeVoAfter)
        );
    }
}