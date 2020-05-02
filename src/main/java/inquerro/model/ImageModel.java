package inquerro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "image_table")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ImageModel {


    public ImageModel(String name, String type, byte[] picByte) {
        this.name = name;
        this.type = type;
        this.picByte = picByte;
    }

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name= "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "picByte", length = 1000)
    private byte[] picByte;

    @Column(name = "questionId")
    private Long questionId;

    @Column(name = "optionIndex")
    private int optionIndex;


}
