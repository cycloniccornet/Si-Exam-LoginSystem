package si.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import si.login.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
