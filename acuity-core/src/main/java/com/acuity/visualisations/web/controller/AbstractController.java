package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.exception.MethodFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Controller
public abstract class AbstractController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Logger getLogger() {
        return logger;
    }

    @Autowired
    private Environment environment;

    //RCT-1565
    protected static String getAbstractErrorMessage() {
        return "A server error occurred at " + new Date().toString() + ", please contact the ACUITY Support Team and pass the time specified.";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public String handleSessionExpired(Exception ex, HttpServletRequest request) {
        logger.error(ex.getMessage(), ex);

        return environment.acceptsProfiles("dev")
                ? ControllerUtils.getStackTraceString(ex) : ControllerUtils.escapeHtmlTags(getAbstractErrorMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error(ex.getMessage(), ex);
        if (ex.getCause() != null && ex.getCause().getClass().equals(AccessDeniedException.class)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return ex.getCause().getMessage();
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return environment.acceptsProfiles("dev")
                    ? ControllerUtils.getStackTraceString(ex) : ControllerUtils.escapeHtmlTags(getAbstractErrorMessage());
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(Exception ex, HttpServletRequest request) {
        logger.error(ex.getMessage(), ex);
        return environment.acceptsProfiles("dev")
                ? ControllerUtils.getStackTraceString(ex) : ControllerUtils.escapeHtmlTags(getAbstractErrorMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleJsonParseException(Exception ex, HttpServletRequest request) {
        logger.error(ex.getMessage(), ex);
        return environment.acceptsProfiles("dev")
                ? ControllerUtils.getStackTraceString(ex) : ControllerUtils.escapeHtmlTags(getAbstractErrorMessage());
    }

    @ExceptionHandler(MethodFailureException.class)
    @ResponseBody
    // as the METHOD_FAILURE tag is deprecated, error 418 used instead
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    public String handleJsonParseException(MethodFailureException ex, HttpServletRequest request) {
        logger.error(ex.getMessage(), ex);
        return ControllerUtils.escapeHtmlTags(getAbstractErrorMessage());
    }
}
