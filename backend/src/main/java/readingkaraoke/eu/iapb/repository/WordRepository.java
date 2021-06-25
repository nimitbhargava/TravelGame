package readingkaraoke.eu.iapb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import readingkaraoke.eu.iapb.entity.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
}
