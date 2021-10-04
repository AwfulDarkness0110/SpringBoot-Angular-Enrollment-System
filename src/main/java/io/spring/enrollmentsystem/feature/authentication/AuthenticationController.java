package io.spring.enrollmentsystem.feature.authentication;

import com.fasterxml.jackson.annotation.JsonView;
import io.spring.enrollmentsystem.common.view.BaseView;
import io.spring.enrollmentsystem.feature.user.UserDto;
import io.spring.enrollmentsystem.feature.user.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController @RequestMapping("/api/v1/auth")
@Tag(name = "authentication", description = "authentication API")
@RequiredArgsConstructor @Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login a user by username and password", tags = "authentication")
    @JsonView(BaseView.Medium.class)
    public ResponseEntity<UserDto> login(@JsonView(UserView.LoginRequest.class)
                                         @RequestBody @Valid UserDto loginRequest,
                                         HttpServletResponse response) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        return ResponseEntity
                .ok()
                .body(authenticationService.login(response, username, password));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Logout a user by access token", tags = "authentication")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/refresh")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Refresh authentication tokens by refresh token", tags = "authentication")
    public ResponseEntity<Void> refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.refresh(request, response);
        return ResponseEntity.ok().build();
    }
}