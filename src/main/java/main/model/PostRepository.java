package main.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT * FROM POSTS WHERE IS_ACTIVE = 1 AND MODERATION_STATUS = 'ACCEPTED' AND TIME <= NOW() ORDER BY TIME DESC", nativeQuery = true)
    Page<Post> getRecentPosts(Pageable pageable);

    @Query(value = "SELECT * FROM POSTS WHERE IS_ACTIVE = 1 AND MODERATION_STATUS = 'ACCEPTED' AND TIME <= NOW() ORDER BY TIME ASC ", nativeQuery = true)
    Page<Post> getEarlyPosts(Pageable pageable);

    @Query(value = "SELECT * FROM POSTS WHERE title LIKE %?1% AND TIME <= NOW()", nativeQuery = true)
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    @Query(value = "SELECT * FROM POSTS WHERE time LIKE %?1% AND TIME <= NOW()", nativeQuery = true)
    Page<Post> findByDate(String time, Pageable pageable);

    @Query(value = "SELECT * FROM posts \n" +
            "JOIN tag2post ON posts.id = tag2post.post_id\n" +
            "JOIN tags ON tags.id = tag2post.tag_id\n" +
            "WHERE tags.name = ?1 AND TIME <= NOW()", nativeQuery = true)
    Page<Post> findByTagName(String name, Pageable pageable);

    @Query(value = "SELECT * FROM POSTS WHERE IS_ACTIVE = 1 AND MODERATION_STATUS = ?1 AND TIME <= NOW()", nativeQuery = true)
    Page<Post> getActivePosts(String status, Pageable pageable);

    @Query(value = "SELECT * FROM POSTS WHERE IS_ACTIVE = 0 AND TIME <= NOW()", nativeQuery = true)
    Page<Post> getInActivePosts(Pageable pageable);



}
