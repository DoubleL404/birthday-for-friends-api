package com.doublel401.java.bff.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"action", "resource"})
})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "resource", nullable = false)
    private String resource;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}
