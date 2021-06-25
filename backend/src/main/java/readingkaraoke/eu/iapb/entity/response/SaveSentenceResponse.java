package readingkaraoke.eu.iapb.entity.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveSentenceResponse {
    private long id;
    String recordedAudioUrl;
    String textgridFileUrl;

    String textAsSyllables;
}
