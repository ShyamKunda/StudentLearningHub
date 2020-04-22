package inquerro.web;


import inquerro.model.MiniQuestion;
import inquerro.model.Question;
import inquerro.service.FirebaseService;
import inquerro.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class QuestionController {

    Logger logger = LoggerFactory.getLogger(QuestionController.class);
    FirebaseService firebaseService;
    QuestionService questionService;

    public QuestionController(FirebaseService firebaseService, QuestionService questionService) {
        this.firebaseService = firebaseService;
        this.questionService = questionService;
    }

    @PostMapping("/postQuestion")
    public ResponseEntity<Question> createQuestion(@ModelAttribute MiniQuestion miniQuestion) {


        System.out.println(miniQuestion.toString());

        Question question = questionService.constructQuestion(miniQuestion);
        Long questionId = firebaseService.getQuestionsCount();

        if(questionId < 0){
            logger.error("Bad Request + " + String.valueOf(HttpStatus.BAD_REQUEST + ": Question + "  + question.toString()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(question);
        } else {
            question.setId(questionId + 1);
        }

        if (firebaseService.saveQuestion(question))
            return ResponseEntity.status(HttpStatus.OK).body(question);
        else{
            logger.error("Bad Request + " + String.valueOf(HttpStatus.BAD_REQUEST + ": Question + "  + question.toString()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(question);
        }


    }

    @GetMapping("/getQuestionPage")
    public ModelAndView createQuestion(Map<String, Object> model) {

        model.put("number", 1234);
        model.put("equation", "View equation here");
        return new ModelAndView("uploadQuestion");

    }

    @GetMapping("/listStudentsAall")
    public String listStudentAll(Model model) throws ExecutionException, InterruptedException, IOException {

        List<Question> userList = questionService.getAllQuestions();
        System.out.println(userList);
        model.addAttribute("customersAll", userList);
        return "customersAll";
    }

    @GetMapping("/getQuestionsPage")
    public String getQuestionsPage(@RequestParam(value = "start", required = false)Integer start, Model model) throws ExecutionException, InterruptedException, IOException {

        logger.info("start id: " + start);
        if (start == null){
            start =10;
        }
        List<Question> questionsList = questionService.getPaginatedQuestions(start,Integer.parseInt("8"));

        logger.debug("Question List: " + questionsList);

        List<String> allAnswers = new ArrayList<>();
        for (Question question:
                questionsList) {
            allAnswers.add(question.getAnswer());
        }


        List<String> allAnswersAlphabets = new ArrayList<>();
        for (Question question:
                questionsList) {

            String answer = question.getStrAnswer();
            if(answer == null) {

                int number = Integer.parseInt( question.getAnswer().replace("option",""));

                if (number==0){
                    answer = "a";
                }else if(number==1){
                    answer = "b";
                }else if(number == 2){
                    answer = "c";
                }else
                    answer = "d";
            }

            allAnswersAlphabets.add(answer);
        }



        model.addAttribute("customersAll", questionsList);
        model.addAttribute("allAnswers", allAnswers);
        model.addAttribute("allAnswersAlphabets", allAnswersAlphabets);

        return "getQuestionsPage";
    }
}
