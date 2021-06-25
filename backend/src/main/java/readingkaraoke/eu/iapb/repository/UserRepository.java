package readingkaraoke.eu.iapb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import readingkaraoke.eu.iapb.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(long id);
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);

}
