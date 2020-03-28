package inquerro.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import inquerro.model.User;
import inquerro.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistrationDto registration);
}
