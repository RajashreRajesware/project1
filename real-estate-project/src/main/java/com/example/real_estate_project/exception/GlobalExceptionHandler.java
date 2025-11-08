package com.example.real_estate_project.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * ✅ Global exception handler for the entire application.
 * This will catch exceptions thrown from any @Controller class.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles generic exceptions (fallback for all unexpected errors).
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex) {
        ModelAndView mav = new ModelAndView("error"); // view name (error.html)
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        mav.addObject("error", "Unexpected Error");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    /**
     * Handles IO exceptions (like image upload failures).
     */
    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(IOException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("error", "File Upload Error");
        mav.addObject("message", "There was a problem uploading your file. Please try again.");
        return mav;
    }

    /**
     * Handles data integrity violations (like foreign key constraint errors).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.CONFLICT.value());
        mav.addObject("error", "Database Error");
        mav.addObject("message", "Operation failed due to related data constraints.");
        return mav;
    }

    /**
     * Handles oversized file uploads.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        mav.addObject("error", "File Too Large");
        mav.addObject("message", "The uploaded file exceeds the maximum allowed size.");
        return mav;
    }

    /**
     * Handles runtime exceptions (common app-level issues).
     */
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("error", "Application Error");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
