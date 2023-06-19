package com.demo.alkolicznik.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

@Entity
@Table(name = "beers")
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    private String name;

    public Beer(String name) {
        this.id = id;
        this.name = name;
    }

    public Beer() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof Beer)) {
            return false;
        }

        Beer compare = (Beer) obj;

        return compare.getId() == this.getId() && compare.getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return "%s (ID: %d)".formatted(this.name, this.id);
    }
}
