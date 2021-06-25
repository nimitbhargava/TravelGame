package readingkaraoke.eu.iapb.service.sentence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import readingkaraoke.eu.iapb.entity.Sentence;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.entity.response.SaveSentenceResponse;
import readingkaraoke.eu.iapb.exception.SentenceException;
import readingkaraoke.eu.iapb.exception.UserException;
import readingkaraoke.eu.iapb.repository.SentenceRepository;
import readingkaraoke.eu.iapb.service.StorageService;
import readingkaraoke.eu.iapb.service.UserService;
import readingkaraoke.eu.iapb.service.WordService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class SentenceService {


    private static final String AUDIO_TYPE = ".wav";
    private static final String TEXT_FILE_URL = "";
    private static final String AUDIO_FILE_URL = "";
    private static final String TEXTGRID_URL = "";

    @Autowired
    SentenceRepository sentenceRepository;

    @Autowired
    WordService wordService;

    @Autowired
    SentenceAugmentation sentenceAugmentation;

    @Autowired
    UserService userService;
    
    @Autowired
    StorageService storageService;


    private void isSentenceValid(Sentence sentence) throws SentenceException {
        if(sentence.getText() == null || sentence.getText().length() == 0) {
            throw new SentenceException("Lause tühi");
        }
        if(sentence.getText().length() > 500) {
            throw new SentenceException("Lause max pikkus on 500 tähte");
        }
    }

    public Sentence getRecordingFileById(long id) throws SentenceException {
        Sentence sentence = sentenceRepository.findById(id);
        if(sentence == null) {
            throw new SentenceException("Selle id-ga lause puudub");
        }
        return sentence;
    }

    private String genSentenceAudioFilename(Sentence sentence) {
        return sentence.getAuthor().getUsername();
    }

    private String genTextfileFilename(Sentence sentence) {
        return sentence.getAuthor().getUsername();
    }


    private SaveSentenceResponse getSaveSentenceResponse(Sentence sentence) {
        SaveSentenceResponse response = new SaveSentenceResponse();
        response.setRecordedAudioUrl(AUDIO_FILE_URL + sentence.getAudioFilename());
        response.setTextgridFileUrl(TEXTGRID_URL + sentence.getTextgridFilename());
        response.setTextAsSyllables(wordService.wordListToSyllablesString(sentence.getWords()));
        response.setId(sentence.getId());

        return response;
    }

    /**
     * Adds back punctuation on words(textgrid convert removes): "This is a sentence" => "This is a sentence."
     * Doesn't work when spacing is incorrect and defaults to textgrid words without punctuation.
     */
    private void setSentenceWordsToOriginals(Sentence sentence) {
        List<String> originalTextWords = Arrays.asList(sentence.getText().split(" "));
        if(originalTextWords.size() != sentence.getWords().size()) {
            return;
        }
        for (int i = 0; i < originalTextWords.size(); i++) {
            sentence.getWords().get(i).setWord(originalTextWords.get(i));
        }
    }

    public SaveSentenceResponse saveSentence(Sentence sentence, User user) throws SentenceException, UserException, IOException {
        userService.isUserValidEditor(user);
        isSentenceValid(sentence);

        MultipartFile audio = sentence.getAudio();
        sentence.setAuthor(user);
        sentence.setAudioFilename(genSentenceAudioFilename(sentence));

        sentence.setAudioFilename(storageService.storeUploadedAudio(audio, genSentenceAudioFilename(sentence), AUDIO_TYPE));
        sentence.setTextFilename(storageService.saveTextFile(sentence.getText(), genTextfileFilename(sentence), ".txt"));

        sentence.setTextgridFilename(sentenceAugmentation.getTextGridFileForSentence(sentence));
        sentence.setWords(sentenceAugmentation.getWordsFromTextgridFile(sentence.getTextgridFilename(), false));
        setSentenceWordsToOriginals(sentence);

        sentenceRepository.save(sentence);

        return getSaveSentenceResponse(sentence);
    }

    public void updateSentenceGroup(Sentence updatedSentence, User user) throws UserException, SentenceException {
        userService.isUserValidEditor(user);

        Sentence oldSentence = sentenceRepository.findById(updatedSentence.getId());
        if(oldSentence == null) {
            throw new SentenceException("Selle ID-ga lause puudub.");
        }
        oldSentence.setDifficulty(updatedSentence.getDifficulty());
        sentenceRepository.save(oldSentence);
    }
}
