package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EditProfileRequest {

    private MultipartFile photo;
    private String name;
    @JsonProperty("e_mail")
    private String email;
    private String password;
    private int removePhoto;


}
