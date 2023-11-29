package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.services.PagoService;

@RestController
@RequestMapping("/api/pay")
public class PagoController {
	@Autowired
	private PagoService pagoService;
}
