package readingkaraoke.eu.iapb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import readingkaraoke.eu.iapb.entity.Sentence;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.entity.Word;
import readingkaraoke.eu.iapb.service.StorageService;
import readingkaraoke.eu.iapb.service.sentence.SentenceAugmentation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Transactional
@Component
public class Application {

    @Autowired
    private StorageService storageService;

    @Autowired
    private SentenceAugmentation sentenceAugmentation;

    @PersistenceContext
    private EntityManager em;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;


    @EventListener(ApplicationReadyEvent.class)
    public void initData() {

        // When using in memory database, insert an admin account for testing
        if(datasourceUrl.equals("jdbc:h2:mem:testdb")) {
            System.out.println("Setting up static admin and sentences for dev env.");
            insertAdmin();

            // Static files(textgrid, audio, text files) need to be prepared in the path provided in custom.properties.
            // Syllable forms for words are still generated with EstNLTK so it needs to be configured properly in scripts and custom.properties

            // saveStaticSentences(adminUser);
        }
    }


    public void insertAdmin() {
        User user = new User();
        user.setAdmin(true);
        user.setUsername("admin");
        user.setPassword("memoryAdmin");
        user.setAge(26);
        em.persist(user);
    }

    public void saveStaticSentences(User adminUser) {

//        List<String> fileNames = Arrays.asList("kerge1", "kerge2", "keskmine1", "keskmine2", "raske1", "raske2");
//                List<String> fileNames = Arrays.asList("kerge1", "keskmine1", "kerge2");
//        List<String> fileNames = Arrays.asList("kerge1");
        List<String> fileNames = Arrays.asList();
        for (String fileName : fileNames) {
            try {
                Sentence sentence = new Sentence();
                String sentenceText = storageService.getFileContents(fileName + ".txt");
                System.out.println("inserting sentence: " + sentenceText);
                List<Word> words = sentenceAugmentation.getWordsFromTextgridFile(fileName + ".TextGrid", true);

                sentence.setTextFilename(fileName + ".txt");
                sentence.setAudioFilename(fileName + ".wav");
                sentence.setTextgridFilename(fileName + ".TextGrid");
                sentence.setAuthor(adminUser);
                sentence.setText(sentenceText);
                sentence.setWords(words);
                if(fileName.contains("kerge")) {
                    sentence.setDifficulty(0);
                } else if(fileName.contains("keskmine")) {
                    sentence.setDifficulty(1);
                } else if(fileName.contains("raske")) {
                    sentence.setDifficulty(2);
                }
                for (Word word : words) {
//                    word.setSentence(sentence);
                }

                insertSentence(sentence);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void insertSentence(Sentence sentence) {
        em.persist(sentence);
    }

}





