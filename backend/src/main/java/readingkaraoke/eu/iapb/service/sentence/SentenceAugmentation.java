package readingkaraoke.eu.iapb.service.sentence;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import readingkaraoke.eu.iapb.entity.Sentence;
import readingkaraoke.eu.iapb.entity.Word;
import readingkaraoke.eu.iapb.exception.SentenceException;
import readingkaraoke.eu.iapb.service.StorageService;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class SentenceAugmentation {

    @Autowired
    private StorageService storageService;

    @Value("${karaoke.syllables.script.path}")
    private String syllableScriptPath;



    private void addSyllablesToWords(List<Word> words) throws SentenceException {
        for (Word word : words) {
            Process process = null;
            try {
                if(System.getProperty("os.name").contains("Windows")) {
                    process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", syllableScriptPath, word.getWord() } );
                } else {
                    String[] cmd = new String[]{"/bin/sh", syllableScriptPath, word.getWord()};
                    process = Runtime.getRuntime().exec(cmd);
                }

                JSONArray jsonArr = getSyllableJson(process);
                word.setNrOfSyllables(jsonArr.length());
                String syllables = "";
                for (int i = 0; i < jsonArr.length(); i++) {
                    if(!syllables.equals("")) {
                        syllables += "-";
                    }
                    syllables += jsonArr.getJSONObject(i).get("syllable");
                }
                word.setWordAsSyllables(syllables);

            } catch (IOException | JSONException e ) {
                e.printStackTrace();
                throw new SentenceException("Silbitamine ebaõnnestus");
            }
        }
    }

    private JSONArray getSyllableJson(Process process) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        JSONArray jsonArr = null;
        while ((line = reader.readLine()) != null) {
            if(line.contains("syllablesDictionary:")) {
                line = line.replace("syllablesDictionary:", "");
                jsonArr = new JSONArray(line);
            }
        }
        if(jsonArr == null) {
            throw new IOException();
        }
        return jsonArr;
    }

    public List<Word> getWordsFromTextgridFile(String fileName, boolean useStaticFiles) throws SentenceException {
        try {
            List<Word> words = storageService.getWordListFromTextgrid(fileName, useStaticFiles);
            addSyllablesToWords(words);
            return words;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SentenceException("Textgridi lugemine ebaõnnestus");
        }
    }


    public String getTextGridFileForSentence(Sentence sentence) throws SentenceException {
        File wavAudioFile, sentenceTextFile;
        try {
            sentenceTextFile = storageService.getFile(sentence.getTextFilename());
            wavAudioFile = storageService.getFile(sentence.getAudioFilename());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new SentenceException("Serveri viga, proovige hiljem uuesti.");
        }
        HttpPost post = new HttpPost("https://bark.phon.ioc.ee/autosegment2/run");
        FileBody wavFileBody = new FileBody(wavAudioFile, ContentType.DEFAULT_BINARY);
        FileBody txtFileBody = new FileBody(sentenceTextFile, ContentType.DEFAULT_BINARY);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("wav", wavFileBody);
        builder.addPart("txt", txtFileBody);
        HttpEntity entity = builder.build();

        post.setEntity(entity);
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(post);
            InputStream inputStream = response.getEntity().getContent();
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            return storageService.saveTextFile(textBuilder.toString(), sentence.getAuthor().getUsername(), ".TextGrid");
        } catch (IOException e) {
            e.printStackTrace();
            throw new SentenceException("Lause segmenteerimine ei õnnestunud. Palun proovige hiljem uuesti.");
        }
    }






}
