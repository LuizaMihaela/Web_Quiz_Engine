package engine;

import engine.repo.CompletedRepository;
import engine.repo.QuizRepository;
import engine.repo.UserRepository;
import engine.model.Completed;
import engine.model.Quiz;
import engine.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
class QuizController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompletedRepository completedRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @PostMapping(path = "/api/register")
    public void registerUser(@Valid @RequestBody User user) {
        User findUser = userRepository.findByEmail(user.getEmail());

        if (findUser != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        user.setRoles("USER");
        userRepository.save(user);
    }

    @PostMapping (path="/api/quizzes")
    public Quiz addQuizz(@Valid @RequestBody Quiz quiz, Principal principal) {
        quiz.setAuthor(principal.getName());
        quizRepository.save(quiz);
        return quiz;
    }

    @GetMapping(path="/api/quizzes")
    public Page<Quiz> getAllQuizzes(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy)
    {
        return quizRepository.findAll(PageRequest.of(page, pageSize, Sort.by(sortBy)));

    }


    @GetMapping(path="/api/quizzes/completed")
    public Page<Completed> getCompletedQuizzes(
            Principal principal,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "completedAt") String sortBy)

    {

        return completedRepository.findByAuthor (principal.getName(),
                PageRequest.of(page, pageSize, Sort.by(sortBy).descending()));
    }


    @GetMapping(path = "/api/quizzes/{id}")
    public Quiz getQuizById(@PathVariable("id") int id) {

        return quizRepository.findById(id).orElseThrow();
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable int id, Principal principal) {
        Quiz quiz = getQuizById(id);

        if (quizRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        } else if (!principal.getName().equals(quiz.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        } else {
            quizRepository.delete(quiz);
        }
    }


    @PostMapping(path="/api/quizzes/{id}/solve")

    public String solveQuiz(@PathVariable int id, @RequestBody Map<String, int[]> answers, Principal principal) {
        int[] correctAnswer = quizRepository.findById(id).orElseThrow().getAnswer();


        if (Arrays.equals(correctAnswer, answers.get("answer"))
                || correctAnswer == null && (answers.get("answer") == null || answers.get("answer").length == 0)) {

            completedRepository.save(new Completed(id, LocalDateTime.now().toString(), principal.getName()));

            return "{" +
                    "\"success\": true," +
                    "\"feedback\": \"Congratulations, You're right!\"" +
                    "}";
        }

        return "{" +
                "\"success\": false," +
                "\"feedback\": \"Wrong, Please try again!\"" +
                "}";
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not found")
    public Map<String, String> handleNoSuchElementException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("error", e.getClass().getSimpleName());

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid method argument")
    public Map<String, String> handleMethodArgumentNotValidException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("error", e.getClass().getSimpleName());

        return response;
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid credentials")
    public Map<String, String> handleConstraintViolationExceptionHandler(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("error", e.getClass().getSimpleName());

        return response;
    }
}
