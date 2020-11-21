package main.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "parent_id")
    private int parentId;

    @Column(name = "post_id")
    private int postId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    private Date time;

    private String text;

}
