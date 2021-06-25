package readingkaraoke.eu.iapb.entity.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import readingkaraoke.eu.iapb.entity.Session;
import readingkaraoke.eu.iapb.entity.User;

@Getter
@Setter
@NoArgsConstructor
public class SessionResponse {
    private Session session;
    private User user;
}
