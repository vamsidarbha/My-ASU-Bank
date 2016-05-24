package com.bankapp.exceptions;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Message;

@ControllerAdvice
class GlobalExceptionHandlerConfig {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(HttpSessionRequiredException.class)
    public String handleSessionExpired(HttpServletRequest req, Exception exception, RedirectAttributes attributes) {
        attributes.addFlashAttribute("error", new Message("error", "Your session has expired. Please login again"));
        return "redirect:/";
    }

    /**
     * Shows a friendly message instead of the exception stack trace.
     * 
     * @param pe
     *            exception.
     * @return the exception message.
     */
    @ExceptionHandler(PersistenceException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlePersistenceException(final PersistenceException pe) {
        String returnMessage;
        if (pe.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) pe.getCause();
            ConstraintViolation<?> cv = cve.getConstraintViolations().iterator().next();
            returnMessage = cv.getMessage();
        } else {
            returnMessage = pe.getLocalizedMessage();
        }
        return returnMessage;
    }
}