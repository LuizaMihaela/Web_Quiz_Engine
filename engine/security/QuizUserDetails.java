package engine.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class QuizUserDetails {

    UserDetails userDetails(engine.model.User user) {
        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }
}
