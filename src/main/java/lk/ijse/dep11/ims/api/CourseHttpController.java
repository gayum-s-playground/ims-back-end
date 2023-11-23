package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.Course;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import javax.validation.Valid;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin
public class CourseHttpController {
    private final HikariDataSource pool;

    public CourseHttpController(){
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("mysql");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("maximumPoolSize",10);
        pool = new HikariDataSource(config);
    }

    @PreDestroy
    public void destroy(){
        pool.close();
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json",produces = "application/json")
    public Course createCourse(@RequestBody @Valid Course course){
        try(Connection connection = pool.getConnection()) {

            PreparedStatement stmCreate = connection.prepareStatement("INSERT INTO course (name, duration_in_months) " +
                    "VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stmCreate.setString(1,course.getName());
            stmCreate.setInt(2,course.getDurationInMonth());
            int i = stmCreate.executeUpdate();

            ResultSet generatedKeys = stmCreate.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            course.setCourse_id(id);
            return course;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{id}",consumes = "application/json")
    public void updateCourse(@PathVariable int id, @RequestBody @Valid Course course){
        try(Connection connection = pool.getConnection()) {
            PreparedStatement stmFind = connection.prepareStatement("SELECT * FROM course WHERE id = ?");
            stmFind.setInt(1,id);
            if (!stmFind.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found");
            }

            PreparedStatement stmUpdate = connection.prepareStatement("UPDATE course SET name=?, " +
                    "duration_in_months=? WHERE id=?");
            stmUpdate.setString(1,course.getName());
            stmUpdate.setInt(2,course.getDurationInMonth());
            stmUpdate.setInt(3,id);
            stmUpdate.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable("id") int courseId){
        try(Connection connection = pool.getConnection()) {
            PreparedStatement stmFind = connection.prepareStatement("SELECT * FROM course WHERE id = ?");
            stmFind.setInt(1,courseId);
            if (!stmFind.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found");
            }

            PreparedStatement stmDelete = connection.prepareStatement("DELETE FROM course WHERE id=?");
            stmDelete.setInt(1,courseId);
            stmDelete.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping(produces = "application/json")
    public List<Course> getAllCourses(){
        try (Connection connection = pool.getConnection()){
            Statement stmGetAll = connection.createStatement();
            ResultSet resultSet = stmGetAll.executeQuery("SELECT * FROM course ORDER BY id");
            ArrayList<Course> courseList = new ArrayList<>();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int durationInMonth = resultSet.getInt("duration_in_months");
                courseList.add(new Course(id,name,durationInMonth));
            }
            return courseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/{id}",produces = "application/json")
    public Course getCourse(@PathVariable("id") int courseId){
        try (Connection connection = pool.getConnection()){
            PreparedStatement stmGetCourse = connection.prepareStatement("SELECT * FROM course WHERE id = ?");
            stmGetCourse.setInt(1,courseId);
//            Statement stmGetCourse = connection.createStatement();
//            ResultSet resultSet = stmGetCourse.executeQuery("SELECT * FROM course WHERE id = 'courseId'");
            ResultSet resultSet = stmGetCourse.executeQuery();
            String name;
            int durationInMonth;
            if (!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found");
            }else {
                name = resultSet.getString("name");
                durationInMonth = resultSet.getInt("duration_in_months");
            }
            return new Course(courseId,name,durationInMonth);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
