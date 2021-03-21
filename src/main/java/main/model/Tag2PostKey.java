package main.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class Tag2PostKey implements Serializable {

    @Column(name = "post_id")
    private int postId;

    @Column(name = "tag_id")
    private int tagId;




}
