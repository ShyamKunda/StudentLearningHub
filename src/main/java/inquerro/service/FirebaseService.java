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
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    Firestore firestore;

    public FirebaseService() {
        firestore = FirestoreClient.getFirestore();
    }

    public String  saveQuestion(Question question) throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> collectionsApiFuture =  firestore.collection("Questions").document().set(question);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String  saveUser(UserRegistrationDto user) throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> collectionsApiFuture =  firestore.collection("User").document().set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public Long getQuestionsCount() throws ExecutionException, InterruptedException {

        Long totalQuestions = firestore.collection("Stats").document("questions").get().get().getLong("count");
        QuerySnapshot querySnapshot =  firestore.collection("Questions").orderBy("id", Query.Direction.DESCENDING).limit(1).get().get();
        return querySnapshot.getDocuments().get(0).getLong("id");
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
