package com.taskify.controllers;

import com.taskify.dtos.UserDto;
import com.taskify.models.RefreshTokenModel;
import com.taskify.security.JwtTokenHelper;
import com.taskify.services.RefreshTokenServices;
import com.taskify.services.UserServices;
import com.taskify.utils.AuthRequest;
import com.taskify.utils.AuthResponse;
import com.taskify.utils.ErrorMessage;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserServices userServices;

    @Autowired
    private RefreshTokenServices refreshTokenServices;

    @PostMapping("")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        UserDto createdUserDto = this.userServices.createUser(userDto);

        if (createdUserDto != null) {
            return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create the user"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        if (authRequest.getEmail().isEmpty() || authRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Please provide the valid credentials!");
        }

        System.out.println(authRequest);

        this.authenticateUser(authRequest.getEmail(), authRequest.getPassword());

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authRequest.getEmail());

        UserDto userDto = this.userServices.getUserByEmail(authRequest.getEmail());

        String accessToken = this.jwtTokenHelper.generateToken(userDetails);
        String refreshToken = this.refreshTokenServices.createRefreshToken(authRequest.getEmail()).getRefreshToken();
        System.out.println("refreshToken: " + refreshToken);

        // Set the `email` and `refreshToken` inside the cookies
        Cookie emailCookie = new Cookie("email", authRequest.getEmail());
        emailCookie.setHttpOnly(true);
        // emailCookie.setSecure(true); // Use true if you're using HTTPS
        emailCookie.setPath("/");
        emailCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days expiration

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true); // Use true if you're using HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days expiration

        // Add cookies to the response
        response.addCookie(emailCookie);
        response.addCookie(refreshTokenCookie);

        response.addCookie(emailCookie);
        response.addCookie(refreshTokenCookie);

        // Log the cookies manually since HttpServletResponse does not provide a method
        // to retrieve them
        System.out.println("Cookie added: email = " + emailCookie.getValue());
        System.out.println("Cookie added: refreshToken = " + refreshTokenCookie.getValue());

        AuthResponse authResponse = new AuthResponse(accessToken, userDto);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private void authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        this.authenticationManager.authenticate(authenticationToken);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Fetch the `email` and `refreshToken` from the cookies
        String email = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("email")) {
                    email = cookie.getValue();
                } else if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (email == null || refreshToken == null) {
            throw new SecurityException("Security Exception... Please try to login again!");
        }

        // Verify the refresh token
        RefreshTokenModel refreshTokenModel = this.refreshTokenServices.verifyRefreshToken(refreshToken);

        if (!refreshTokenModel.getEmail().equals(email)) {
            throw new SecurityException("Security Exception... Please try to login again!");
        }

        // Generate new access token
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        String accessToken = this.jwtTokenHelper.generateToken(userDetails);

        return new ResponseEntity<>(new AuthResponse(accessToken, this.userServices.getUserByEmail(email)),
                HttpStatus.OK);

    }

}
