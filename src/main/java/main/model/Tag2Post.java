package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tag2post")
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "tag_id")
    private int tagId;




}
