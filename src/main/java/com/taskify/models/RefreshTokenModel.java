package com.taskify.models;

import com.taskify.constants.ModelConstants;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = ModelConstants.REFRESH_TOKEN_TABLE)
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String refreshToken;

    private Instant expiryDate;

}
