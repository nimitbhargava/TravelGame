package readingkaraoke.eu.iapb.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import readingkaraoke.eu.iapb.service.StorageService;
import readingkaraoke.eu.iapb.service.sentence.SentenceAugmentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Controller for trying functionality while developing
 */
@CrossOrigin(origins = {"https://readingkaraoke.xyz", "http://localhost:8081"})
@RestController
@RequestMapping("/api")
public class GeneralController {

    @Autowired
    StorageService storageService;

    @Autowired
    SentenceAugmentation sentenceAugmentation;

    private String printProcess(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if(line.startsWith("syllablesArr:")) {
                return line.replace("syllablesArr:", "");
            }
        }
        return null;
    }

    @GetMapping("/testconnect")
    public ResponseEntity<String> testConnection() {
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    @PostMapping("/testupload")
    public String handleFileUpload
            (@RequestParam("file") MultipartFile file, @RequestParam("name") String fileName) {
        System.out.println("filename: " + fileName);
        storageService.storeUploadedAudio(file, fileName, ".wav");

        return "success";
    }


}
