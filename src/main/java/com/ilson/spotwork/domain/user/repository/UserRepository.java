package com.ilson.spotwork.domain.user.repository;

import com.ilson.spotwork.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
