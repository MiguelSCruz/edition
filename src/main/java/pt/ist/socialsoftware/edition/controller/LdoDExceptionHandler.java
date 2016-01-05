package pt.ist.socialsoftware.edition.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import pt.ist.socialsoftware.edition.domain.LdoD;
import pt.ist.socialsoftware.edition.shared.exception.LdoDCreateVirtualEditionException;
import pt.ist.socialsoftware.edition.shared.exception.LdoDEditVirtualEditionException;
import pt.ist.socialsoftware.edition.shared.exception.LdoDException;
import pt.ist.socialsoftware.edition.shared.exception.LdoDLoadException;

@EnableWebMvc
@ControllerAdvice
public class LdoDExceptionHandler {
	private final static Logger logger = LoggerFactory.getLogger(LdoDExceptionHandler.class);

	@ExceptionHandler({ LdoDLoadException.class })
	public ModelAndView handleLdoDLoadException(LdoDLoadException ex) {

		logger.debug("LdoDLoadException: {}", ex.getMessage());

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("utils/ldoDExceptionPage");
		modelAndView.addObject("message", ex.getMessage());
		return modelAndView;
	}

	@ExceptionHandler({ LdoDEditVirtualEditionException.class })
	public ModelAndView handleLdoDEditVirtualEditionException(LdoDEditVirtualEditionException ex) {

		logger.debug("LdoDEditVirtualEditionException: {}", ex.getErrors());

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("errors", ex.getErrors());
		modelAndView.addObject("externalId", ex.getVirtualEdition().getExternalId());
		modelAndView.addObject("virtualEdition", ex.getVirtualEdition());
		modelAndView.addObject("acronym", ex.getAcronym());
		modelAndView.addObject("title", ex.getTitle());
		modelAndView.addObject("date", ex.getVirtualEdition().getDate());
		modelAndView.addObject("pub", ex.isPub());
		modelAndView.setViewName("virtual/edition");
		return modelAndView;
	}

	@ExceptionHandler({ LdoDCreateVirtualEditionException.class })
	public ModelAndView handleLdoDCreateVirtualEditionException(LdoDCreateVirtualEditionException ex) {

		logger.debug("LdoDCreateVirtualEditionException: {}", ex.getErrors());

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("errors", ex.getErrors());
		modelAndView.addObject("acronym", ex.getAcronym());
		modelAndView.addObject("title", ex.getTitle());
		modelAndView.addObject("pub", ex.isPub());
		modelAndView.addObject("virtualEditions", ex.getVirtualEditions());
		modelAndView.addObject("expertEditions", LdoD.getInstance().getSortedExpertEdition());
		modelAndView.addObject("user", ex.getUser());
		modelAndView.setViewName("virtual/editions");
		return modelAndView;
	}

	@ExceptionHandler({ AccessDeniedException.class })
	public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {

		logger.debug("AccessDeniedException: {}", ex.getMessage());

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("utils/ldoDExceptionPage");
		modelAndView.addObject("i18n", true);
		modelAndView.addObject("message", "general.access.denied");
		return modelAndView;
	}

	@ExceptionHandler({ LdoDException.class })
	public ModelAndView handleLdoException(LdoDException ex) {

		logger.debug("LdoDException: {}", ex.getStackTrace().toString());

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("utils/ldoDExceptionPage");
		modelAndView.addObject("message", ex.getMessage());
		return modelAndView;
	}

	@ExceptionHandler({ Exception.class })
	public ModelAndView handleException(Exception ex) {

		logger.error("Exception: {}", ex.getMessage(), ex);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("utils/ldoDExceptionPage");
		modelAndView.addObject("message", ex.getMessage());
		return modelAndView;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ModelAndView handleAnyException(HttpServletRequest request, Exception ex) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("utils/ldoDExceptionPage");
		modelAndView.addObject("message", ex.getMessage());
		return modelAndView;
	}

}
