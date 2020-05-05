package com.accenture.bootcamp.thymeleaf;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByEmailContaining(String email);

    List<User> findAll();
}
