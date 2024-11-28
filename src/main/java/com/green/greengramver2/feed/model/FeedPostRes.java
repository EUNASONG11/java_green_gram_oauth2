package com.green.greengramver2.feed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(title = "피드 등록 응답")
public class FeedPostRes {
    @Schema(title="피드 PK")
    private long feedId;
    @Schema(title="피드 사진 리스트")
    private List<String> pics;
}
