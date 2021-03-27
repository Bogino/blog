package main.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "tag2post",
//            joinColumns = {@JoinColumn(name = "tag_id")},
//            inverseJoinColumns = {@JoinColumn(name = "post_id")})
//    private Set<Post> tag2Posts = new HashSet<>();

    @OneToMany(mappedBy = "tag")
    Set<Tag2Post> tag2PostSet = new HashSet<>();

}
