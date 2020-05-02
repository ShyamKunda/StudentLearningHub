package inquerro.web;


import inquerro.model.ImageModel;
import inquerro.model.MiniQuestion;
import inquerro.model.Question;
import inquerro.repository.ImageRepositoy;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static inquerro.web.ImageUploadController.compressBytes;

@Controller
public class QuestionController {

    Logger logger = LoggerFactory.getLogger(QuestionController.class);
    FirebaseService firebaseService;
    QuestionService questionService;
    ImageRepositoy imageRepository;

    public QuestionController(FirebaseService firebaseService, QuestionService questionService, ImageRepositoy imageRepository) {
        this.firebaseService = firebaseService;
        this.questionService = questionService;
        this.imageRepository =imageRepository;
    }

    @PostMapping("/uploadImage")
    public ResponseEntity.BodyBuilder uplaodImage(@RequestParam("imageFile") MultipartFile file) throws IOException {
        System.out.println("Original Image Byte Size - " + file.getBytes().length);
        ImageModel img = new ImageModel(file.getOriginalFilename(), file.getContentType(),
                compressBytes(file.getBytes()));
        imageRepository.save(img);
        return ResponseEntity.status(HttpStatus.OK);
    }

    @PostMapping("/postQuestion")
    public ResponseEntity<Question> createQuestion(@ModelAttribute MiniQuestion miniQuestion) throws ExecutionException, InterruptedException {


        System.out.println(miniQuestion.toString());

        Question question = questionService.constructQuestion(miniQuestion);
        Long questionId = firebaseService.getQuestionsCount();

        if(questionId < 0){
            logger.error("Bad Request + " + String.valueOf(HttpStatus.BAD_REQUEST + ": Question + "  + question.toString()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(question);
        } else {
            question.setId(questionId + 1);
        }

        if (firebaseService.saveQuestion(question)){
            List<String> allTags = question.getTags();
            return ResponseEntity.status(HttpStatus.OK).body(question);
        }

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
            start =0;
        }
        List<Question> questionsList = questionService.getPaginatedQuestions(start,Integer.parseInt("4"));
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

    @GetMapping("/getQuestionsPageByTagName")
    public String getQuestionsPageByTagName(@RequestParam(value = "start", required = false)Integer start, @RequestParam String[] tags, @RequestParam String totalQuestions, @RequestParam String parentTopic, Model model) throws ExecutionException, InterruptedException, IOException {

        logger.info("start id: " + start);
        if (start == null){
            start =0;
        }

        for(int i=0;i< tags.length;i++){

            tags[i] = tags[i].replace("-", " ");
        }
        List<Question> questionsList = questionService.getPaginatedQuestionsByTagNames(start, tags, Integer.parseInt("4"));
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


        int questionsCount = Integer.parseInt(totalQuestions);
        List<Integer> paginationList = new ArrayList<>();
        int startPage = 0;
        while(startPage < questionsCount){
            paginationList.add(startPage);
            startPage += 4;
        }

        model.addAttribute("customersAll", questionsList);
        model.addAttribute("allAnswers", allAnswers);
        model.addAttribute("allAnswersAlphabets", allAnswersAlphabets);
        model.addAttribute("paginationList", paginationList);
        model.addAttribute("tagsList", tags[0]);
        model.addAttribute("questionsCount", questionsCount);
        model.addAttribute("parentTopic", parentTopic.length()<=5 ?parentTopic: getShortName(parentTopic));


        return "getQuestionsPage";
    }

    private String getShortName(String name){


        switch (name){
            case "Computer Science & Information Technology":
                return "CSE";
            case "Electronics & Communication":
                return "ECE";
            case "Civil Engineering":
                return "Civil";
            case "Electrical Engineering":
                return "EEE";
            case "Mechanical Engineering":
                return "Mech";
            default:
                return "Miscellaneous";
        }
    }
}
