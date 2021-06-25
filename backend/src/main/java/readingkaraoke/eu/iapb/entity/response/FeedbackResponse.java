package readingkaraoke.eu.iapb.entity.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackResponse {

    private double averageTotalSyllables;
    private double averageTotalDifficulty;
    private Set<FeedbackStatistics> feedbackStatistics;
}
