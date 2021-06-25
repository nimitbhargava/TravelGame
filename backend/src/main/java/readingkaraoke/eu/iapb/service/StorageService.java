package readingkaraoke.eu.iapb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import readingkaraoke.eu.iapb.entity.Word;
import readingkaraoke.eu.iapb.exception.SentenceException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class StorageService {

    @Value("${sentence.recordings.path}")
    private String sentencePathRoot;

    @Value("${sentence.recordings.static.path}")
    private String staticPathRoot;


    public String storeUploadedAudio(MultipartFile file, String filename, String fileEnding) {
        Path root = Paths.get(sentencePathRoot);
        int counter = 0;
        boolean saved = false;
        String fullName = "";

        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            while (!saved) {
                fullName = filename + "_" + counter + fileEnding;
                if (Files.exists(root.resolve(fullName))) {
                    counter += 1;
                } else {
                    Files.copy(file.getInputStream(), root.resolve(fullName));
                    saved = true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }

        return fullName;
    }

    public List<Word> getWordListFromTextgrid(String filename, boolean useStaticFiles) throws Exception {
        File textGrid;
        if(useStaticFiles) {
            System.out.println("path: " + staticPathRoot + filename);
            textGrid = new File(staticPathRoot + filename);
        } else {
            System.out.println("path: " + sentencePathRoot + filename);
            textGrid = new File(sentencePathRoot + filename);
        }
        BufferedReader br = new BufferedReader(new FileReader(textGrid));

        String line;
        boolean onWord = false;
        Word nextWord = null;
        List<Word> words = new ArrayList<>();

        double wordStart = 0.00;
        double wordEnd = 0.00;

        while ((line = br.readLine()) != null) {
            if(line.contains("item [2]:")) {
                break;
            }
            if(line.contains("intervals [")) {
                onWord = true;
                if(nextWord != null) {
                    if(!nextWord.getWord().equals("<sil>")) {
                        int wordTime = (int)((wordEnd - wordStart) * 1000);
                        nextWord.setTime(wordTime);
                        words.add(nextWord);
                    }
                }
                nextWord = new Word();
            } else if(onWord) {
                if(line.contains("xmin")) {
                    wordStart = Double.parseDouble(line.split(" ")[2]);
                } else if(line.contains("xmax")) {
                    wordEnd = Double.parseDouble(line.split(" ")[2]);
                } else if(line.contains("text")) {
                    line = line.replace("\"", "");
                    nextWord.setWord(line.split(" ")[2]);
                }
            }
        }
        br.close();

        return words;
    }

    public String saveTextFile(String sentenceText, String filename, String fileType) throws SentenceException {
        Path root = Paths.get(sentencePathRoot);
        String fullFilename = "";
        int counter = 0;
        do {
            fullFilename = filename + "_" + counter + fileType;
            counter++;
        } while (Files.exists(root.resolve(fullFilename)));

        File file = new File(root.resolve(fullFilename).toString());
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(sentenceText);
            fileWriter.close();
        } catch (IOException e) {
            throw new SentenceException("Lause teksti salvestamine ei õnnestunud.");
        }
        return fullFilename;
    }

    public String getFileContents(String filename) {
        StringBuilder data = new StringBuilder();
        try {
            Path root = Paths.get(sentencePathRoot);
            File myObj = new File(root.toString() + "\\"+ filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Faili lugemine ebaõnnestus");
            e.printStackTrace();
        }
        return data.toString();
    }

    public File getFile(String filename) throws FileNotFoundException {
        File file = new File(sentencePathRoot + filename);
        if(!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }

    public byte[] getSentenceFileAsByteArr(String filename) throws IOException {
        File file = getFile(filename);
        InputStream inputStream = new FileInputStream(file);
        return inputStream.readAllBytes();
    }
}
