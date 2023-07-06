package lk.directpay.Login.controllers;

import lk.directpay.Login.entities.User;
import lk.directpay.Login.model.UserDTO;
import lk.directpay.Login.services.UserService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String homePage(){
        return "home";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDTO user = new UserDTO();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDTO user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDTO> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/excel.xls")
    public void exportUsers(HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.isAuthenticated()) {
            List<User> users = userService.getAllUsers();

            try {
                WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
                WritableSheet sheet = workbook.createSheet("User Details", 0);

                String[] headers = {"ID", "Name", "Email"};
                for (int col = 0; col < headers.length; col++) {
                    Label headerLabel = new Label(col, 0, headers[col]);
                    sheet.addCell(headerLabel);
                }

                int rowNum = 1;
                for (User user : users) {
                    sheet.addCell(new Number(0, rowNum, user.getId()));
                    sheet.addCell(new Label(1, rowNum, user.getName()));
                    sheet.addCell(new Label(2, rowNum, user.getEmail()));
                    rowNum++;
                }

                for (int col = 0; col < headers.length; col++) {
                    sheet.setColumnView(col, 15);
                }


                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=user_details.xls");

                workbook.write();
                workbook.close();
            } catch (IOException | WriteException e) {
                e.printStackTrace();
            }
        } else {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }
    }
}

