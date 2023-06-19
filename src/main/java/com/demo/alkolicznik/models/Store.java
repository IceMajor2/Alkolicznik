package com.demo.alkolicznik.models;

import jakarta.persistence.*;

import java.util.Set;

@Table(name = "stores")
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private Set<Beer> beers;
}
