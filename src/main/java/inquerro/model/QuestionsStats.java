package inquerro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionsStats {


    private Long id;
    private String title;
    private int count;
    private Map<String, Integer> subTopics;
}
