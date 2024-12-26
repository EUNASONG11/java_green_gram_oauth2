package com.green.greengramver.feed.model;

import com.green.greengramver.feed.comment.model.FeedCommentDto;
import com.green.greengramver.feed.comment.model.FeedCommentGetRes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FeedGetRes {
    private long feedId;
    private String contents;
    private String location;
    private String createdAt;
    private long writerUserId;
    private String writerPic;
    private String writerNm;
    private int isLike;

    private List<String> pics;

    private FeedCommentGetRes comment;


    public FeedGetRes(FeedWithPicCommentDto dto) {
        this.feedId = dto.getFeedId();
        this.contents = dto.getContents();
        this.location = dto.getLocation();
        this.createdAt = dto.getCreatedAt();
        this.writerUserId = dto.getWriterUserId();
        this.writerPic = dto.getWriterPic();
        this.writerNm = dto.getWriterNm();
        this.isLike = dto.getIsLike();
        this.pics = dto.getPics();

        //dto.getCommentList().size()값이 4라면
        this.comment = new FeedCommentGetRes();
        List<FeedCommentDto> list = dto.getCommentList();
        if (list == null) {
            comment.setCommentList(new ArrayList<>());
        } else if (dto.getCommentList().size() == 4) {
            this.comment.setMoreComment(true);
            list.remove(dto.getCommentList().size() - 1);
        }
    }
}
