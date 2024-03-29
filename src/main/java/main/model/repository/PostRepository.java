package main.model.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {


    @Query(value = "SELECT * FROM posts WHERE title LIKE %?1% AND time_post <= NOW()  AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE time LIKE %?1% AND time_post <= NOW() AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    Page<Post> findByDate(String time, Pageable pageable);

    @Query(value = "SELECT * FROM posts \n" +
            "JOIN posts_tags ON posts.id = posts_tags.posts_id\n" +
            "JOIN tags ON tags.id = posts_tags.tags_id\n" +
            "WHERE tags.name = ?1 AND posts.time_post <= NOW() AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    Page<Post> findByTagName(String name, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = ?1 AND time_post <= NOW()", nativeQuery = true)
    Page<Post> getActivePosts(String status, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = 'NEW' AND time_post <= NOW()", nativeQuery = true)
    List<Post> getNewPosts();

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 0 AND time_post <= NOW()", nativeQuery = true)
    Page<Post> getInActivePosts(Pageable pageable);


    @Query(value = "SELECT COUNT(*) FROM posts\n" +
            "JOIN posts_tags ON posts.id = posts_tags.posts_id\n" +
            "JOIN tags ON tags.id = posts_tags.tags_id\n" +
            "WHERE tags.name = ?1", nativeQuery = true)
    int getCountPostsByTagName(String name);


    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = 'ACCEPTED' AND time_post <= NOW()", nativeQuery = true)
    ArrayList<Post> getAllAcceptedPosts();

    @Query(value = "SELECT COUNT(*) FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = 'ACCEPTED' AND time_post <= NOW()", nativeQuery = true)
    int getCountAcceptedPosts();

    @Query(value = "SELECT time_post FROM posts WHERE YEAR(time_post) = ?1 AND moderation_status = 'ACCEPTED' AND time_post <= NOW()", nativeQuery = true)
    ArrayList<Date> getDatesByYear(String year);

    @Query(value = "SELECT distinct year(time_post) FROM posts WHERE moderation_status = 'ACCEPTED' AND time_post <= NOW()", nativeQuery = true)
    ArrayList<Integer> getYearsOfPosts();

    @Query(value = "SELECT COUNT(*) FROM posts WHERE time_post = ?1 AND moderation_status = 'ACCEPTED' AND time_post <= NOW()", nativeQuery = true)
    int getCountPostsByDate(Date date);

    @Query(value = "SELECT * FROM posts WHERE id = ?1 AND IS_ACTIVE = 1 AND moderation_status = 'ACCEPTED' AND time_post <= NOW()", nativeQuery = true)
    Optional<Post> findByIdAcceptedPost(int id);

    @Query(value = "SELECT user_id FROM posts WHERE id = ?1", nativeQuery = true)
    int findUserIdByPostId(int postId);

    @Query(value = "SELECT * FROM posts WHERE id = ?1 AND is_active = 1", nativeQuery = true)
    Optional<Post> findByPostId(int postId);

    @Query(value = "SELECT * FROM posts WHERE user_id = ?1 AND is_active = 1 AND moderation_status = 'ACCEPTED' ORDER BY time_post ASC", nativeQuery = true)
    List<Post> findPostsByUserId(int userId);

    @Query(value = "SELECT * FROM posts\n" +
            "JOIN post_comments ON posts.id = post_comments.post_id\n" +
            "WHERE moderation_status = 'ACCEPTED'\n" +
            "GROUP BY post_comments.post_id\n" +
            "ORDER BY count(*) DESC", nativeQuery = true)
    Page<Post> getPopularPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 0 AND time_post <= NOW() AND user_id = ?1", nativeQuery = true)
    Page<Post> getMyInActivePosts(Pageable pageable, int userId);

    @Query(value = "SELECT * FROM posts WHERE IS_ACTIVE = 1 AND moderation_status = ?1 AND time_post <= NOW() AND user_id = ?2", nativeQuery = true)
    Page<Post> getMyActivePosts(String status, Pageable pageable, int userId);

    @Query(value = "SELECT * FROM posts\n" +
            "JOIN post_votes ON posts.id = post_votes.post_id\n" +
            "WHERE moderation_status = 'ACCEPTED' AND post_votes.value > 0\n" +
            "GROUP BY post_votes.post_id\n" +
            "ORDER BY count(*) DESC", nativeQuery = true)
    Page<Post> getBestPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE moderation_status = 'ACCEPTED' ORDER BY time_post ASC", nativeQuery = true)
    Optional<List<Post>> getEarlyPosts();
}
