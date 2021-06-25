package readingkaraoke.eu.iapb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import readingkaraoke.eu.iapb.entity.Recording;
import readingkaraoke.eu.iapb.entity.Session;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.entity.response.FeedbackResponse;
import readingkaraoke.eu.iapb.entity.response.FeedbackStatistics;
import readingkaraoke.eu.iapb.exception.SessionException;
import readingkaraoke.eu.iapb.repository.RecordingRepository;
import readingkaraoke.eu.iapb.repository.SessionRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackService {

    @Autowired
    private RecordingRepository recordingRepository;

    @Autowired
    private SessionRepository sessionRepository;

    private double getAvgFromDoubleList(List<Double> doubleList) {
        return doubleList.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    private void calculateAverageSyllablesAndDifficulty(FeedbackStatistics feedbackStatistics, List<Recording> sessionRecordings) {
        List<Double> difficultiesUsed = new ArrayList<>();
        List<Double> syllablesPerSecondList = new ArrayList<>();
        for (Recording sessionRecording : sessionRecordings) {
            difficultiesUsed.add(Double.valueOf(sessionRecording.getSentence().getDifficulty()));
            syllablesPerSecondList.add(sessionRecording.getSyllablesPerSecond());
        }

        feedbackStatistics.setAverageDifficulty(getAvgFromDoubleList(difficultiesUsed));
        feedbackStatistics.setAverageSyllablesPerSec(getAvgFromDoubleList(syllablesPerSecondList));
    }

    private void calculateTotalAverages(FeedbackResponse feedbackResponse, Map<FeedbackStatistics, List<Recording>> feedbackMap) {
        List<Double> difficultiesUsed = new ArrayList<>();
        List<Double> syllablesPerSecondList = new ArrayList<>();

        for (Map.Entry<FeedbackStatistics, List<Recording>> feedbackEntry : feedbackMap.entrySet()) {
            difficultiesUsed.add(feedbackEntry.getKey().getAverageDifficulty());
            syllablesPerSecondList.add(feedbackEntry.getKey().getAverageSyllablesPerSec());
        }

        feedbackResponse.setAverageTotalSyllables(getAvgFromDoubleList(syllablesPerSecondList));
        feedbackResponse.setAverageTotalDifficulty(getAvgFromDoubleList(difficultiesUsed));
    }


    public FeedbackResponse getUserFeedback(User user) throws SessionException {
        List<Session> userSessions = sessionRepository.findAllByUserOrderByCreateDateAsc(user);
        if(userSessions == null) {
            throw new SessionException("Sessioonid puuduvad.");
        }

        Map<FeedbackStatistics, List<Recording>> statisticsToRecording = new LinkedHashMap<>();

        for (Session userSession : userSessions) {
            List<Recording> sessionRecordings = recordingRepository.findAllBySessionToken(userSession.getUuid());
            if(sessionRecordings != null && sessionRecordings.size() > 0) {
                FeedbackStatistics feedbackStatistics = new FeedbackStatistics();
                feedbackStatistics.setSessionDate(userSession.getCreateDate());
                calculateAverageSyllablesAndDifficulty(feedbackStatistics, sessionRecordings);
                statisticsToRecording.put(feedbackStatistics, sessionRecordings);
            }
        }
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setFeedbackStatistics(statisticsToRecording.keySet());
        calculateTotalAverages(feedbackResponse, statisticsToRecording);

        return feedbackResponse;
    }
}
