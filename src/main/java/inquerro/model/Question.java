package inquerro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Question {

    private Long id;
    private String content;
    private String explanation;
    private List<String> options;
    private String answer;
    private String author;
    private List<String> tags;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
    private boolean isDeleted;
    private String strAnswer;


}
