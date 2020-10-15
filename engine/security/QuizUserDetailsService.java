package engine.security;

import engine.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class QuizUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizUserDetails quizUserDetails;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return quizUserDetails.userDetails(userRepository.findByEmail(email));
    }
}
