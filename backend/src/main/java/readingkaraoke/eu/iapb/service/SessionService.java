package readingkaraoke.eu.iapb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import readingkaraoke.eu.iapb.entity.Session;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.entity.response.SessionResponse;
import readingkaraoke.eu.iapb.exception.SessionException;
import readingkaraoke.eu.iapb.repository.SessionRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

@Service
public class SessionService {

    @Autowired
    SessionRepository sessionRepository;

    private static final long EXTEND_SESSION_IN_MILLISECONDS = 1000 * 60 * 60 * 9;

    /**
     * Extends session on use.
     */
    private void extendSession(Session session) {
        session.setValidUntil(new Timestamp(session.getValidUntil().getTime() + EXTEND_SESSION_IN_MILLISECONDS));
        sessionRepository.save(session);
    }

    private Session getSessionByUUID(UUID uuid) throws SessionException {
        Session session = sessionRepository.findByUuid(uuid);
        if(session == null) {
            throw new SessionException("Sessioon puudub.");
        }
        if(session.getValidUntil() == null || session.getValidUntil().before(Calendar.getInstance().getTime())) {
            throw new SessionException("Sessioon aegunud.");
        }
        return session;
    }

    public Session getSession(UUID sessionId) throws SessionException {
        Session session = getSessionByUUID(sessionId);
        extendSession(session); // extend valid session with each new request
        return session;
    }

    public void endSession(UUID sessionId) throws SessionException {
        Session session = sessionRepository.findByUuid(sessionId);
        if(session == null) {
            throw new SessionException("Sessioon puudub.");
        }
        session.setValidUntil(null);
    }

    @Transactional
    public SessionResponse startNewSession(User user) {
        Session session = new Session();
        session.setUser(user);
        session.setValidUntil(new Timestamp(System.currentTimeMillis() + EXTEND_SESSION_IN_MILLISECONDS));
        session = sessionRepository.save(session);

        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setSession(session);
        sessionResponse.setUser(user);
        return sessionResponse;
    }

    public User getUserBySession(UUID userToken) throws SessionException {
        return getSession(userToken).getUser();
    }
}
