package readingkaraoke.eu.iapb.entity.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackStatistics {
    private double averageSyllablesPerSec;
    private double averageDifficulty;

    private Date sessionDate;
}
