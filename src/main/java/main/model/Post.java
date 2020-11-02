package main.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

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
    @Column(columnDefinition = "status")
    private ModerationStatus status;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "moderator_id")
    private int moderatorId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    private Date time;
    private String title;
    private String text;
    @Column(name = "view_count")
    private int viewCount;



}
