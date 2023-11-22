package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTO implements Serializable {

    @Null(message = "Id should be null",groups = Create.class)
    @NotNull(message = "ID should not be null",groups = Update.class)
    private Integer id;
    @NotNull(message = "Name should not be empty")
    @Pattern(regexp = "[a-zA-Z ]+")
    private String name;
    @NotNull(message = "Contact should not be empty")
    @Pattern(regexp = "\\d{3}-\\d{7}")
    private String contact;

    public interface Create extends Default {}  //CREATE
    public interface Update extends Default {}  //UPDATE
}
