package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostVote, Integer> {

    @Query("select p from PostComment p where p.userId = :userId")
    List<PostComment> findByUserId(@Param("userId") User userId);

}