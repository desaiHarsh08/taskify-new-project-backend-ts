package com.taskify.services;

import com.taskify.models.RefreshTokenModel;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenServices {

    RefreshTokenModel createRefreshToken(String email);

    RefreshTokenModel verifyRefreshToken(String email);

}
