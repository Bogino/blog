package main.model;

import jdk.jfr.DataAmount;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Data
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "is_moderator")
    private boolean isModerator;
    @Column(name = "reg_time")
    private Date regTime;
    private String name;
    private String email;
    private String password;
    private String code;
    private String photo;




}
