package main.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Table(name = "posts")
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active")
    private int isActive;

    @Enumerated(EnumType.STRING)
    private ModerationStatus status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderatorId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    private Date time;

    private String title;

    @ManyToMany
    @JoinTable (name="tag2post",
            joinColumns=@JoinColumn (name="post_id"),
            inverseJoinColumns=@JoinColumn(name="tag_id"))
    private Set<Tag> tags;

    private String text;

    @Column(name = "view_count")
    private int viewCount;


}
