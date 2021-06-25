package readingkaraoke.eu.iapb.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // Sentence display time in milliseconds
    private int time;

    private String word;

    private String wordAsSyllables;

    private int nrOfSyllables;
}
