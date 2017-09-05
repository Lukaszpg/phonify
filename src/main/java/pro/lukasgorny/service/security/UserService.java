package pro.lukasgorny.service.security;

import pro.lukasgorny.model.User;

/**
 * Created by lukaszgo on 2017-05-25.
 */
public interface UserService {

    void save(User user);
    User findByEmail(String email);
}
