package com.green.greengramver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Feed extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    @ManyToOne // Many는 Feed, One은 User
    @JoinColumn(name = "writer_user_id", nullable = false)
    private User writerUserId;

    @Column(length = 1_000)
    private String contents;

    @Column(length = 30)
    private String location;
}
