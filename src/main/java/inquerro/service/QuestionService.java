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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuestionService {

    Logger logger = LoggerFactory.getLogger(QuestionService.class);

    public QuestionService() {

    }

    public Question constructQuestion(MiniQuestion miniQuestion){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = "anonymous";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
            logger.info("authentication.getDetails();: " + authentication.getDetails());
            logger.info("currentUserName " + currentUserName);
        }

        List<String> tags =  miniQuestion.getTags2();

        List<String> newTags =  miniQuestion.getTags2();
        for(int i=0; i<tags.size() ;i++){

           logger.info("tags.get(i)" + tags.get(i));
           String tag =  tags.get(i)
                   .replace("[{\"value\":\"", "")
                   .replace("\"}","")
                   .replace("{\"value\":\"","")
                   .replace("]","");
            logger.info("tags String" + tag);
           newTags.set(i,tag);
        }

        Question question = Question.builder()
                .answer(miniQuestion.getAnswer())
                .author(currentUserName)
                .tags(newTags)
                .content(miniQuestion.getContent())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .explanation(miniQuestion.getExplanation())
                .isDeleted(false)
                .modifiedAt(new Timestamp(System.currentTimeMillis()))
                .options(Arrays.asList(new String[]{miniQuestion.getOption0(),miniQuestion.getOption1(),miniQuestion.getOption2(),miniQuestion.getOption3()}))
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

            logger.info("document id:" + document.get("id"));
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

            logger.info("document.getData().values()" + document.getData().values());

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

        logger.info("allUsers" + allUsers);
        return allUsers;
    }

    class OptionsClass {

        List options;
    }
}
