package readingkaraoke.eu.iapb.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import readingkaraoke.eu.iapb.entity.Sentence;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.entity.response.SaveSentenceResponse;
import readingkaraoke.eu.iapb.exception.SentenceException;
import readingkaraoke.eu.iapb.exception.SessionException;
import readingkaraoke.eu.iapb.exception.UserException;
import readingkaraoke.eu.iapb.service.SessionService;
import readingkaraoke.eu.iapb.service.StorageService;
import readingkaraoke.eu.iapb.service.sentence.SentenceService;

import java.io.IOException;

@CrossOrigin(origins = {"https://readingkaraoke.xyz", "http://localhost:8081"})
@RestController
@RequestMapping("/api/sentence")
public class SentenceController {

    @Autowired
    SessionService sessionService;

    @Autowired
    SentenceService sentenceService;

    @Autowired
    StorageService storageService;

    @PostMapping("/save")
    public ResponseEntity<SaveSentenceResponse> saveSentence(@ModelAttribute Sentence sentence) {
        try {
            User user = sessionService.getUserBySession(sentence.getUserToken());
            SaveSentenceResponse response = sentenceService.saveSentence(sentence, user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SentenceException | SessionException | UserException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Serveri viga. Palun proovige hiljem uuesti");
        }
    }

    @GetMapping("/recordings/{sentenceId}")
    public ResponseEntity<byte[]> getSentenceRecording(@PathVariable long sentenceId) {
        try {
            Sentence sentence = sentenceService.getRecordingFileById(sentenceId);
            try {
                byte[] fileAsByteArr = storageService.getSentenceFileAsByteArr(sentence.getAudioFilename());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Content-Disposition", "attachment; filename=" + sentence.getAudioFilename());

                return new ResponseEntity<>(fileAsByteArr, headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Audio fail puudub.");
            }
        } catch (SentenceException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Serveri viga. Palun proovige hiljem uuesti");
        }
    }

    @GetMapping("/textgrids/{sentenceId}")
    public ResponseEntity<byte[]> getSentenceTextgrid(@PathVariable long sentenceId) {
        try {
            Sentence sentence = sentenceService.getRecordingFileById(sentenceId);
            try {
                byte[] fileAsByteArr = storageService.getSentenceFileAsByteArr(sentence.getTextgridFilename());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Content-Disposition", "attachment; filename=" + sentence.getTextgridFilename());

                return new ResponseEntity<>(fileAsByteArr, headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "" +
                        "Textgrid fail puudub.");
            }
        } catch (SentenceException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Serveri viga. Palun proovige hiljem uuesti");
        }
    }

    @PostMapping("/updategroup")
    public ResponseEntity<String> updateSentenceGroup(@RequestBody Sentence sentence) {
        try {
            User user = sessionService.getUserBySession(sentence.getUserToken());
            sentenceService.updateSentenceGroup(sentence, user);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (SentenceException | SessionException | UserException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

}
