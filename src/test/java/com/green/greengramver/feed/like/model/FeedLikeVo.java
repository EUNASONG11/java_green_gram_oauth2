package com.green.greengramver.feed.like.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
/*
    immutable(불변성)하게 객체를 만들고 싶다. 그러면 setter를 빼야 함
    private한 멤버필드에 값 넣는 방법 2가지 (생성자, setter)
    setter를 빼기로 했기 때문에 남은 선택지는 생성자

    생성자를 이용해서 객체 생성을 해야 하는데 멤버필드 값을 세팅하는 경우의 수가 많을 수 있다.
    1. feedId만 세팅
    2. userId만 세팅
    3. createdAt만 세팅
    4. feedId, userId만 세팅
    5. feedId, createdAt만 세팅
    6. userId, createdAt만 세팅
    7. feedId, userId, createdAt 세팅
    8. 하나도 세팅 안한다.

    이러한 이유로 @Builder 사용
 */

@Getter
@Builder
@EqualsAndHashCode //객체의 equals() 및 hashCode() 메서드를 자동으로 생성해주는 기능, Object 클래스에는 기본적으로 다음과 같은 equals()와 hashCode() 메서드가 포함(오버라이딩)
public class FeedLikeVo {
    private long feedId;
    private long userId;
    private String createdAt;
}
