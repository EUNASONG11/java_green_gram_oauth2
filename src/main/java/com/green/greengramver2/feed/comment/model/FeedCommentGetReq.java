package com.green.greengramver2.feed.comment.model;

import com.green.greengramver2.common.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.BindParam;

import java.beans.ConstructorProperties;

@Getter
@Setter
@ToString
public class FeedCommentGetReq{
    private final static int FIRST_COMMENT_SIZE = 3;

    @Schema(title="피드 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    @Schema(title="튜플 시작 index", name="start_idx", requiredMode = Schema.RequiredMode.REQUIRED)
    private int startIdx;

    @Schema(title="페이지당 아이템 수", description = "default: 20", example = "20")
    private int size;

    @ConstructorProperties({"feed_id","start_idx","size"})
    public FeedCommentGetReq (long feedId, int startIdx, Integer size) {
        this.feedId = feedId;
        this.startIdx = startIdx;
        this.size = (size == null ? Constants.getDefault_page_size() : size) + 1;
    }
}
