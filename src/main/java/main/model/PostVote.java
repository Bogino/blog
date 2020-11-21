package main.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(name = "post_id")
    private int postId;

    private Date time;

    private int value;


}
