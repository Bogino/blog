package main.model;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active")
    private int isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status")
    private ModerationStatus status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderatorId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    private Date time;

    private String title;

    @Column(columnDefinition = "Text")
    private String text;

    @Column(name = "view_count")
    private int viewCount;


    @OneToMany(mappedBy = "post")
    Set<Tag2Post> tag2PostSet = new HashSet<>();


    public void setViewCount(int viewCount) {
        this.viewCount += viewCount;
    }
}
