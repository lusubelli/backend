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
        return new User(signinRequest.getEmail(), signinRequest.getPassword(), null, null);
    }

    private boolean isSamePassword(String storedPassword, String inputPassword) {
        return storedPassword.equals(inputPassword);
    }
}
