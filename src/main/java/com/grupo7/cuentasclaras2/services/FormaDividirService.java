package com.grupo7.cuentasclaras2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo7.cuentasclaras2.repositories.FormaDividirRepository;

@Service
public class FormaDividirService {
	@Autowired
	private FormaDividirRepository formaDividirRepository;
}
