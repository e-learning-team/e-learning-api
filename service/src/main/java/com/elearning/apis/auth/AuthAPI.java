package com.elearning.apis.auth;

import com.elearning.controller.JwtController;
import com.elearning.controller.RefreshTokenController;
import com.elearning.controller.UserController;
import com.elearning.controller.VerificationCodeController;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.RefreshTokenDTO;
import com.elearning.models.dtos.UserEmailRequest;
import com.elearning.models.dtos.auth.AuthResponse;
import com.elearning.models.dtos.auth.UserLoginDTO;
import com.elearning.models.dtos.auth.UserRegisterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.SignatureException;

import static com.elearning.utils.Constants.REFRESH_TOKEN_COOKIE_NAME;
import static com.elearning.utils.Constants.REFRESH_TOKEN_EXPIRE_TIME_MILLIS;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthAPI {
    private final UserController userController;
    @Autowired
    private VerificationCodeController verificationCodeController;
    private final JwtController jwtController;
    @Autowired
    private RefreshTokenController refreshTokenController;

    public AuthAPI(UserController userController, JwtController jwtController) {
        this.userController = userController;
        this.jwtController = jwtController;
    }

    @Operation(summary = "Gửi mã xác nhận email")
    @PostMapping("/email/verify")
    public ResponseEntity<?> sendEmailVerification(@RequestBody UserEmailRequest request) {
        return ResponseEntity.ok().body(verificationCodeController.createEmailConfirmCode(request.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userFormDTO) throws ServiceException {
        var authResponse = userController.register(userFormDTO);
        var cookie = createRefreshCookie(authResponse.getRefreshToken());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse);
    }
    @PostMapping("/register/mobile/{email}/{code}")
    public ResponseEntity<?> verifyEmail(@PathVariable(value = "email") String email, @PathVariable(value = "code") String code) {
        return ResponseEntity.ok().body(verificationCodeController.checkEmailConfirmCode(email, code));
    }

    @PostMapping("/register/mobile")
    public ResponseEntity<?> registerMobile(@Valid @RequestBody UserRegisterDTO userFormDTO) throws ServiceException {
        var authResponse = userController.register(userFormDTO);
        return ResponseEntity.ok().body(authResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userFormDTO) throws ServiceException {
        AuthResponse rs = userController.login(userFormDTO);
        var cookie = createRefreshCookie(rs.getRefreshToken());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(rs);
    }

    @PostMapping("/login/mobile")
    public ResponseEntity<?> loginMobile(@Valid @RequestBody UserLoginDTO userFormDTO) throws ServiceException {
        AuthResponse rs = userController.login(userFormDTO);
        return ResponseEntity.ok().body(rs);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws SignatureException {
        Cookie requestCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        AuthResponse authResponse = jwtController.refreshToken(requestCookie);
        var cookie = createRefreshCookie(authResponse.getRefreshToken());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse);
    }

    @PostMapping("/refresh-token/mobile")
    public ResponseEntity<?> refreshTokenMobile(@RequestBody RefreshTokenDTO refreshToken) throws SignatureException {
        System.out.println("refreshToken------------------" + refreshToken);
        AuthResponse authResponse = jwtController.refreshToken(refreshToken.getRefreshToken());
        return ResponseEntity.ok().body(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws SignatureException {
        Cookie requestCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        if (requestCookie != null) {
            refreshTokenController.deleteRefreshTokenBranch(requestCookie.getValue());
            Cookie delete = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
            delete.setHttpOnly(true);
            delete.setPath("/");
            delete.setMaxAge(0);
            response.addCookie(delete);

            SecurityContextHolder.clearContext();

        }
        return ResponseEntity.ok()
                .body(null);
    }

    @PostMapping("/logout/mobile")
    public ResponseEntity<?> logoutMobile(@RequestBody String refreshToken) throws SignatureException {
        refreshTokenController.deleteRefreshTokenBranch(refreshToken);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body(null);
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME_MILLIS / 1000)
                .path("/")
                .httpOnly(true).build();
    }

}
