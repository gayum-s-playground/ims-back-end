package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course implements Serializable {
    @Null(message = "course_id should be empty")
    private Integer course_id;
    @NotBlank(message = "name can't be empty")
    @Pattern(regexp = "^[A-Za-z0-9 \\-]{2,}$", message = "Course name can contain only [A-Z][a-z][0-9] and -")
    private String name;
    @NotNull(message = "Course duration can't be empty")
    @Positive(message = "Invalid duration")
    private Integer durationInMonth;
}
