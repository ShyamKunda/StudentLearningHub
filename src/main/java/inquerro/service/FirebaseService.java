package inquerro.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import inquerro.model.Question;
import inquerro.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {


    public String  saveQuestion(Question question) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture =  firestore.collection("Questions").document().set(question);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }



}
