package fr.usubelli.accounting.backend.usecase;

import fr.usubelli.accounting.backend.dto.SigninRequest;
import fr.usubelli.accounting.backend.dto.User;
import fr.usubelli.accounting.backend.port.UserGateway;

public class Signin {

    private final UserGateway userGateway;

    public Signin(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public User signin(SigninRequest signinRequest) {
        final User user = userGateway.findUser(signinRequest.getEmail());
        if (user != null && isSamePassword(user.getPassword(), signinRequest.getPassword())) {
           return new User(signinRequest.getEmail(), null, null, User.UserState.CREATED);
        }
        return null;
    }

    private boolean isSamePassword(String storedPassword, String inputPassword) {
        return storedPassword.equals(inputPassword);
    }
}
