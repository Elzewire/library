package it.raccoon.library.repositories;

import it.raccoon.library.domain.LibUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<LibUser, Long> {
    LibUser findByUsername(String username);
}
