package main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @GetMapping("/check")
    private ResponseEntity isAuthorized(){
        return new ResponseEntity(false, HttpStatus.OK);
    }

//    @PostMapping("/register")
//    private ResponseEntity register(){
//
//    }


}
