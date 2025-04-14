package edu.gcc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<DBUser,Long> {
    DBUser findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUid(Long uid);
    void deleteByUid(Long uid);
}
