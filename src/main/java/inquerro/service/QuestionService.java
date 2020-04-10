package inquerro.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
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
import java.sql.Timestamp;
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

    public QuestionService() {

    }

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
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .explanation(miniQuestion.getExplanation())
                .isDeleted(false)
                .modifiedAt(new Timestamp(System.currentTimeMillis()))
                .options(Arrays.asList(new String[]{miniQuestion.getOption1(),miniQuestion.getOption2(),miniQuestion.getOption3(),miniQuestion.getOption4()}))
                .build();


        return question;

    }

    public List<Question> getPaginatedQuestions(int start, int count) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection("Questions");
        ApiFuture<QuerySnapshot> futureWithCondition = collectionReference.orderBy("id").startAfter(start).limit(count).get();

        List<QueryDocumentSnapshot> documents1 = futureWithCondition.get().getDocuments();
        List<Question> allQuestions = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents1) {

            System.out.println(document.get("id"));
            Map<String, Object> userData= document.getData();
            List<String> optionsList = (List<String>) userData.get("options");

            Question question = Question.builder()
                    .content(userData.get("content").toString())
                    .answer(userData.get("answer").toString())
                    .options(optionsList)
                    .explanation(userData.get("explanation").toString())
                    .build();
            allQuestions.add(question);
        }

        return allQuestions;

    }

    public List<Question> getAllQuestions() throws ExecutionException, InterruptedException, IOException {

        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("Questions").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Question> allUsers = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {

            System.out.println(document.getData().values());
            Map<String, Object> userData= document.getData();

            List<String> optionsList = (List<String>) userData.get("options");

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
