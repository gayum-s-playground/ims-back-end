package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.TeacherTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.List;

@RestController
@RequestMapping("/teachers")
@CrossOrigin
public class TeacherHttpController {

    private final HikariDataSource pool;

    public TeacherHttpController(){
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("961021");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.addDataSourceProperty("maximumPoolSize",10);
        pool = new HikariDataSource(config);

    }

    @PreDestroy
    public void destroy(){
        pool.close();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json" ,consumes = "application/json")
    public TeacherTO createTeacher(@RequestBody @Validated(TeacherTO.Create.class) TeacherTO teacher){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement createStm = connection.prepareStatement("INSERT INTO teacher(name, contact) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            createStm.setString(1,teacher.getName());
            createStm.setString(2,teacher.getContact());
            createStm.executeUpdate();
            ResultSet generatedKeys = createStm.getGeneratedKeys();
            generatedKeys.next();
            int teacherId = generatedKeys.getInt(1);
            teacher.setId(teacherId);
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{teacherId}", consumes = "application/json")
    public void updateTeacher(@RequestBody @Validated(TeacherTO.Update.class) TeacherTO teacher,
                              @PathVariable int teacherId){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement updateStm = connection.prepareStatement("SELECT * FROM teacher WHERE id = ?");
            updateStm.setInt(1,teacherId);
            if(!updateStm.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher not found.");
            }
            PreparedStatement stm = connection.prepareStatement("UPDATE teacher SET name = ?, contact = ? WHERE id = ?");
            stm.setString(1,teacher.getName());
            stm.setString(2,teacher.getContact());
            stm.setInt(3,teacherId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{teacherId}")
    public void deleteTeacher(@PathVariable int teacherId){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stm.setInt(1,teacherId);
            if(!stm.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher id mismatched");
            }
            PreparedStatement delStm = connection.prepareStatement("DELETE FROM teacher WHERE id=?");
            delStm.setInt(1,teacherId);
            delStm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{teacherId}", produces = "application/json")
    public TeacherTO getTeacherDetail(@PathVariable int teacherId){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            preparedStatement.setInt(1,teacherId);
            TeacherTO teacher;
            ResultSet rst = preparedStatement.executeQuery();
            if(!rst.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher was not found.");
            }else {
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String contact = rst.getString("contact");
                teacher = new TeacherTO(id, name, contact);
            }
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public List<TeacherTO> getAllTeachers(){
        return null;
    }
}
