package inquerro.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import inquerro.model.MiniQuestion;
import inquerro.model.Question;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuestionService {


    public Question constructQuestion(MiniQuestion miniQuestion){


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = "anonymous";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
            System.out.println("authentication.getDetails();: " + authentication.getDetails());
            System.out.println("currentUserName " + currentUserName);
        }


        Question question = Question.builder()
                .answer(miniQuestion.getAnswer())
                .author(currentUserName)
                .tags(miniQuestion.getTags2())
                .content(miniQuestion.getContent())
                .createdAt(Instant.now())
                .explanation(miniQuestion.getExplanation())
                .isDeleted(false)
                .id(2l)
                .modifiedAt(Instant.now())
                .options(Arrays.asList(new String[]{miniQuestion.getOption1(),miniQuestion.getOption2(),miniQuestion.getOption3(),miniQuestion.getOption4()}))
                .build();


        return question;

    }

    public List<Question> getAllQuestions() throws ExecutionException, InterruptedException, IOException {

        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = firestore.collection("Questions").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Question> allUsers = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {

            System.out.println(document.getData().values());
            Map<String, Object> userData= document.getData();
            System.out.println(document.getData());
            ObjectMapper mapper = new ObjectMapper();
            String options = mapper.writeValueAsString(userData.get("options"));
            //OptionsClass[] optionsClass = mapper.readValue(options,OptionsClass[].class);
            String[] optionsArray = options.split(",");
            List<String> optionsList = new ArrayList<>();
            for(String a : optionsArray){
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(a);
                while(m.find()) {
                    System.out.println(m.group());
                    optionsList.add(m.group());
                }

            }



            Question question = Question.builder()
                    .content(userData.get("content").toString())
                    .answer(userData.get("answer").toString())
                    .options(optionsList)
                    .explanation(userData.get("explanation").toString())
                    .build();
            allUsers.add(question);
        }
        System.out.println(allUsers);
        return allUsers;
    }

    class OptionsClass {

        List options;
    }
}
