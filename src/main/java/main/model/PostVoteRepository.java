package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    @Query("select p from PostVote p where p.postId = :postId")
    List<PostVote> findByPostId(@Param("postId") Post postId);

}
