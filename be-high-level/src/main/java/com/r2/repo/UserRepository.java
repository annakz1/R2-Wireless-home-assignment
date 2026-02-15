package com.r2.repo;
 
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.r2.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}

