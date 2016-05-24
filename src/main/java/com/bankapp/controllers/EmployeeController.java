package com.bankapp.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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
import com.bankapp.models.Account;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
@Secured("ROLE_EMPLOYEE")

public class EmployeeController implements Constants {
    @Autowired
    private ISystemManagerService managerService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IProfileRequestService profileRequestService;

    @Autowired
    private IUserService userService;
    
    private final Logger LOGGER = Logger.getLogger(EmployeeController.class);

    // VIEW TRANSACTIONS
    @RequestMapping(value = "/employee/myaccount", method = RequestMethod.GET)
    public ModelAndView getMyAccount(Principal principal) {
        ModelAndView mv = new ModelAndView();
        if (userService.hasMissingFields(principal)) {
            mv.addObject("message",
                    new Message("error", "You are missing important details. Please update your profile urgently"));
        }
        mv.setViewName("employee/myaccount");
        return mv;
    }

    // VIEW TRANSACTIONS
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.GET)
    public ModelAndView getPendingTransaction() {
        List<Transaction> transactions = transactionService.getPendingTransactions();
        ModelAndView mv = new ModelAndView();
        mv.addObject("transactions", transactions);
        mv.setViewName("employee/viewTransactions");
        return mv;
    }

    @RequestMapping(value = "/employee/requests", method = RequestMethod.GET)
    public ModelAndView getPendingProfileRequests() {

        List<ProfileRequest> requests = profileRequestService.getRequestsByStatus(S_PROFILE_UPDATE_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("requests", requests);
        mv.setViewName("employee/viewRequests");
        return mv;
    }

    @RequestMapping(value = "/employee/requests", method = RequestMethod.POST, params = "action=Authorize")
    public String authorizeProfileRequests(@ModelAttribute("row") ProfileRequest profileRequest, BindingResult result,
            RedirectAttributes attributes) {
        ProfileRequest pRequest = profileRequestService.getRequestById(profileRequest.getrId());
        String serviceStatus = profileRequestService.authorizeRequest(pRequest);
        String status="";
        String message ;
        if (serviceStatus.equalsIgnoreCase(S_PROFILE_UPDATE_VERIFIED)) {
            status = "success";
            message= "Request has been approved successfully";
            attributes.addFlashAttribute("message", new Message("success", message));
        } else {
            status = "error";
            message= "Request was not updated";
            attributes.addFlashAttribute("message", new Message("error", serviceStatus));
        }
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "transactions Authorize",
                "POST", "employee", status, message);
        LOGGER.info(logMessage);
        return "redirect:/employee/requests";
    }

    // DECLINE USER_PROFILE
    @RequestMapping(value = "/employee/requests", method = RequestMethod.POST, params = "action=Decline")
    public String declineProfileRequests(@ModelAttribute("row") ProfileRequest profileRequest, BindingResult result,
            WebRequest request, Errors errors, RedirectAttributes attributes) {
        ProfileRequest pRequest = profileRequestService.getRequestById(profileRequest.getrId());
        String serviceStatus = profileRequestService.declineRequest(pRequest);
        String status="";
        String message ;
        if (serviceStatus.equalsIgnoreCase(S_PROFILE_UPDATE_DECLINED)) {
            status="success";
            message=  "Request has been declined successfully";
            attributes.addFlashAttribute("message", new Message("success",message));
        } else {
            status="error";
            message=  "Request not declined some error occured";
            attributes.addFlashAttribute("message", new Message("error", message));
        }

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "transactions Decline",
                "POST", "employee", status, message);
        LOGGER.info(logMessage);
        return "redirect:/employee/requests";
    }

    // EMPLOYEE AUTHORIZE TRANSACTION
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.POST, params = "action=Authorize")
    public String approvetransaction(@ModelAttribute("row") Transaction txn, BindingResult result, WebRequest request,
            Errors errors, RedirectAttributes attributes) {
        Transaction transaction = managerService.getTransactionById(txn.getTransactionId());

        Account FromAccount = transaction.getFromAccount();
        Account ToAccount = transaction.getToAccount();
        Double AmountToBeSent = transaction.getAmount();

        Double FromAccountBalance = FromAccount.getBalance();

        String str = "";
        String status ="";
        if (FromAccountBalance > AmountToBeSent) {
            managerService.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
            Double ToAccountBalance = ToAccount.getBalance();
            managerService.reflectChangesToReceiver(ToAccount, ToAccountBalance, AmountToBeSent);
            status = "success";
            str = managerService.approveTransaction(transaction);
            attributes.addFlashAttribute("message", new Message("success", str));
        } else {
            status = "less balance";
            str = managerService.declineTransaction(transaction);
            attributes.addFlashAttribute("message", new Message("error", str));
        }
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "transactions Authorize",
                "POST", "employee", status, str);
        LOGGER.info(logMessage);
        return "redirect:/employee/transactions";
    }

    // EMPLOYEE DECLINE TRANSACTION
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.POST, params = "action=Decline")
    public String declinetransaction(@ModelAttribute("row") Transaction txn, BindingResult result, WebRequest request,
            Errors errors, RedirectAttributes attributes) {

        Transaction transaction = managerService.getTransactionById(txn.getTransactionId());
        String str = managerService.declineTransaction(transaction);
        attributes.addFlashAttribute("message", new Message("success", str));
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "transactions Decline",
                "POST", "employee", "success", "Decline transaction");
        LOGGER.info(logMessage);
        return "redirect:/employee/transactions";
    }

    // EMPLOYEE MODIFY TRANSACTION
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.POST, params = "action=Modify")
    public ModelAndView modifytransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = managerService.getTransactionById(Id.getTransactionId());

        /*
         * Account ToAccount = transaction.getToAccount(); String to_acct =
         * ToAccount.getAccId(); String a = to_acct.toString(); Double
         * AmountToBeSent = transaction.getAmount(); Date transferDate =
         * transaction.getTransferDate();
         */
        mv.addObject("transaction", transaction);
        mv.setViewName("employee/editTransactions");
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "transactions modify",
                "POST", "employee", "success", "modify transaction");
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = { "/employee/userscreditrequest" }, method = RequestMethod.GET)
    public ModelAndView creditDebit(HttpServletRequest request) {

        ModelAndView mv = new ModelAndView("employee/viewcreditrequest");
        mv.addObject("role", "employee");
        List<Transaction> transactions = transactionService.getCreditDebitRequest();
        mv.addObject("transactions", transactions);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s]", "usercreditrequest", "GET", "employee");
        LOGGER.info(logMessage);
        return mv;
    }
    
    @RequestMapping(value = "/employee/credittransactions", method = RequestMethod.POST)
    public String acceptCreditRequest(@AuthenticationPrincipal UserDetails activeUser, @ModelAttribute("row") Transaction txn, BindingResult result, WebRequest request,
            Errors errors, RedirectAttributes attributes) {

        Transaction transaction = transactionService.getTransactionsById(txn.getTransactionId());
        String str = transactionService.creditDebitTransaction(userService.getUserByEmail(activeUser.getUsername()), A_CREDIT, transaction);
        String status;
        if(str.equals(SUCCESS)){
            attributes.addFlashAttribute("message", new Message("success", str));
            status = "success";
        }else{
            attributes.addFlashAttribute("message", new Message("error", str));
            status = "error";
        }
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "credittransactions",
                "POST", "employee", status, str);
        LOGGER.info(logMessage);
        return "redirect:/employee/userscreditrequest";
    }
    
    @RequestMapping(value = "/employee/debittransactions", method = RequestMethod.POST)
    public String acceptDebitRequest(@AuthenticationPrincipal UserDetails activeUser,@ModelAttribute("row") Transaction txn, BindingResult result, WebRequest request,
            Errors errors, RedirectAttributes attributes) {

        Transaction transaction = transactionService.getTransactionsById(txn.getTransactionId());
        String str = transactionService.creditDebitTransaction(userService.getUserByEmail(activeUser.getUsername()), A_DEBIT, transaction);
        String status;
        if(str.equals(SUCCESS)){
            status = "success";
            attributes.addFlashAttribute("message", new Message("success", str));
        }else{
            status = "error";
            attributes.addFlashAttribute("message", new Message("error", str));
        }
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "debittransactions",
                "POST", "employee", status, str);
        LOGGER.info(logMessage);
        return "redirect:/employee/userscreditrequest";
    }

}
