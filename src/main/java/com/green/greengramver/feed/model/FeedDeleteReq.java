package com.green.greengramver.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
@Schema(title="피드 delete 요청")
@EqualsAndHashCode
public class FeedDeleteReq {
    @Schema(description = "피드 PK", example = "2", name="feed_id", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    @ConstructorProperties({"feed_id"})
    public FeedDeleteReq(long feedId) {
        this.feedId = feedId;
    }
}
