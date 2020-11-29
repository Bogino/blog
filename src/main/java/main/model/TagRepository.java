package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>{

    @Query("select t from Tag t where t.name like %?1%")
    List<Tag> findByNameContaining(String name);


}
