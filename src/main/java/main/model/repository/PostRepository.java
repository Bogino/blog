package main.model.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = 'ACCEPTED' AND time <= NOW() ORDER BY time DESC", nativeQuery = true)
    Page<Post> getRecentPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = 'ACCEPTED' AND time <= NOW() ORDER BY time ASC ", nativeQuery = true)
    Page<Post> getEarlyPosts(Pageable pageable);


    @Query(value = "SELECT * FROM posts WHERE title LIKE %?1% AND time <= NOW()  AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE time LIKE %?1% AND time <= NOW() AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    Page<Post> findByDate(String time, Pageable pageable);

    @Query(value = "SELECT * FROM posts \n" +
            "JOIN tag2post ON posts.id = tag2post.post_id\n" +
            "JOIN tags ON tags.id = tag2post.tag_id\n" +
            "WHERE tags.name = ?1 AND time <= NOW() AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    Page<Post> findByTagName(String name, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = ?1 AND time <= NOW()", nativeQuery = true)
    Page<Post> getActivePosts(String status, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 0 AND time <= NOW()", nativeQuery = true)
    Page<Post> getInActivePosts(Pageable pageable);


    @Query(value = "SELECT COUNT(*) FROM posts\n" +
            "JOIN tag2post ON posts.id = tag2post.post_id\n" +
            "JOIN tags ON tags.id = tag2post.tag_id\n" +
            "WHERE tags.name = ?1", nativeQuery = true)
    int getCountPostsByTagName(String name);


    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = 'ACCEPTED' AND time <= NOW()", nativeQuery = true)
    List<Post> getAllAcceptedPosts();

    @Query(value = "SELECT time FROM posts WHERE YEAR(time) = ?1 AND moderation_status = 'ACCEPTED' AND time <= NOW()", nativeQuery = true)
    List<Date> getDatesByYear(String year);

    @Query(value = "SELECT distinct year(time) FROM posts WHERE moderation_status = 'ACCEPTED' AND time <= NOW()", nativeQuery = true)
    List<Integer> getYearsOfPosts();

    @Query(value = "SELECT COUNT(*) FROM posts WHERE time = ?1 AND moderation_status = 'ACCEPTED' AND time <= NOW()", nativeQuery = true)
    int getCountPostsByDate(Date date);

    @Query(value = "SELECT time FROM posts WHERE moderation_status = 'ACCEPTED' AND time <= NOW()", nativeQuery = true)
    List<Date> getDates();


}
