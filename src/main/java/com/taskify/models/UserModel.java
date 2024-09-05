package com.taskify.models;

import com.taskify.constants.Department;
import com.taskify.constants.ModelConstants;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = ModelConstants.USER_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String department = Department.QUOTATION.name();

    @Column(nullable = false)
    private String phone;

    private boolean isDisabled;

    private String profileImage;

}
