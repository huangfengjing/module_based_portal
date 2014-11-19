package com.doleje.portlet.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple hello world controller.
 * Presents basic usage of SpringMVC and Velocity.
 * @author pmendelski
 *
 */
@Controller
public class PortletController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/portlet", method = RequestMethod.GET)
	public String portlet(ModelMap modelMap) {
		return "portlet";
	}
}
