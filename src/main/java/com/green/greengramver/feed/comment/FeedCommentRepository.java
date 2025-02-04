package com.green.greengramver.feed.comment;

import com.green.greengramver.entity.FeedComment;
import com.green.greengramver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    Optional<FeedComment> findByFeedCommentIdAndUserId(Long feedCommentId, User userId);
}
