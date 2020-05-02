package inquerro.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import inquerro.model.*;
import inquerro.web.dto.UserRegistrationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {


    Logger logger = LoggerFactory.getLogger(FirebaseService.class);
    private CatagoriesService catagoriesService;


    public FirebaseService() {
    }

    private  Map<String,Long> getTagDestination( List<Catogories> allCatagories, List<String> allTags){

        Map<String,Long> tagDestination = new HashMap<>();
        List<String> allTagsTemp = allTags;
        for (Catogories catogories: allCatagories
        ) {

            Map<String, List<String>> topics =  catogories.getTopics();
            Set allKeys = topics.keySet();
            int i=0;
            List<Integer> toRemove = new ArrayList<>();
            for(String tag: allTags){
                if(allKeys.contains(tag)){
                    tagDestination.put(tag, catogories.getId());
                    toRemove.add(i);
                }
                i++;
            }
            for(Integer a : toRemove){
                allTags.remove(a);
            }
        }
        return tagDestination;
    }

    public boolean  saveQuestion(Question question) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();;
        ApiFuture<WriteResult> collectionsApiFuture =  firestore.collection("Questions").document().set(question);
            String time = collectionsApiFuture.get().getUpdateTime().toString();
            //Get All tags Present in the question
            List<String> allTags = question.getTags();
            //Get All catagories from Catagories service
            catagoriesService = new CatagoriesService();
            List<Catogories> allCatagories = catagoriesService.getCatagories();
            //Get a Map between tags and Branch Id
            Map<String,Long> tagDestination = getTagDestination(allCatagories,allTags);
            //Iterate the Map of all Tags
             boolean result = false;

            for (Map.Entry<String, Long> entry : tagDestination.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                String tagName = entry.getKey();
                Long branchThatTagBelongs = entry.getValue();
                QuestionsStats questionsStats =  catagoriesService.getQuestionsStatsById(branchThatTagBelongs);
                if(questionsStats != null){
                    Map<String, Integer> supTopics =  questionsStats.getSubTopics();
                    if(supTopics.containsKey(tagName)){
                        supTopics.put(tagName,supTopics.get(tagName)+1 );
                    }else {
                        //Add tag to questionTags
                        supTopics.put(tagName,1);
                    }
                    questionsStats.setSubTopics(supTopics);
                    questionsStats.setCount(questionsStats.getCount()+1);
                    result=  catagoriesService.updateExistingQuestionStat(branchThatTagBelongs,questionsStats);


                }
            }
            return result;

    }

    public String  saveUser(UserRegistrationDto user) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();;
        ApiFuture<WriteResult> collectionsApiFuture =  firestore.collection("User").document().set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public Long getQuestionsCount() throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();;
        logger.info("Inside: getQuestionsCount()" );

            QuerySnapshot querySnapshot =  firestore.collection("Questions").orderBy("id", Query.Direction.DESCENDING).limit(1).get().get();
             if (querySnapshot.getDocuments().size() > 0)
                return querySnapshot.getDocuments().get(0).getLong("id");
             else
                 return 0l;

    }

    public String setQuestionsCount(Long count) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();;
        ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("Stats").document("questions/id").set(count);


        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String saveMathJaxEquation(MathJaxVerification mathJaxVerification) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("MathJaxValidation").document("1").set(mathJaxVerification);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String getEquation() throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();;
        String  data = firestore.collection("MathJaxValidation").document("1").get().get().get("data").toString();
        return data;
    }



}
