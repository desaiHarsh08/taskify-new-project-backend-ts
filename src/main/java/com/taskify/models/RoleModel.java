package com.taskify.models;

import com.taskify.constants.ModelConstants;
import com.taskify.constants.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = ModelConstants.ROLE_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "user_id_fk")
    private UserModel user;

    private String roleType = Role.MARKETING.name();

}
