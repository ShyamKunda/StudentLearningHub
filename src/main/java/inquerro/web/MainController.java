package inquerro.web;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import inquerro.model.Catogories;
import inquerro.model.QuestionsStats;
import inquerro.service.CatagoriesService;
import inquerro.service.FirebaseService;
import inquerro.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class MainController {

    QuestionService questionService;
    CatagoriesService catagoriesService;
    Logger logger = LoggerFactory.getLogger(MainController.class);


    public MainController(QuestionService questionService, CatagoriesService catagoriesService) {
        this.questionService = questionService;
        this.catagoriesService = catagoriesService;
    }

    @GetMapping("/mainPage")
    public String mainPage(){
        return "index";
    }

    @GetMapping("/")
    public String root(Model model) {



//        List<Catogories> catagories = catagoriesService.getCatagories();
//        Map<String,List<String>> subtopic =  catagories.get(0).getTopics();
//        Map<String, Integer> allBranchesInfo = new HashMap<>();

        List<QuestionsStats> questionsStatsList = catagoriesService.getQuestionStats();

//        for(Catogories catogory : catagories){
//            QuestionsStats questionsStats = new QuestionsStats();
//            questionsStats.setId(catogory.getId());
//            questionsStats.setTitle(catogory.getTitle());
//            int totalQuestion = 0;
//            Map<String, Integer> subTopicMap = new HashMap<>();
//            Map<String,List<String>> topics = catogory.getTopics();
//            Set<String> allSubTopics = topics.keySet();
//            for(String topic : allSubTopics){
//                int subTopicTotalQuestions = questionService.getTotalQuestionsBasedTags(new String[]{topic});
//                totalQuestion += subTopicTotalQuestions;
//                subTopicMap.put(topic, subTopicTotalQuestions);
//                allBranchesInfo.put(catogory.getId() + "__" + topic,questionService.getTotalQuestionsBasedTags(new String[]{topic}));
//            }
//            questionsStats.setSubTopics(subTopicMap);
//            questionsStats.setCount(totalQuestion);
//            questionsStatsList.add(questionsStats);
//            allBranchesInfo.put(catogory.getTitle(),totalQuestion);
////            Firestore firestore = FirestoreClient.getFirestore();
////            firestore.collection("Stats").document(questionsStats.getId().toString()).set(questionsStats);
//
//        }
//        logger.info("All catagories :" + catagories);
//        logger.info("All allBranchesInfo :" + allBranchesInfo.toString());
//        logger.info("Questions stats  :" + questionsStatsList);
//        model.addAttribute("catagories", catagories);
//        model.addAttribute("allTagsInfo", allBranchesInfo);
        logger.info("Questions stats  :" + questionsStatsList);
        model.addAttribute("questionsStatsList", questionsStatsList);




        return "topics";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (!existingAuth.isAuthenticated())
            return "login";
        else
            System.out.println(request.getRequestURI());
        return "login";

    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }
}
