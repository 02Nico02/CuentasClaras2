package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.grupo7.cuentasclaras2.services.GastoRecurrenteService;

@RestController
@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("/api/recurring-expense")
public class GastoRecurrenteController {
	@Autowired
	private GastoRecurrenteService gastoRecurrenteService;
}
