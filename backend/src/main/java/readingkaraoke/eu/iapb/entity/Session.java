package readingkaraoke.eu.iapb.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @Column(name = "id")
    @JsonProperty("token")
    private UUID uuid = UUID.randomUUID();

    @JsonIgnore
    @ManyToOne
    @NotNull
    private User user;

    @Temporal(TIMESTAMP)
    @NotNull
    private Date createDate;

    private Timestamp validUntil;


    @PrePersist
    protected void onCreate() {
        createDate = new Date();
    }
}
