package readingkaraoke.eu.iapb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import readingkaraoke.eu.iapb.entity.Recording;
import readingkaraoke.eu.iapb.entity.Sentence;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.exception.RecordingException;
import readingkaraoke.eu.iapb.exception.SentenceException;
import readingkaraoke.eu.iapb.exception.SessionException;
import readingkaraoke.eu.iapb.exception.UserException;
import readingkaraoke.eu.iapb.service.KaraokeService;
import readingkaraoke.eu.iapb.service.SessionService;
import readingkaraoke.eu.iapb.service.StorageService;
import readingkaraoke.eu.iapb.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/karaoke")
@CrossOrigin(origins = {"https://readingkaraoke.xyz", "http://localhost:8081"})
public class KaraokeController {

    @Autowired
    private KaraokeService karaokeService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserService userService;


    @GetMapping("/getRandombyDifficultyGroup/{groupId}")
    public ResponseEntity<Sentence> getRandomSentence(@PathVariable("groupId") int groupId) {
        return new ResponseEntity<>(karaokeService.getRandomSentenceFromGroup(groupId), HttpStatus.OK);
    }

    @GetMapping("/getSentenceById/{sentenceId}")
    public ResponseEntity<Sentence> getRandomSentence(@PathVariable("sentenceId") long sentenceId) {
        try {
            return new ResponseEntity<>(karaokeService.getSentenceById(sentenceId), HttpStatus.OK);
        } catch (SentenceException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Sentence>> testGrid() {
        return new ResponseEntity<>(karaokeService.getAllSentences(), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<String> addKaraokeRecording(@ModelAttribute Recording recording) {
        try {
            User user = sessionService.getUserBySession(recording.getSessionToken());
            karaokeService.saveRecording(recording, user);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        } catch (RecordingException | SessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }



    @GetMapping("/recordings/{recordingId}")
    public ResponseEntity<byte[]> getKaraokeRecording(
            @PathVariable long recordingId, @RequestHeader Map<String, String> requestHeaders) {
        try {
            if(!requestHeaders.containsKey("token")) {
                throw new SessionException("Sessioon puudub. Logige sisse.");
            }
            User user = sessionService.getUserBySession(UUID.fromString(requestHeaders.get("token")));
            userService.isUserValidEditor(user);
            Recording recording = karaokeService.getKaraokeRecordingById(recordingId);

            try {
                byte[] fileAsByteArr = storageService.getSentenceFileAsByteArr(recording.getRecordingFileName());
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
                responseHeaders.add("Content-Disposition", "attachment; filename=" + recording.getRecordingFileName());
                return new ResponseEntity<>(fileAsByteArr, responseHeaders, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Audio fail puudub.");
            }
        } catch (SessionException | RecordingException | UserException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/recordings/allBySentence/{sentenceId}")
    public ResponseEntity<List<Recording>> getAllRecordingsBySentence(
            @PathVariable long sentenceId, @RequestHeader Map<String, String> requestHeaders) {
        try {
            if(!requestHeaders.containsKey("token")) {
                throw new SessionException("Sessioon puudub. Logige sisse.");
            }
            User user = sessionService.getUserBySession(UUID.fromString(requestHeaders.get("token")));
            userService.isUserValidEditor(user);

            List<Recording> recordings = karaokeService.getRecordingsBySentenceId(sentenceId);
            return new ResponseEntity<>(recordings, HttpStatus.OK);
        } catch (SessionException | SentenceException | UserException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }


}
