package com.shopping.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_roles")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

}