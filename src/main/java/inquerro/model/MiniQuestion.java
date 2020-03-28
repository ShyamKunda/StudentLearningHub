package inquerro.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MiniQuestion {

    private String content;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    private List<String> tags2;
    private String explanation;
}
