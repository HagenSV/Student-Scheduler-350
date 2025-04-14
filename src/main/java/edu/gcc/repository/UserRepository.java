package edu.gcc.repository;

import edu.gcc.dbUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<dbUser,String> {
    dbUser findByUsername(String name);
    boolean existsByUsername(String name);
}
