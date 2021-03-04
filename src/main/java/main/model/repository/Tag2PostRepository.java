package main.model.repository;

import main.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Tag2PostRepository extends JpaRepository<Tag2Post, Integer> {
}
