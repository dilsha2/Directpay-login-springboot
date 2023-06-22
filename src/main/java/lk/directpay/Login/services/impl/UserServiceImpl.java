//package lk.directpay.Login.services.impl;
//
//import lk.directpay.Login.entities.User;
//import lk.directpay.Login.repositories.UserRepository;
//import lk.directpay.Login.services.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Override
//    public User register(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
//}
