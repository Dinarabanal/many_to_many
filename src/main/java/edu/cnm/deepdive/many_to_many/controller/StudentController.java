package edu.cnm.deepdive.many_to_many.controller;


import edu.cnm.deepdive.many_to_many.model.dao.ProjectRepository;
import edu.cnm.deepdive.many_to_many.model.dao.StudentRepository;
import edu.cnm.deepdive.many_to_many.model.entity.Project;
import edu.cnm.deepdive.many_to_many.model.entity.Student;
import java.util.List;
import java.util.NoSuchElementException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ExposesResourceFor(Student.class)
@RequestMapping("/students")
public class StudentController {

  private StudentRepository studentRepository;
  private ProjectRepository projectRepository;

  @Autowired
  public StudentController(
      StudentRepository studentRepository,
      ProjectRepository projectRepository) {
    this.studentRepository = studentRepository;
    this.projectRepository = projectRepository;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Student> list() {
    return studentRepository.findAllByOrderByNameAsc();
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Student> post(@RequestBody Student student) {
    studentRepository.save(student);
    return ResponseEntity.created(student.getHref()).body(student);

  }

  @GetMapping(value = "{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Project get(@PathVariable("studentId") long projectId) {
    return projectRepository.findById(projectId).get();
  }

  @Transactional
  @DeleteMapping(value = "{studentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("studentId") long studentId) {
    Student student = get(studentId);
    List <Project> projects = student.getProjects();
    for (Project project : projects) {
     project.getStudents().remove(student);
    }
    projectRepository.saveAll(projects);
    StudentRepository.delete(student);

  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
  @ExceptionHandler(NoSuchElementException.class)
  public void notFound() {

  }

}