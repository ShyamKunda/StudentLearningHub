package inquerro.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import inquerro.model.MathJaxVerification;
import inquerro.model.Question;
import inquerro.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public String saveMathJaxEquation(MathJaxVerification mathJaxVerification) throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("MathJaxValidation").document("1").set(mathJaxVerification);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String getEquation() throws ExecutionException, InterruptedException {

        String  data = firestore.collection("MathJaxValidation").document("1").get().get().get("data").toString();
        return data;
    }



}
