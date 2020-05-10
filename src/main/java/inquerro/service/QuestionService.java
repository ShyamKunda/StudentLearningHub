package inquerro.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
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

        String answer = "";
        int number = Integer.parseInt( miniQuestion.getAnswer().replace("option",""));

        if (number==0){
            answer = "a";
        }else if(number==1){
            answer = "b";
        }else if(number == 2){
            answer = "c";
        }else
            answer = "d";

        logger.info("Answer: " + answer);
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
                .strAnswer(answer)
                .build();


        return question;

    }

    public boolean likeAQuestion(int questionId){
        Firestore firestore = FirestoreClient.getFirestore();

        DocumentReference questionDoc;
        List<String> likes;
           try {
               Query query = firestore.collection("Questions").whereEqualTo("id", questionId);
               questionDoc =  query.get().get().getDocuments().get(0).getReference();
               logger.info("liked below question with id:" + questionDoc.get().get().get("id"));
               likes = (List<String>) questionDoc.get().get().get("likes");
               if(likes == null){
                   likes = new ArrayList<>();
               }
               Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
               String currentUserName = authentication.getName();
               likes.add(currentUserName);
               questionDoc.update("likes", likes);
               return true;
           }catch (Exception e) {
               logger.info("Exception occured in likeAQuestion service : " + e.getMessage());
                e.printStackTrace();
           }


        return false;

    }

    public boolean unlikeAQuestion(int questionId){

        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference questionDoc;
        List<String> likes;
        try {
            Query query = firestore.collection("Questions").whereEqualTo("id", questionId);
            questionDoc =  query.get().get().getDocuments().get(0).getReference();
            logger.info("un liked below question with id:" + questionDoc.get().get().get("id"));
            likes = (List<String>) questionDoc.get().get().get("likes");
            if(likes == null){
                return true;
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();
            likes.remove(currentUserName);
            questionDoc.update("likes", likes);
            return true;
        }catch (Exception e) {
            logger.info("Exception occured in unlikeAQuestion service : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Question> getPaginatedQuestions(int start, int count) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection("Questions");
        ApiFuture<QuerySnapshot> futureWithCondition = collectionReference.orderBy("id").startAfter(start).limit(count).get();

        List<QueryDocumentSnapshot> documents1 = futureWithCondition.get().getDocuments();
        logger.info("Total documents fetech:" + documents1.size());
        List<Question> allQuestions = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents1) {

            logger.info("document id:" + document.get("id"));
            Map<String, Object> userData= document.getData();
            List<String> optionsList = (List<String>) userData.get("options");

            String answer = "";
            int number = Integer.parseInt( userData.get("answer").toString().replace("option",""));

            if (number==0){
                answer = "a";
            }else if(number==1){
                answer = "b";
            }else if(number == 2){
                answer = "c";
            }else
                answer = "d";

            Question question = Question.builder()
                    .content(userData.get("content").toString())
                    .answer(userData.get("answer").toString())
                    .options(optionsList)
                    .explanation(userData.get("explanation").toString())
                    .strAnswer(answer)
                    .build();
            allQuestions.add(question);
        }

        return allQuestions;

    }


    public int getTotalQuestionsBasedTags(String[] tags){

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection("Questions");
        List<String> tagList = Arrays.asList(tags);


        try{
            return collectionReference.whereArrayContainsAny("tags",tagList).get().get().size();
        }catch (Exception e){
            logger.error("Unable to get question with specified tags <%s> and Error message is %s", tags.toString(), e.getMessage());
        }

        return 0;
    }

    public List<Question> getPaginatedQuestionsByTagNames(int start, String[] tags,int count){

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection("Questions");
        List<String> tagList = Arrays.asList(tags);
        ApiFuture<QuerySnapshot> futureWithCondition =  collectionReference.whereArrayContainsAny("tags",tagList).orderBy("id").startAfter(start).limit(count).get();

        List<QueryDocumentSnapshot> documents1 = new ArrayList<>();
        List<Question> allQuestions = new ArrayList<>();
        try {
            documents1 = futureWithCondition.get().getDocuments();
            logger.info("Total documents fetech:" + documents1.size());
            if(documents1.size() == 0 ) {
                logger.info("No documents found with start %d and tags %d",start, tags.toString() );
                return allQuestions;
            }
        } catch (Exception e){

            logger.info("Unable to Fetch documents" + e.getMessage());
        }


        for (QueryDocumentSnapshot document : documents1) {

            logger.info("document id:" + document.get("id"));
            Map<String, Object> userData= document.getData();
            List<String> optionsList = (List<String>) userData.get("options");



            Question question = Question.builder()
                    .id(Long.parseLong(userData.get("id").toString()))
                    .author(userData.get("author").toString())
                    .tags((List<String>)userData.get("tags"))
                    .isDeleted((boolean)userData.get("deleted"))
                    .content(userData.get("content").toString())
                    .answer(userData.get("answer").toString())
                    .options(optionsList)
                    .likes((List<String>)userData.get("likes"))
                    .explanation(userData.get("explanation").toString())
                    .strAnswer(userData.get("strAnswer").toString())
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
