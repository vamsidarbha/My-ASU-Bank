package com.bankapp.controllers;

import java.security.Principal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
@Secured("ROLE_CUSTOMER")
public class CustomerController implements Constants {

    private final Logger LOGGER = Logger.getLogger(CustomerController.class);

    @Autowired
    private ITransactionService transactionService;
    
    @Autowired IUserService userService;

    @RequestMapping(value = "/customer/authorizemerchant", method = RequestMethod.GET)
    public ModelAndView authorizemerchant(Principal principal) {
        ModelAndView mv = new ModelAndView("/customer/authorizemerchant");
        mv.addObject("role", "customer");
        User user = userService.getUserFromSession(principal);
        List<Transaction> transactions = transactionService.getMerchantRequests(user, S_PENDING_CUSTOMER_VERIFICATION);
        mv.addObject("transactions", transactions);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "authorize merchant",
                "GET", "Customer", "success", "view authorized merchant");
        LOGGER.info(logMessage);
        return mv;
    }

    @RequestMapping(value = "/customer/approverequest", method = RequestMethod.POST)
    public String approveRequest(@ModelAttribute("transaction") Transaction transaction, BindingResult result,
            RedirectAttributes attributes) {
        Message message;
        String msg = transactionService.actionOnRequest(transaction.getTransactionId(), S_CUSTOMER_VERIFIED);
        String status;
        if (msg.equals(SUCCESS)) {
            message = new Message("success", "Request has been approved ");
            status= "success";
        } else if(msg.equals(ERR_LESS_BALANCE)){
            message = new Message("error", "You have insufficient balance. ");
            status= "less balance";
        }else {
            message = new Message("error", "error please try again");
            status= "error";
        }
        attributes.addFlashAttribute("message", message);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "approve request",
                "POST", "Custtomer", status, message);
        LOGGER.info(logMessage);
        return "redirect:/customer/authorizemerchant";
    }

    @RequestMapping(value = "/customer/declinerequest", method = RequestMethod.POST)
    public String declineRequest(@ModelAttribute("transaction") Transaction transaction, BindingResult result,
            RedirectAttributes attributes) {
        Message message;
        String status;
        String msg = transactionService.actionOnRequest(transaction.getTransactionId(), S_CUSTOMER_DECLINED);
        if (msg.equals(SUCCESS)) {
            status = "success";
            message = new Message("success", "Request has been declined ");
        } else {
            status = "error";
            message = new Message("error", "error please try again");
        }
        attributes.addFlashAttribute("message", message);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "declinerequest",
                "POST", "Custtomer", status, message);
        LOGGER.info(logMessage);
        return "redirect:/customer/authorizemerchant";
    }
}