package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("select t from Tag t where t.name like %?1%")
    List<Tag> findByNameContaining(String name);

    @Query(value = "SELECT name FROM tags \n" +
            "JOIN tag2post ON tag2post.tag_id = tags.id\n" +
            "JOIN posts ON posts.id = tag2post.post_id\n" +
            "WHERE posts.id = ?1", nativeQuery = true)
    List<String> findByPostId(int postId);


}
