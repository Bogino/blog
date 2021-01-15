package main.model.repository;

import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {


    @Query(value = "SELECT COUNT(*) FROM post_comments WHERE post_id = ?1", nativeQuery = true)
    int getCountCommentsByPostId(int postId);

    @Query(value = "SELECT * FROM post_comments WHERE post_id = ?1", nativeQuery = true)
    List<PostComment> getCommentsByPostId(int postId);

}