package main.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date time;

    private String code;

    @Column(name = "secret_code")
    private String secretCode;


}
