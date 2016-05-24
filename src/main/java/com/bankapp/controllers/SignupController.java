package com.bankapp.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Message;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.forms.SignupForm;
import com.bankapp.listeners.OnRegistrationCompleteEvent;
import com.bankapp.models.Account;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;
import com.bankapp.services.IUserService;
import com.bankapp.validators.RecaptchaFormValidator;
import com.bankapp.services.IAccountService;
import com.bankapp.services.IMailService;

@Controller
public class SignupController {
    private final Logger LOGGER = Logger.getLogger(SignupController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IMailService mailService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Value("${com.bankapp.account.default_balance}")
    private double defaultBalance;

    @Value("${com.bankapp.account.default_critical_limit}")
    private double defaultCriticalLimit;

    final private String signupViewName = "registration/signup";

    private final RecaptchaFormValidator recaptchaFormValidator;

    @ModelAttribute("recaptchaSiteKey")
    public String getRecaptchaSiteKey(@Value("${recaptcha.site-key}") String recaptchaSiteKey) {
        return recaptchaSiteKey;
    }

    @Autowired
    public SignupController(RecaptchaFormValidator recaptchaFormValidator) {
        this.recaptchaFormValidator = recaptchaFormValidator;
    }

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(recaptchaFormValidator);
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView getSignupPage() {
        ModelAndView modelAndView = new ModelAndView(signupViewName, "form", new SignupForm());
        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView registerUser(@Valid @ModelAttribute("form") SignupForm form, BindingResult resultForm,
            HttpServletRequest request) {

        ModelAndView mv = new ModelAndView(signupViewName);

        if (resultForm.hasErrors()) {
            mv.addObject("form", form);
            mv.addObject("errors", resultForm.getAllErrors());

            String logMessage = String.format(
                    "Registration for user account failed with information: [Email=%s, Message=%s]", form.getEmail(),
                    resultForm.getAllErrors());
            LOGGER.info(logMessage);
            return mv;
        }
        Role role = form.getRole();

        if (!role.getName().equalsIgnoreCase("ROLE_CUSTOMER") && !role.getName().equalsIgnoreCase("ROLE_MERCHANT")) {
            mv.addObject("form", form);
            mv.addObject("errors", resultForm.getAllErrors());
            mv.addObject("message", new Message("error", "You can only be a customer or a merchant"));

            String logMessage = String.format(
                    "Registration for user account failed with information: [Email=%s, Message=%s]", form.getEmail(),
                    "You can only be a customer or a merchant");
            LOGGER.info(logMessage);
            return mv;
        }

        User newUser = new User();
        newUser.setUsername(form.getUsername());
        newUser.setEmail(form.getEmail());
        newUser.setPassword(form.getPassword());
        newUser.setAddress(form.getAddress());
        newUser.setDateOfBirth(form.getDateOfBirth());
        newUser.setPhoneNumber(form.getPhoneNumber());
        newUser.setGender(form.getGender());
        newUser.setSecurityQuestion(form.getSecurityQuestion());
        newUser.setSecurityAnswer(form.getSecurityAnswer());
        newUser.setTypeOfAccount(form.getTypeOfAccount());

        String logMessage = String.format("Registering user account with information: {%s, %s}", newUser, role);
        LOGGER.info(logMessage);

        User registered = createUserAccount(newUser, role.getName());
        if (registered == null) {
            String message = String.format("This email is already taken");
            mv.addObject("message", message);
            mv.addObject("form", form);
            return mv;
        }
        try {
            eventPublisher
                    .publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        } catch (Exception e) {
            String message = String.format("Action: %s, Message: %s", "signup", e.getMessage());
            LOGGER.error(message);

            mv.addObject("message", e.getMessage());
            mv.addObject("form", form);
            return mv;
        }

        mv.setViewName("registration/activationInfo");
        mv.addObject("username", newUser.getUsername());
        mv.addObject("email", newUser.getEmail());
        return mv;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(HttpServletRequest request, Model model, @RequestParam("token") String token,
            RedirectAttributes attributes) {
        String logMessage = String.format("Verifying user account with information: {token = %s}", token);
        LOGGER.info(logMessage);

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = String.format("The token is invalid, please register again!");
            attributes.addFlashAttribute("message", new Message("error", message));
            return "redirect:/";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String message = String.format("The verification token has expired. Please register again!");
            String url = getAppUrl(request) + "/resendRegistrationToken?token=" + token;
            attributes.addFlashAttribute("message", new Message("error", message));
            attributes.addFlashAttribute("url", url);
            return "registration/activationFailed";
        }

        // Check if user is enabled
        if (user == null) {
            attributes.addFlashAttribute("message",
                    new Message("error", "There seems to be no account linked to this token!"));
        } else if (!user.isEnabled()) {
            user.setEnabled(true);
            userService.saveRegisteredUser(user);

            // Create user account
            Account userAccount;
            String typeOfAccount = user.getTypeOfAccount();
            switch (typeOfAccount) {
            case "S":
                userAccount = new Account(user, "Savings", defaultBalance, defaultCriticalLimit);
                break;
            case "C":
            default:
                userAccount = new Account(user, "Checkings", defaultBalance, defaultCriticalLimit);
            }

            accountService.saveAccount(userAccount);

            logMessage = String.format("User %s has been verified, created new account [%s]", user.getId(),
                    userAccount);
            LOGGER.info(logMessage);

            attributes.addFlashAttribute("message",
                    new Message("success", "Your account has been verified and enabled!"));

        } else {
            attributes.addFlashAttribute("message", new Message("error", "Your account has already been verified!"));
        }

        return "redirect:/";
    }

    @RequestMapping(value = "/resendRegistrationToken", method = RequestMethod.GET)
    public ModelAndView resendRegistrationToken(HttpServletRequest request,
            @RequestParam("token") String existingToken) {
        String newToken = userService.generateNewVerificationToken(existingToken).getToken();
        User user = userService.getUserByVerificationToken(newToken);

        String recipientAddress = user.getEmail();
        String userName = user.getUsername();
        String subject = String.format("My ASU Bank - Resending Activation");
        String confirmationUrl = getAppUrl(request) + "/registrationConfirm?token=" + newToken;

        String textBody = String.format(
                "Dear %s, <br /><br />Here is your new account verification link:<br />"
                        + "<a href='%s'>%s</a>.<br /><br />Regards,<br />My ASU Bank",
                userName, confirmationUrl, confirmationUrl);
        mailService.sendEmail(recipientAddress, subject, textBody);

        ModelAndView mv = new ModelAndView("registration/activationInfo");
        mv.addObject("email", recipientAddress);
        mv.addObject("username", userName);
        return mv;
    }

    private User createUserAccount(final User newUser, final String roleName) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(newUser, roleName);
        } catch (final EmailExistsException e) {
            return null;
        }
        return registered;
    }

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
