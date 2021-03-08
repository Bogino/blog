package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ChangePasswordRequest {


    private String password;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

    private String code;
}
