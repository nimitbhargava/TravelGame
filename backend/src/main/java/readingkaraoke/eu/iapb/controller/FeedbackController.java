package readingkaraoke.eu.iapb.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.entity.response.FeedbackResponse;
import readingkaraoke.eu.iapb.exception.SessionException;
import readingkaraoke.eu.iapb.service.FeedbackService;
import readingkaraoke.eu.iapb.service.SessionService;

import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"https://readingkaraoke.xyz", "http://localhost:8081"})
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private FeedbackService feedbackService;


    @GetMapping("/getUserFeedback")
    public ResponseEntity<FeedbackResponse> requestUserFeedback(@RequestHeader Map<String, String> requestHeaders) {
        System.out.println("req feedback");
        try {
            if(!requestHeaders.containsKey("token")) {
                throw new SessionException("Sessioon puudub. Logige sisse.");
            }
            User user = sessionService.getUserBySession(UUID.fromString(requestHeaders.get("token")));
            FeedbackResponse feedbackResponse = feedbackService.getUserFeedback(user);
            return new ResponseEntity<>(feedbackResponse, HttpStatus.OK);
        } catch (SessionException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
