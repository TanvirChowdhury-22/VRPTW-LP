package com.example.deliverymapping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

	@GetMapping("/")
	public String homePage() throws Exception {
		return "homepage";
	}

	@GetMapping("/viewresult")
	public String viewResult() throws Exception {
		return "viewresult";
	}

}
