package com.adijith.mywebapp.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("name")
public class LoginController {

    private AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        super();
        this.authenticationService = authenticationService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String gotoLoginPage() {
        return "forward:/WEB-INF/jsp/loginPage.jsp"; // No change
    }

    @RequestMapping(value = "login", method = RequestMethod.POST) // Correct method and mapping
    public String gotoWelcomePage(@RequestParam String name, @RequestParam String password, ModelMap model) {

        if (authenticationService.authenticate(name, password)) {
            model.put("name", name);
            model.put("password", password);
            return "forward:/WEB-INF/jsp/welcome.jsp"; // Correct forward path
        }

        // If authentication fails, show an error message
        model.put("errorMessage", "Invalid Credentials");
        return "loginPage"; // Correct view name for the login page
    }
}
