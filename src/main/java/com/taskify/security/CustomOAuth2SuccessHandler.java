package com.taskify.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        // Custom processing
        // String userName = authToken.getPrincipal().getAttributes().get("name").toString();
        // String userEmail = authToken.getPrincipal().getAttributes().get("email").toString();

        System.out.println("success fired!");
        System.out.println(authToken.getPrincipal().getAttributes());

        // Redirect to frontend
        response.sendRedirect("http://localhost:5173/home");
    }
}