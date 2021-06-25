package readingkaraoke.eu.iapb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sentences")
public class Sentence {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String text;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "word_id")
    private List<Word> words;

    @OneToOne
    private User author;

    private String audioFilename;

    private String textFilename;

    private String textgridFilename;

    // 0 = easiest, 1=medium, 2 = hardest. Custom groups start at 3. Null when no group.
    private Integer difficulty;


    @Transient
    private UUID userToken;

    @JsonIgnore
    @Transient
    private MultipartFile audio;

}
