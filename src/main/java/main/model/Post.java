package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    @OneToMany(mappedBy = "postId")
    private Set<PostVote> votes;

    @OneToMany(mappedBy = "postId")
    private Set<PostComment> comments = new HashSet<>();

    private LocalDateTime time;

    private String title;

    @Column(columnDefinition = "Text")
    private String text;

    @Column(name = "view_count")
    private int viewCount;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "posts_tags",
            joinColumns = {@JoinColumn(name = "posts_id")},
            inverseJoinColumns = {@JoinColumn(name = "tags_id")}
    )
    Set<Tag> tags = new HashSet<>();


    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public ModerationStatus getStatus() {
        return status;
    }

    public void setStatus(ModerationStatus status) {
        this.status = status;
    }

    public User getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(User moderatorId) {
        this.moderatorId = moderatorId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<PostVote> getVotes() {
        return votes;
    }

    public void setVotes(Set<PostVote> votes) {
        this.votes = votes;
    }

    public Set<PostComment> getComments() {
        return comments;
    }

    public void setComments(Set<PostComment> comments) {
        this.comments = comments;
    }
}
