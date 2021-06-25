package readingkaraoke.eu.iapb.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "recordings")
public class Recording {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @OneToOne
    private Sentence sentence;

    @NotNull
    @OneToOne
    private User user;

    @NotNull
    private String recordingFileName;

    @NotNull
    private Double syllablesPerSecond;

    @Temporal(TIMESTAMP)
    @NotNull
    private Date createDate;

    private UUID sessionToken;

    @PrePersist
    protected void onCreate() {
        createDate = new Date();
    }

    @Transient
    private long sentenceId;

    @Transient
    private MultipartFile audioFile;

}
