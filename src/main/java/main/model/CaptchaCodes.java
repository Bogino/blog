package main.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "captcha_time")
    private LocalDateTime time;

    private String code;

    @Column(name = "secret_code")
    private String secretCode;


}
