package org.wierzbadev.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wierzbadev.userservice.model.BannedUser;

@Repository
public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {

    boolean existsByEmail(String email);
}
