
package com.bankapp.controllers;

import java.security.Principal;
import java.util.Date;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
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
import com.bankapp.forms.UserPaymentForm;
import com.bankapp.models.Transaction;
import com.bankapp.services.ITransactionService;

@Controller
@Secured("ROLE_MERCHANT")
public class MerchantController implements Constants {

    private final Logger LOGGER = Logger.getLogger(MerchantController.class);

    @Autowired
    private ITransactionService transactionService;

    @RequestMapping(value = "/merchant/userpayment", method = RequestMethod.GET)
    public ModelAndView askUserPayment() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("form", new UserPaymentForm());
        mv.addObject("role", "merchant");
        mv.setViewName("merchant/userpayment");
        return mv;
    }

    @RequestMapping(value = "/merchant/userpayment", method = RequestMethod.POST)
    public String askUserPayment(@AuthenticationPrincipal UserDetails activeUser, final ModelMap model,
            @ModelAttribute("form") @Valid UserPaymentForm form, BindingResult result, WebRequest request,
            Errors errors, Principal principal, RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl = "redirect:/merchant/myaccount";
        String logMessage;

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "merchant/userpayment";
        }

        String toEmail = activeUser.getUsername();
        String fromEmail = form.getEmail();

        Transaction transaction = new Transaction();
        transaction.setEncryptedAmount(form.getAmount());
        transaction.setComment(form.getComment());
        transaction.setTransferDate(new Date());
        String serviceStatus = transactionService.askCustomerPayment(fromEmail, toEmail, transaction);

        switch (serviceStatus) {

        case ERR_TRANS_DECODE:
        case ERR_TRANS_DECRYPTION:
        case ERR_TRANS_INCORRECT_FORMAT:
        case ERR_TRANS_EXPIRED:
        case ERR_TRANS_LIMIT:
        case ERR_LESS_BALANCE:
        case ERR_ACCOUNT_NOT_EXISTS:
        case ERR_SAME_USER:
        case ERR_UNHANDLED:
            status = "error";
            message = serviceStatus;
            redirectUrl = "redirect:/merchant/userpayment";
            break;
        case SUCCESS:
            status = "success";
            message = "Notified the user for approval and transaction is in pending state till customers approval";
            redirectUrl = "redirect:/merchant/myaccount";
            break;
        default:
            status = "error";
            message = ERR_UNHANDLED;
            redirectUrl = "redirect:/merchant/userpayment";
            break;
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", "merchant");

        logMessage = String.format("[Action=%s, Method=%s][Status=%s][Message=%s]", "userpayment", "POST",
                serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

}
