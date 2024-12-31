package com.green.greengramver.feed;

import com.green.greengramver.TestUtils;
import com.green.greengramver.feed.model.FeedPicDto;
import com.green.greengramver.feed.model.FeedPicVo;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.MyBatisSystemException;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@MybatisTest //Mybatis와 관련된 객체 다 생성
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //기존 데이터베이스로 테스트 하겠다(H2 말고)
class FeedPicMapperTest {
    @Autowired
    FeedPicMapper feedPicMapper;

    @Autowired
    FeedPicTestMapper feedPicTestMapper;

    @Test
    void insFeedPicNoFeedIdThrowForeignKeyException() {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(10L);
        givenParam.setPics(new ArrayList<>(1));
        givenParam.getPics().add("a.jpg");

        assertThrows(DataIntegrityViolationException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }

    @Test
    void insFeedPicNullPicsThrowNotNullException() {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);

        assertThrows(MyBatisSystemException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }

    @Test
    void insFeedPicNoPicsThrowNotNullException() {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);
        givenParam.setPics(new ArrayList<>());

        assertThrows(BadSqlGrammarException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }

    @Test
    void insFeedPic_PicStringLengthMoreThan50_ThrowException() { // 가독성을 위해서 _ 사용
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);
        givenParam.setPics(new ArrayList<>(1));
        givenParam.getPics().add("_123456789_123456789_123456789_123456789_123456789_12");
        assertThrows(BadSqlGrammarException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }

    @Test
    void insFeedPic() {
        String[] pics = {"a.jpg", "b.jpg", "c.jpg"};
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(5L);
        givenParam.setPics(new ArrayList<>(pics.length));
        for (String pic : pics) {
            givenParam.getPics().add(pic);
        }

        List<FeedPicVo> feedPicListBefore = feedPicTestMapper.selFeedPicListByFeedId(givenParam.getFeedId());

        int actualAffectedRows = feedPicMapper.insFeedPic(givenParam);

        List<FeedPicVo> feedPicListAfter = feedPicTestMapper.selFeedPicListByFeedId(givenParam.getFeedId());

        //feedPicListAfter에서 pic만 뽑아내서 이전처럼 List<String> 변형한 후 체크
        List<String> feedOnlyPicList = new ArrayList<>(feedPicListAfter.size());
        for (FeedPicVo feedPicVo : feedPicListAfter) {
            feedOnlyPicList.add(feedPicVo.getPic());
        }


        List<String> picList = Arrays.asList(pics);
        for (int i = 0; i < pics.length; i++) {
            String pic = picList.get(i);
            System.out.printf("%s - contains: %b\n", pic, feedOnlyPicList.contains(pic));
        }

        //stream을 이용해서 한다.
        //Predicate - 리턴 타입 O(boolean), 파라미터 O(FeedPicVo)
        String[] pics2 = {"a.jpg", "b.jpg", "c.jpg", "d.jpg"};

        feedPicListAfter.stream().allMatch(feedPicVo -> picList.contains(feedPicVo.getPic()));


        assertAll(
                  () -> feedPicListAfter.forEach(feedPicVo -> TestUtils.assertCurrentTimeStamp(feedPicVo.getCreatedAt()))
                , () -> assertEquals(givenParam.getPics().size(), actualAffectedRows)
                , () -> assertEquals(0, feedPicListBefore.size())
                , () -> assertEquals(givenParam.getPics().size(), feedPicListAfter.size())
                , () -> assertTrue(feedOnlyPicList.containsAll(Arrays.asList(pics)))
                , () -> assertTrue(Arrays.asList(pics).containsAll(feedOnlyPicList))
                , () -> assertTrue(feedPicListAfter.stream().allMatch(feedPicVo -> picList.contains(feedPicVo.getPic())))

                , () -> assertTrue(feedPicListAfter.stream() // stream 생성
                                                            .map(FeedPicVo::getPic) // 똑같은 크기의 새로운 반환 Stream<String> ["a.jpg", "b.jpg", "c.jpg"]
                                                            .filter(pic -> picList.contains(pic)) // 필터는 연산의 결과가 true 인 것만 뽑아내서 새로운 stream 반환 Stream<String> ["a.jpg", "b.jpg", "c.jpg"]
                                                            .limit(picList.size()) // stream 크기를 제한, 이전 stream의 크기가 10개인데 limit(2)로 하면 2개짜리 stream이 반환된다.
                                                            .count() == picList.size())
                , () -> assertTrue(feedPicListAfter.stream().map(FeedPicVo::getPic).toList().containsAll(Arrays.asList(pics)))
                // Function return type 0, parameter 0 (FeedPicVo)
                , () -> assertTrue(feedPicListAfter.stream().map(feedPicVo -> feedPicVo.getPic()) // ["a.jpg", "b.jpg", "c.jpg"]
                                                   .toList() // stream > List
                                                   .containsAll(Arrays.asList(pics)))
        );
    }

}