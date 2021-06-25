package readingkaraoke.eu.iapb.service;

import org.springframework.stereotype.Service;
import readingkaraoke.eu.iapb.entity.Word;

import java.util.List;

@Service
public class WordService {

    public String wordListToSyllablesString(List<Word> words) {
        StringBuilder syllableString = new StringBuilder();
        for (Word word : words) {
            syllableString.append(word.getWordAsSyllables()).append(" ");
        }
        return syllableString.toString();
    }

}
