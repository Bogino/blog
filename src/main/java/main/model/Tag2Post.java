package main.model;

import javax.persistence.*;

@Entity
public class Tag2Post {

    @EmbeddedId
    private Tag2PostKey tag2PostKey;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;
}
