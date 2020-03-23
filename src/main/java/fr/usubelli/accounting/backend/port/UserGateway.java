package fr.usubelli.accounting.backend.port;

import fr.usubelli.accounting.backend.dto.User;
import fr.usubelli.accounting.backend.exception.UserAlreadyExistsException;

public interface UserGateway {

    User createUser(User user) throws UserAlreadyExistsException;

    User findUser(String email);

    User updateUser(User user);

}
