package com.example.pinboard.account.domain.model;

import jakarta.persistence.*;
import lombok.*;

/*
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "user")
@ToString(exclude = {"id"})*/
public class UserModel {
    @Id
    @Column(name = "USER_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
