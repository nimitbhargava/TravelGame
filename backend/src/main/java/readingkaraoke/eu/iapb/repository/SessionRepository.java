package readingkaraoke.eu.iapb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import readingkaraoke.eu.iapb.entity.Session;
import readingkaraoke.eu.iapb.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository  extends JpaRepository<Session, Long> {
    Session findByUuid(UUID uuid);
    List<Session> findAllByUserOrderByCreateDateAsc(User user);

}
