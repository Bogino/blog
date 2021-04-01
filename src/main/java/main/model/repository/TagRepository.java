package main.model.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("select t from Tag t where t.name like %?1%")
    List<Tag> findByNameContaining(String name);

    @Query(value = "SELECT name FROM tags \n" +
            "JOIN posts_tags ON posts_tags.tags_id = tags.id\n" +
            "JOIN posts ON posts.id = posts_tags.posts_id\n" +
            "WHERE posts.id = ?1", nativeQuery = true)
    List<String> findByPostId(int postId);

    @Query(value = "SELECT * FROM tags t ORDER BY (SELECT COUNT(*) FROM posts_tags pt WHERE pt.tags_id = t.id) DESC LIMIT 1", nativeQuery = true)
    Optional<Tag> getTagWithMaxPostsCount();


    @Query(value = "SELECT * FROM tags WHERE name = ?1", nativeQuery = true)
    Tag findByName(String name);
}
