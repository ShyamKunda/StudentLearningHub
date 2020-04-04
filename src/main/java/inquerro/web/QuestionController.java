package inquerro.web;


import inquerro.model.MiniQuestion;
import inquerro.model.Question;
import inquerro.service.FirebaseService;
import inquerro.service.QuestionService;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Controller
public class QuestionController {


    FirebaseService firebaseService;
    QuestionService questionService;

    public QuestionController(FirebaseService firebaseService, QuestionService questionService) {
        this.firebaseService = firebaseService;
        this.questionService = questionService;
    }

    @PostMapping("/postQuestion")
    public ResponseEntity<MiniQuestion> createQuestion(@ModelAttribute MiniQuestion miniQuestion) throws ExecutionException, InterruptedException {


        System.out.println(miniQuestion.toString());

        Question question = questionService.constructQuestion(miniQuestion);

        question.setId(firebaseService.getQuestionsCount()+1);

        if (firebaseService.saveQuestion(question).length() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(miniQuestion);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(miniQuestion);

    }

    @GetMapping("/getQuestionPage")
    public ModelAndView createQuestion(Map<String, Object> model) {

        model.put("number", 1234);
        model.put("equation", "View equation here");
        return new ModelAndView("uploadQuestion");

    }

    @GetMapping("/listStudentsAall")
    public String listStudentAll(Model model) throws ExecutionException, InterruptedException, IOException {

        System.out.println("Entered here");
        List<Question> userList = questionService.getAllQuestions();
        System.out.println(userList);
        model.addAttribute("customersAll", userList);
        return "customersAll";
    }

    @GetMapping("/getQuestionsPage")
    public String getQuestionsPage(@RequestParam(value = "start", required = false)Integer start, Model model) throws ExecutionException, InterruptedException, IOException {


        System.out.println("sstart: " + start);
        List<Question> userList = questionService.getPaginatedQuestions(start,Integer.parseInt("3"));
        System.out.println(userList);
        model.addAttribute("customersAll", userList);
        return "getQuestionsPage";
    }
}
