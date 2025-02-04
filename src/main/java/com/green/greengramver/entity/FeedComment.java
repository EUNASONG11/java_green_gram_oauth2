package com.green.greengramver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FeedComment extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedCommentId;

    @ManyToOne(fetch = FetchType.LAZY) //default : (fetch = FetchType.EAGER)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(length = 150, nullable = false)
    private String comment;
}
