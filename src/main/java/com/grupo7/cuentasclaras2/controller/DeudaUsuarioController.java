package com.grupo7.cuentasclaras2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo7.cuentasclaras2.services.DeudaUsuarioService;

@RestController
@RequestMapping("/api/user-debt")
public class DeudaUsuarioController {
	@Autowired
	private DeudaUsuarioService deudaUsuarioService;
}
