package inquerro.web;

import inquerro.model.MathJaxVerification;
import inquerro.model.MiniQuestion;
import inquerro.service.FirebaseService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller

public class MathJaxVerificationController {

    FirebaseService firebaseService;

    public MathJaxVerificationController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @PostMapping("/postContent")
    public ModelAndView verifyMathEquations(@ModelAttribute MathJaxVerification mathJaxData, Map<String, Object> model) throws ExecutionException, InterruptedException {

        if (firebaseService.saveMathJaxEquation(mathJaxData).length() > 0){

            model.put("equation", firebaseService.getEquation());
            return new ModelAndView("uploadQuestion");
        }
        else{

            return new ModelAndView("uploadQuestion");
        }


    }
}
