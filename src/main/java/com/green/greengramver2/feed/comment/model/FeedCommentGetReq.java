package com.green.greengramver2.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
@ToString
public class FeedCommentGetReq{
    private final static int FIRST_COMMENT_SIZE = 3;
    private final static int DEFAULT_PAGE_SIZE = 20;
    @Schema(title="피드 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;
    @Schema(title="페이지", description = "2이상의 값만 사용해주세요. 아이템 수는 20개입니다.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int page;
    @JsonIgnore
    private int startIdx;
    @JsonIgnore
    private int size;

    public FeedCommentGetReq (@BindParam("feed_id") long feedId, int page) {
        this.feedId = feedId;
        setPage(page);
    }

    public void setPage(int page) {
        this.page = page;
        if (page < 1) {return;}
        if (page == 1) {
            startIdx = 0;
            size = FIRST_COMMENT_SIZE + 1;//+1은 isMore 처리용
            return;
        }
        startIdx = ((page - 2) * DEFAULT_PAGE_SIZE) + FIRST_COMMENT_SIZE;
        size = DEFAULT_PAGE_SIZE + 1; //+1은 isMore 처리용
    }
}
