package com.bankapp.controllers;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.forms.CreditDebitForm;
import com.bankapp.forms.InitiateTransactionForm;
import com.bankapp.forms.TransferFundsForm;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IAccountService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

@Controller
@Secured({ "ROLE_CUSTOMER", "ROLE_MERCHANT" })
public class CommonController implements Constants {

    private final Logger LOGGER = Logger.getLogger(CustomerController.class);

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IUserService userService;

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        CustomDateEditor editor = new CustomDateEditor(sdf, true);
        binder.registerCustomEditor(Date.class, editor);
    }

    @RequestMapping(value = { "/customer/myaccount", "/merchant/myaccount" }, method = RequestMethod.GET)
    public ModelAndView getTransactions(Principal principal, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        User loggedInUser = userService.getUserFromSession(principal);
        Account account = accountService.getAccountByUser(loggedInUser);
        List<Transaction> transactions = transactionService.getTransactionsByAccount(account, account);
        mv.addObject("accounts", account);
        mv.addObject("transactions", transactions);
        mv.addObject("role", role);
        mv.setViewName("common/myaccount");

        String message = String.format("[Action=%s, Method=%s, Role=%s][User=%s, Account=%s, NumberOfTransactions=%s]",
                "myaccount", "GET", role, loggedInUser.getEmail(), account.getAccId(), transactions.size());
        LOGGER.info(message);

        return mv;
    }

    @RequestMapping(value = { "/customer/transferfunds", "/merchant/transferfunds" }, method = RequestMethod.GET)
    public ModelAndView transferFunds(HttpServletRequest request) {

        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        ModelAndView mv = new ModelAndView("common/transferfunds");
        mv.addObject("form", new TransferFundsForm());
        mv.addObject("role", role);

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s]", "transferfunds", "GET", role);
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = { "/customer/transferfunds", "/merchant/transferfunds" }, method = RequestMethod.POST)
    public String saveTransaction(@AuthenticationPrincipal UserDetails activeUser, final ModelMap model,
            @ModelAttribute("form") @Valid TransferFundsForm form, BindingResult result, HttpServletRequest request,
            RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "common/transferfunds";
        }

        String fromEmail = activeUser.getUsername();
        String toEmail = form.getEmail();
        Transaction transaction = new Transaction();
        transaction.setEncryptedAmount(form.getAmount());
        transaction.setComment(form.getComment());

        String serviceStatus = transactionService.saveTransaction(fromEmail, toEmail, transaction);

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
            redirectUrl = "redirect:/" + role + "/transferfunds";
            break;
        case CRITICAL:
            status = "success";
            message = serviceStatus;
            redirectUrl = "redirect:/" + role + "/myaccount";
            break;
        case SUCCESS:
            status = "success";
            message = "Money transfered successfully";
            redirectUrl = "redirect:/" + role + "/myaccount";
            break;
        default:
            status = "error";
            message = ERR_UNHANDLED;
            redirectUrl = "redirect:/" + role + "/transferfunds";
            break;
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", role);

        logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "transferfunds", "POST",
                role, serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

    @RequestMapping(value = { "/customer/initiatetransaction",
            "/merchant/initiatetransaction" }, method = RequestMethod.GET)
    public ModelAndView initiateTransaction(HttpServletRequest request) {
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        ModelAndView mv = new ModelAndView("common/initiatetransaction");
        mv.addObject("form", new InitiateTransactionForm());
        mv.addObject("role", role);

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s]", "initiatetransaction", "GET", role);
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = { "/customer/initiatetransaction",
            "/merchant/initiatetransaction" }, method = RequestMethod.POST)
    public String initiateTransaction(@AuthenticationPrincipal UserDetails activeUser, final ModelMap model,
            @ModelAttribute("form") @Valid InitiateTransactionForm form, BindingResult result,
            HttpServletRequest request, RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "common/initiatetransaction";
        }

        String fromEmail = activeUser.getUsername();
        String toEmail = form.getEmail();
        Transaction transaction = new Transaction();
        transaction.setEncryptedAmount(form.getAmount());
        transaction.setComment(form.getComment());
        transaction.setTransferDate(form.getTransferDate());
        String serviceStatus = transactionService.initiateTransaction(fromEmail, toEmail, transaction);

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
            redirectUrl = "redirect:/" + role + "/initiatetransaction";
            break;
        case CRITICAL:
            status = "success";
            message = serviceStatus;
            redirectUrl = "redirect:/" + role + "/myaccount";
            break;
        case SUCCESS:
            status = "success";
            message = "Your transaction is initiated and will be handled by our employees";
            redirectUrl = "redirect:/" + role + "/myaccount";
            break;
        default:
            status = "error";
            message = ERR_UNHANDLED;
            redirectUrl = "redirect:/" + role + "/initiatetransaction";
            break;
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", role);

        logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "initiatetransaction",
                "POST", role, serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

    @RequestMapping(value = { "/customer/creditdebit", "/merchant/creditdebit" }, method = RequestMethod.GET)
    public ModelAndView creditDebit(HttpServletRequest request) {
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        ModelAndView mv = new ModelAndView("common/creditdebit");
        mv.addObject("form", new CreditDebitForm());
        mv.addObject("role", role);

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s]", "creditdebit", "GET", role);
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = { "/customer/creditdebit", "/merchant/creditdebit" }, method = RequestMethod.POST)
    public String creditDebit(@AuthenticationPrincipal UserDetails activeUser, final ModelMap model,
            @ModelAttribute("form") @Valid CreditDebitForm form, BindingResult result, HttpServletRequest request,
            RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "common/creditdebit";
        }

        String Email = activeUser.getUsername();
        Transaction transaction = new Transaction();
        transaction.setEncryptedAmount(form.getAmount());
        transaction.setStatus(form.getStatus());

        String serviceStatus = transactionService.creditDebit(Email, transaction);

        switch (serviceStatus) {

        case ERR_TRANS_DECODE:
        case ERR_TRANS_DECRYPTION:
        case ERR_TRANS_INCORRECT_FORMAT:
        case ERR_TRANS_EXPIRED:
        case ERR_TRANS_LIMIT:
        case ERR_LESS_BALANCE:
        case ERR_ACCOUNT_NOT_EXISTS:
        case ERR_UNHANDLED:
            status = "error";
            message = serviceStatus;
            redirectUrl = "redirect:/" + role + "/creditdebit";
            break;
        case SUCCESS:
            status = "success";
            message = "Your transaction is initiated and will be handled by our employees";
            redirectUrl = "redirect:/" + role + "/myaccount";
            break;
        default:
            status = "error";
            message = ERR_UNHANDLED;
            redirectUrl = "redirect:/" + role + "/creditdebit";
            break;
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", role);

        logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "creditdebit", "POST",
                role, serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

    @RequestMapping(value = "/getstatement", method = RequestMethod.GET)
    public ModelAndView getStatement(Principal principal, HttpServletRequest request) {
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }
        User loggedInUser = userService.getUserFromSession(principal);
        Account account = accountService.getAccountByUser(loggedInUser);
        List<Transaction> transactions = transactionService.getTransactionsByAccount(account, account);
        Map<String, Transaction> statementData = new HashMap<String, Transaction>();
        Integer count = 0;
        for (Transaction tr : transactions) {
            statementData.put((count++).toString(), tr);
        }
        ModelAndView mv = new ModelAndView(new AbstractPdfView() {

            @Override
            protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                    HttpServletRequest request, HttpServletResponse response) throws Exception {

                document.add(new Paragraph("My ASU Bank - Your Bank Statement"));
                document.add(new Paragraph("Account: " + account.getAccId()));
                document.add(new Paragraph("User: " + loggedInUser.getEmail()));
                document.add(new Paragraph("Generated: " + new Date()));

                Map<String, Transaction> statementData = (Map<String, Transaction>) model.get("statementData");

                Table table = new Table(4);
                table.addCell("Tranfer Date");
                table.addCell("From Account");
                table.addCell("To Account");
                table.addCell("Amount");

                for (Map.Entry<String, Transaction> entry : statementData.entrySet()) {
                    table.addCell(entry.getValue().getTransferDate().toString());
                    table.addCell(entry.getValue().getFromAccount().getUser().getEmail());
                    table.addCell(entry.getValue().getToAccount().getUser().getEmail());
                    table.addCell(entry.getValue().getAmount().toString());
                }

                document.add(table);

            }

        }, "statementData", statementData);
        mv.addObject("role", role);
        return mv;

    }

}
