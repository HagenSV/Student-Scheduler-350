package edu.gcc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<dbUser,String> {
    dbUser findByUsername(String name);
    boolean existsByUsername(String name);
    boolean existsByUid(String uid);
    void deleteByUid(String uid);
}
