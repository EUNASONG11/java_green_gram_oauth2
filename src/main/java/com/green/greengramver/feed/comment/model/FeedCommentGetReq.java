package com.green.greengramver.feed.comment.model;

import com.green.greengramver.common.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@ToString
@EqualsAndHashCode
public class FeedCommentGetReq{
    private final static int FIRST_COMMENT_SIZE = 3;

    @Positive
    @Schema(title="피드 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    @PositiveOrZero
    @Schema(title="튜플 시작 index", name="start_idx", requiredMode = Schema.RequiredMode.REQUIRED)
    private int startIdx;

    @Min(value = 21, message = "사이즈는 20이상이어야 합니다.")
    @Schema(title="페이지당 아이템 수", description = "default: 20", example = "20")
    private int size;

    @ConstructorProperties({"feed_id","start_idx","size"})
    public FeedCommentGetReq (long feedId, int startIdx, Integer size) {
        this.feedId = feedId;
        this.startIdx = startIdx;
        this.size = (size == null ? Constants.getDefault_page_size() : size) + 1;
    }
}
