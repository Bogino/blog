package main.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT * FROM POSTS WHERE IS_ACTIVE = 1 AND MODERATION_STATUS = 'ACCEPTED' ORDER BY TIME DESC", nativeQuery = true)
    Page<Post> getRecentPosts(Pageable pageable);

    @Query(value = "SELECT * FROM POSTS WHERE IS_ACTIVE = 1 AND MODERATION_STATUS = 'ACCEPTED' ORDER BY TIME ASC", nativeQuery = true)
    Page<Post> getEarlyPosts(Pageable pageable);


}
