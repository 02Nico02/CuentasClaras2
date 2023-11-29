package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.services.FormaDividirService;

@RestController
@RequestMapping("/api/split-form")
public class FormaDividirController {
	@Autowired
	private FormaDividirService formaDividirService;
}
