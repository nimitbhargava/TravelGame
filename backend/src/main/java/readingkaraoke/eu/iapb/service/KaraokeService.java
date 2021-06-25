package readingkaraoke.eu.iapb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import readingkaraoke.eu.iapb.entity.Recording;
import readingkaraoke.eu.iapb.entity.Sentence;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.exception.RecordingException;
import readingkaraoke.eu.iapb.exception.SentenceException;
import readingkaraoke.eu.iapb.repository.RecordingRepository;
import readingkaraoke.eu.iapb.repository.SentenceRepository;

import java.util.List;
import java.util.Random;

@Service
public class KaraokeService {

    private static final String AUDIO_TYPE = ".wav";

    @Autowired
    private SentenceRepository sentenceRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private RecordingRepository recordingRepository;

    public Sentence getRandomSentenceFromGroup(int groupId) {
        List<Sentence> sentences = sentenceRepository.findBydifficulty(groupId);
        Random rand = new Random();
        return sentences.get(rand.nextInt(sentences.size()));
    }

    public List<Sentence> getAllSentences() {
        return sentenceRepository.findAll();
    }

    private String genRecordingFilename(Recording recording) {
        return recording.getUser().getUsername() + String.valueOf(recording.getSentence().getId());
    }

    public Recording getKaraokeRecordingById(long id) throws RecordingException {
        Recording recording = recordingRepository.findById(id);
        if(recording == null) {
            throw new RecordingException("Selle id-ga lindistus puudub");
        }
        return recording;
    }

    public Recording saveRecording(Recording recording, User user) throws RecordingException {
        recording.setUser(user);
        Sentence sentence = sentenceRepository.findById(recording.getSentenceId());
        if(sentence == null) {
            throw new RecordingException("Lauset ei leitud");
        }
        recording.setSentence(sentence);

        recording.setRecordingFileName(
                storageService.storeUploadedAudio(recording.getAudioFile(), genRecordingFilename(recording), AUDIO_TYPE));
        recording = recordingRepository.save(recording);
        return recording;
    }

    public List<Recording> getRecordingsBySentenceId(long sentenceId) throws SentenceException {
        Sentence sentence = sentenceRepository.findById(sentenceId);
        if(sentence == null) {
            throw new SentenceException("Lause puudub.");
        }
        List<Recording> recordings = recordingRepository.findBySentence(sentence);
        for (Recording recording : recordings) {
            recording.getUser().setPassword(null);
        }
        return recordings;
    }

    public Sentence getSentenceById(long sentenceId) throws SentenceException {
        Sentence sentence = sentenceRepository.findById(sentenceId);
        if(sentence == null) {
            throw new SentenceException("Selle ID-ga lause puudub");
        }
        return sentence;
    }
}





