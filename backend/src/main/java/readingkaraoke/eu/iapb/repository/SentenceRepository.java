package readingkaraoke.eu.iapb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import readingkaraoke.eu.iapb.entity.Sentence;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    Sentence findById(long id);
    List<Sentence> findBydifficulty(int difficulty);


}
