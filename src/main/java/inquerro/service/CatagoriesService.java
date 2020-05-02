package inquerro.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import inquerro.model.Catogories;
import inquerro.model.Question;
import inquerro.model.QuestionsStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CatagoriesService {


    Logger logger = LoggerFactory.getLogger(CatagoriesService.class);

    public CatagoriesService() {

    }


    public List<Catogories> getCatagories() throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection("Catagories");
        ApiFuture<QuerySnapshot> futureWithCondition = collectionReference.orderBy("id").get();
        List<QueryDocumentSnapshot> allDocuments = new ArrayList<>();
        List<Catogories> allCatagories = new ArrayList<>();

            allDocuments  = futureWithCondition.get().getDocuments();
            for (QueryDocumentSnapshot document : allDocuments) {
                allCatagories.add(document.toObject(Catogories.class));
            }


        return allCatagories;

    }

    @Cacheable("topics")
    public List<QuestionsStats> getQuestionStats() {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection("Stats");
        ApiFuture<QuerySnapshot> futureWithCondition = collectionReference.orderBy("id").get();
        List<QueryDocumentSnapshot> allDocuments = new ArrayList<>();
        List<QuestionsStats> allCatagories = new ArrayList<>();
        try {
            allDocuments  = futureWithCondition.get().getDocuments();
            for (QueryDocumentSnapshot document : allDocuments) {
                allCatagories.add(document.toObject(QuestionsStats.class));
            }

        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }
        return allCatagories;
    }

    public QuestionsStats getQuestionsStatsById(Long id) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection("Stats").document(id.toString());
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
            document = future.get();
        QuestionsStats questionsStat = null;
        if (document.exists()) {
            // convert document to POJO
            questionsStat = document.toObject(QuestionsStats.class);
        } else {
            logger.error("No such document exist with collection Stats and document id %s", id.toString());
        }
        return questionsStat;
    }


    public boolean updateExistingQuestionStat(Long id, QuestionsStats questionsStats) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResultApiFuture =  firestore.collection("Stats").document(id.toString()).set(questionsStats);
            WriteResult future = writeResultApiFuture.get();
            future.getUpdateTime();
            return true;


    }
}
