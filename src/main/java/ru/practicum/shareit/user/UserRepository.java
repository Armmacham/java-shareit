package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories(basePackages = "ru.practicum")
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByEmail(String email);
}
