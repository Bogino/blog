package main.controller;

import main.api.request.ChangePasswordRequest;
import main.api.request.EmailRestoreRequest;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.LoginResponse;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {


    private final AuthenticationManager authenticationManager;


    private final UserService userService;


    @Autowired
    public ApiAuthController(AuthenticationManager authenticationManager, UserService userService) {

        this.authenticationManager = authenticationManager;

        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();


        return ResponseEntity.ok(userService.getLoginResponse(user.getUsername()));
    }


    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {

        if (principal == null)
            return ResponseEntity.ok(new LoginResponse());

        return ResponseEntity.ok(userService.getLoginResponse(principal.getName()));
    }


    @GetMapping("/captcha")
    public ResponseEntity getCaptcha() {

        return new ResponseEntity(userService.getCaptcha(), HttpStatus.OK);

    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequest request) {


        return new ResponseEntity(userService.register(request), HttpStatus.OK);
    }


    @ResponseBody
    @PostMapping("/restore")
    public ResponseEntity restore(@RequestBody EmailRestoreRequest request) {

        return ResponseEntity.ok(userService.restore(request));

    }

    @PostMapping("/password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest request) {

        return ResponseEntity.ok(userService.changePassword(request));
    }

}