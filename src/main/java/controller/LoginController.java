package controller;

import model.UserDAO;

public class LoginController {

    public boolean login(String email, String password) {
        return UserDAO.validateLogin(email, password);
    }
}

