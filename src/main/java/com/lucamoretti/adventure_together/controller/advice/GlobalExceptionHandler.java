package com.lucamoretti.adventure_together.controller.advice;

import com.lucamoretti.adventure_together.util.exception.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


/*
  Gestore globale delle eccezioni per l'applicazione.
  Intercetta eccezioni specifiche e restituisce risposte appropriate.
  intercetta le eccezioni eventualmente non gestite nei controller e fornisce risposte standardizzate.
*/

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Bad Request
    @ExceptionHandler({
            DataIntegrityException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ModelAndView handleBadRequest(Exception ex) {
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    // 404 - Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    // 409 - Conflict
    @ExceptionHandler(DuplicateResourceException.class)
    public ModelAndView handleConflict(DuplicateResourceException ex) {
        ModelAndView mav = new ModelAndView("error/409");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    // 403 - Forbidden
    @ExceptionHandler(UnauthorizedActionException.class)
    public ModelAndView handleForbidden(UnauthorizedActionException ex) {
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    // 500 - Server Error
    @ExceptionHandler({
            FileStorageException.class,
            Exception.class      // fallback generale
    })
    public ModelAndView handleServer(Exception ex) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}

