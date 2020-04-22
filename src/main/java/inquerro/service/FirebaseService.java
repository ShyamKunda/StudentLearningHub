package inquerro.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import inquerro.model.MathJaxVerification;
import inquerro.model.Question;
import inquerro.model.User;
import inquerro.web.dto.UserRegistrationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    Firestore firestore;
    Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    public FirebaseService() {
        firestore = FirestoreClient.getFirestore();
    }

    public boolean  saveQuestion(Question question){

        ApiFuture<WriteResult> collectionsApiFuture =  firestore.collection("Questions").document().set(question);
        try {
            String time = collectionsApiFuture.get().getUpdateTime().toString();
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }

    }

    public String  saveUser(UserRegistrationDto user) throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> collectionsApiFuture =  firestore.collection("User").document().set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public Long getQuestionsCount() {
        logger.error("Inside: getQuestionsCount()" );
        try{
            QuerySnapshot querySnapshot =  firestore.collection("Questions").orderBy("id", Query.Direction.DESCENDING).limit(1).get().get();
             if (querySnapshot.getDocuments().size() > 0)
                return querySnapshot.getDocuments().get(0).getLong("id");
             else
                 return 0l;
        }catch (Exception e) {
            logger.error(e.getMessage());
            return -1l;
        }
    }

    public String setQuestionsCount(Long count) throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("Stats").document("questions/id").set(count);


        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String saveMathJaxEquation(MathJaxVerification mathJaxVerification) throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("MathJaxValidation").document("1").set(mathJaxVerification);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String getEquation() throws ExecutionException, InterruptedException {

        String  data = firestore.collection("MathJaxValidation").document("1").get().get().get("data").toString();
        return data;
    }



}
