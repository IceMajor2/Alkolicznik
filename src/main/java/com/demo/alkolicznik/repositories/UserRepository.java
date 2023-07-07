package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String username);
}
