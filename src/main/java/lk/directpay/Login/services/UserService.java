package lk.directpay.Login.services;

import lk.directpay.Login.entities.User;
import lk.directpay.Login.model.UserDTO;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDto);

    User findByEmail(String email);

    List<UserDTO> findAllUsers();
}