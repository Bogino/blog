package main.service;

import main.api.request.ChangePasswordRequest;
import main.api.request.EmailRestoreRequest;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.CaptchaImageResponse;
import main.api.response.LoginResponse;
import main.api.response.Result;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    LoginResponse getLoginResponse(String email);

    CaptchaImageResponse getCaptcha();

    Result register(RegisterRequest request);

    LoginResponse login(LoginRequest loginRequest);

    Result restore(EmailRestoreRequest request);

    Result changePassword(ChangePasswordRequest request);

    Result updateProfile(String email,
                         String name,
                         String password,
                         int removePhoto);

    Result getPostProfileMy(MultipartFile photo,
                            String email,
                            String name,
                            String password,
                            int removePhoto);

    String uploadImage(MultipartFile image);
}
