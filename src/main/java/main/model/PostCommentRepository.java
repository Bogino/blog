package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostVote, Integer> {


    @Query(value = "SELECT COUNT(*) FROM POST_COMMENTS WHERE POST_ID = ?1", nativeQuery = true)
    int findByUserId(int postId);

}