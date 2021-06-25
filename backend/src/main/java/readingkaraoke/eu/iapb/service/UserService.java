package readingkaraoke.eu.iapb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import readingkaraoke.eu.iapb.config.PasswordEncoder;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.exception.RegistrationException;
import readingkaraoke.eu.iapb.exception.UserException;
import readingkaraoke.eu.iapb.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void isUserValidEditor(User user) throws UserException {
        // add additional checks if there's time to implement seperate roles
        if(!user.isAdmin()) {
            throw new UserException("Kasutajal puuduvad admin õigused");
        }
    }

    public User registerNewUser(User user) throws RegistrationException {
        user.setAdmin(false);
        if(user.getUsername() == null || user.getUsername().length() < 4) {
            throw new RegistrationException("Kasutajanimi liiga lühike.");
        }

        if(user.getPassword() == null || user.getPassword().length() < 4) {
            throw new RegistrationException("Parool liiga lühike.");
        }
        if(userRepository.findByUsername(user.getUsername()) != null) {
            throw new RegistrationException("Kasutajanimi kasutuses.");
        }

        user.setPassword(passwordEncoder.encoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUser(User user) throws UserException {
//        User authUser = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        User authUser = userRepository.findByUsername(user.getUsername());
        if(authUser == null) {
            throw new UserException("Vale kasutajanimi või parool.");
        }
        if(!passwordEncoder.encoder().matches(user.getPassword(), authUser.getPassword())) {
            // Backup auth for old plaintext password accounts.
            // The first users in live don't have encrypted passwords.
            // Need to encrypt or remove old users and then remove this.
            authUser = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
            if(authUser == null) {
                throw new UserException("Vale parool.");
            }
        }
        return authUser;
    }

}
