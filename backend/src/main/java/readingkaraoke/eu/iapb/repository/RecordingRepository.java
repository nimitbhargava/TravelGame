package readingkaraoke.eu.iapb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import readingkaraoke.eu.iapb.entity.Recording;
import readingkaraoke.eu.iapb.entity.Sentence;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecordingRepository  extends JpaRepository<Recording, Long> {
    Recording findById(long id);
    List<Recording> findBySentence(Sentence sentence);
    List<Recording> findAllBySessionToken(UUID sessionToken);
}
