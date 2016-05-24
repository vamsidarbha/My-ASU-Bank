/**
 * 
 */
package com.bankapp.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.forms.AddEmployeeForm;
import com.bankapp.forms.EmployeeProfileUpdateForm;
import com.bankapp.forms.UpdateUsersForm;
import com.bankapp.forms.ViewByEmailForm;
import com.bankapp.listeners.OnRegistrationCompleteEvent;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Role;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;;;

/**
 * @author Nitesh Dhanpal
 *
 */

@Controller
@Secured("ROLE_MANAGER")
public class SystemManagerController implements Constants {

    @Autowired
    private ISystemManagerService managerService;

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private IProfileRequestService profileReq;

    @Autowired
    private ITransactionService transactionService;

    private final Logger LOGGER = Logger.getLogger(SystemManagerController.class);

    @RequestMapping(value = "/manager/myaccount", method = RequestMethod.GET)
    public ModelAndView getMyaccount(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        String Username = loggedInUser.getUsername();
        mv.addObject("username", Username);
        mv.addObject("role", "ROLE_MANAGER");
        mv.setViewName("manager/myaccount");
        return mv;
    }

    @RequestMapping(value = "/manager/criticaltransaction", method = RequestMethod.GET)
    public ModelAndView getCriticalTransaction() {
        List<Transaction> transactions = managerService.getTransactionsByStatus(S_OTP_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("critical", transactions);
        mv.setViewName("/manager/viewTransaction");
        return mv;
    }

    @RequestMapping(value = "/manager/pendingtransaction", method = RequestMethod.GET)
    public ModelAndView getInitiatedTransaction() {
        List<Transaction> transactions = managerService.getTransactionsByStatus(S_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("transactions", transactions);
        mv.setViewName("/manager/viewPending");
        return mv;
    }

    @RequestMapping(value = "/manager/profilerequests", method = RequestMethod.GET)
    public ModelAndView getProfileRequests() {
        List<ProfileRequest> list = profileReq.getRequestsByStatus(S_PROFILE_UPDATE_PENDING);
        List<ProfileRequest> list1 = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < list.size(); i++) {
            ProfileRequest Req = list.get(i);
            String role = Req.getRole().getName();
            if (role.equals("ROLE_CUSTOMER") || role.equals("ROLE_MERCHANT")) {
                list1.add(k, Req);
                k++;
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("profileRequests", list1);
        mv.setViewName("/manager/profileRequests");
        return mv;
    }

    @RequestMapping(value = "/manager/approveprofilerequest", method = RequestMethod.POST)
    public String approveProfileRequests(@ModelAttribute("row") ProfileRequest Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal, RedirectAttributes attributes) {
        ProfileRequest profile = managerService.getProfilebRequestByRId(Id.getrId());
        String Address = profile.getAddress();
        Date DoB = profile.getDateOfBirth();
        String Ph = profile.getPhoneNumber();

        User user = profile.getUser();
        user.setAddress(Address);
        user.setDateOfBirth(DoB);
        user.setPhoneNumber(Ph);
        user.setSecurityAnswer(profile.getSercurityAnswer());
        user.setSecurityQuestion(profile.getSecurityQuestion());
        managerService.saveUser(user);

        String status = managerService.approveProfileRequest(profile);

        attributes.addFlashAttribute("message", new Message(status, "Request has been approved"));
        attributes.addFlashAttribute("role", "manager");
        return "redirect:/manager/profilerequests";
    }

    @RequestMapping(value = "/manager/getUserByEmail", method = RequestMethod.GET)
    public ModelAndView getUserEmail() {
        ModelAndView modelAndView = new ModelAndView("/manager/viewUserByEmailForm", "form", new ViewByEmailForm());
        return modelAndView;
    }

    @RequestMapping(value = "/manager/approvetransaction", method = RequestMethod.POST)
    public String approveTransaction(@ModelAttribute("row") Transaction txn, BindingResult result, WebRequest request,
            Errors errors, Principal principal, RedirectAttributes attributes) {
        Transaction transaction = transactionService.getTransactionsById(txn.getTransactionId());
        String serviceStatus = transactionService.executeTransaction(transaction);
        String status;
        String message;
        String redirectUrl;
        String logMessage;
        String role = "manager";
        switch (serviceStatus) {
        case SUCCESS:
            status = "success";
            message = "Money transfered successfully";
            redirectUrl = "redirect:/" + role + "/criticaltransaction";
            break;
        default:
            status = "error";
            message = ERR_UNHANDLED;
            redirectUrl = "redirect:/" + role + "/criticaltransaction";
            break;
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", role);

        logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]",
                "approve critical transaction", "POST", role, serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

    @RequestMapping(value = "/manager/addUserForm", method = RequestMethod.POST)
    public String addUser(final ModelMap model, @ModelAttribute("form") @Valid AddEmployeeForm form,
            BindingResult result, Errors errors, Principal principal, HttpServletRequest request,
            RedirectAttributes attributes) throws UserNameExistsException {
        User user = new User();
        Role role = form.getRole();
        String redirectUrl = "redirect:/manager/addUserForm";
        String status = "success";

        if (result.hasErrors()) {
            status = "error";
            model.addAttribute("form", form);
            String message = "Invalid Details";
            model.addAttribute("message", new Message(status, message));
            return "/manager/addUserForm";
        }

        if (!role.getName().equalsIgnoreCase("ROLE_CUSTOMER") && !role.getName().equalsIgnoreCase("ROLE_MERCHANT")) {
            model.addAttribute("form", form);
            attributes.addFlashAttribute("message",
                    new Message("error", "You can only create a customer or a merchant"));

            String logMessage = String.format("External user creation failed: [Email=%s, Message=%s]", form.getEmail(),
                    "You can only create a customer or a merchant");
            LOGGER.info(logMessage);
            return "redirect:/manager/addUserForm";
        }

        User registered = null;

        user.setEmail(form.getEmail());
        user.setUsername(form.getUsername());
        user.setDateOfBirth(form.getDateOfBirth());

        String temporaryPassword = OneTimePassword.generateOTP();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        String message = "External user is added successfully";
        try {
            registered = userService.registerNewUserAccount(user, role.getName());
        } catch (EmailExistsException e1) {
            status = "error";
            message = String.format("Action: %s, Message: %s", "signup", e1.getMessage());
            LOGGER.error(message);
            redirectUrl = "redirect:/manager/addUserForm";
        }

        if (registered != null) {
            try {
                eventPublisher.publishEvent(
                        new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
            } catch (Exception e) {
                status = "error";
                message = String.format("Action: %s, Message: %s", "signup", e.getMessage());
                LOGGER.error(message);
            }
            userService.generateTemporaryPassword(registered);
        }
        attributes.addFlashAttribute("message", new Message(status, message));
        return redirectUrl;

    }

    @RequestMapping(value = "/manager/viewUserByEmailForm", method = RequestMethod.POST)
    public ModelAndView getUserByEmail(final ModelAndView model, @ModelAttribute("form") @Valid ViewByEmailForm form,
            BindingResult result, Errors errors, Principal principal, RedirectAttributes attributes) {

        User user = null;
        String status = "success";
        String message = "";

        if (result.hasErrors()) {
            model.addObject("form", form);
            model.setViewName("/manager/viewUserByEmailForm");
            return model;
        }

        if (userService.emailExist(form.getEmail())) {
            try {
                user = managerService.viewUserByEmail(form.getEmail());
            } catch (Exception e) {
                status = "error";
                message = String.format("Message: %s", e.getMessage());
                LOGGER.error(message);
            }

        } else {
            status = "error";
            message = String.format("Action: EmailDoesNotExist");
            // System.out.println(message);
            model.addObject("message", new Message(status, message));
            model.setViewName("/manager/viewUserByEmailForm");
            String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]",
                    "viewUserByEmailForm", "POST", "manager", "error", message);
            LOGGER.info(logMessage);
            return model;

        }

        model.addObject("viewuser", user);
        model.setViewName("/manager/viewUser");

        return model;

    }

    @RequestMapping(value = "/manager/update", method = RequestMethod.GET)
    public ModelAndView getCustomerAndMerchantDetails() {
        ModelAndView mv = new ModelAndView("/manager/viewUsers");
        List<User> customers = userService.getCustomers();
        List<User> merchants = userService.getMerchants();
        List<User> newList = new ArrayList<User>(customers);
        newList.addAll(merchants);
        UpdateUsersForm form = new UpdateUsersForm();
        form.setUsers(newList);
        mv.addObject("form", form);
        return mv;
    }

    @RequestMapping(value = "/manager/update", method = RequestMethod.POST)
    public String updateCustomerAndMerchantDetails(@Valid @ModelAttribute("user") EmployeeProfileUpdateForm updatedUser,
            BindingResult result, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("message", new Message("error",
                    "Update details incorrect [Phone# should be 10 digits, and DoB in MM/dd/yyyy format]"));
            return "redirect:/manager/update";
        }

        String status, message;
        User newUser = new User();
        newUser.setUsername(updatedUser.getUsername());
        newUser.setAddress(updatedUser.getAddress());
        newUser.setDateOfBirth(updatedUser.getDateOfBirth());
        newUser.setPhoneNumber(updatedUser.getPhoneNumber());

        String serviceStatus = userService.updateUser(updatedUser.getId(), newUser);

        switch (serviceStatus) {

        case SUCCESS:
            status = "success";
            message = "Employee details have been updated";
            break;
        case ERROR:
        default:
            status = "error";
            message = "Oops, something went wrong. Please try again!";
        }

        attributes.addFlashAttribute("message", new Message(status, message));

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "Update details",
                "POST", "manager", status, serviceStatus);
        LOGGER.info(logMessage);

        return "redirect:/manager/update";
    }

    @RequestMapping(value = "/manager/deleteUsers", method = RequestMethod.GET)
    public ModelAndView deleteUser() {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("/manager/deleteUser");
        List<User> users = userService.displayDeleteUsers();
        mv.addObject("users", users);
        return mv;
    }

    @RequestMapping(value = "/manager/deleteUsers", method = RequestMethod.POST)
    public String deleteManager(@ModelAttribute("email") String email, BindingResult result,
            RedirectAttributes attributes) {
        String status, message;
        String serviceStatus = userService.deleteExternalUser(email);
        switch (serviceStatus) {
        case SUCCESS:
            status = "success";
            message = String.format("User %s has been deleted", email);
            break;
        case ERR_ACCOUNT_NOT_EXISTS:
        case ERR_UNHANDLED:
        default:
            status = "error";
            message = serviceStatus;
        }

        attributes.addFlashAttribute("message", new Message(status, message));

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "deleteUsers",
                "POST", "manager", status, message);
        LOGGER.info(logMessage);

        return "redirect:/manager/deleteUsers";
    }

    @RequestMapping(value = "/manager/addUserForm", method = RequestMethod.GET)
    public ModelAndView getUserAddage() {
        ModelAndView modelAndView = new ModelAndView("/manager/addUserForm", "form", new AddEmployeeForm());
        return modelAndView;
    }

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
