package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class InitResponse {

    @Value("${blog.title}")
    private String title;

    @Value("${blog.subtitle}")
    private String subtitle;

    @Value("${blog.phone}")
    private String phone;

    @Value("${blog.email}")
    @JsonProperty("e_mail")
    private String email;

    @Value("${blog.copyright}")
    private String copyright;

    @Value("${blog.copyrightFrom}")
    private String copyrightFrom;


}

/*

blog.title: DevPub
blog.subtitle: Рассказы разработчиков
blog.phone: 8 999 777-44-33
blog.email: mail@mail.ru
blog.copyright: Михал Палыч
blog.copyrightFrom: 2020

**/