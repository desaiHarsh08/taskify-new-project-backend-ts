package com.taskify.controllers;

import java.io.IOException;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.taskify.security.CustomOAuth2FailureHandler;
// import com.taskify.security.CustomOAuth2SuccessHandler;

import jakarta.servlet.ServletException;

@RestController
public class OAuthController {

    @GetMapping("/auth/code/google")
    public void handleCallback(@RequestParam String state, @RequestParam String code, @RequestParam String scope,
            @RequestParam String authuser, String prompt)
            throws IOException, ServletException {

                System.out.println(state);
                System.out.println(code);
                System.out.println(scope);
                System.out.println(authuser);
                System.out.println(prompt);
        // Get the authentication object
        // OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken)
        // authentication;
        // System.out.println("authToken: " + authToken);

        // System.out.println("in /auth/callback -> " + request);

        // // Determine if authentication is successful
        // if (authentication != null && authentication.isAuthenticated()) {
        // // Redirect to success URL
        // this.oAuth2SuccessHandler.onAuthenticationSuccess(request, response,
        // authentication);
        // } else {
        // // Redirect to error URL
        // this.oAuth2FailureHandler.onAuthenticationFailure(request, response, null);
        // }
    }
}
