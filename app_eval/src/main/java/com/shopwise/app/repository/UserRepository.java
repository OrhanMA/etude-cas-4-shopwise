package com.shopwise.app.repository;

import com.shopwise.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
