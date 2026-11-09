package airline.repository;

import airline.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import airline.entity.enums.UserRole;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  List<User> findByNameContaining(String name);

  List<User> findByRole(UserRole role);

  boolean existsByUsername(String username);
  boolean existsByUserEmail(String userEmail);
  
}
