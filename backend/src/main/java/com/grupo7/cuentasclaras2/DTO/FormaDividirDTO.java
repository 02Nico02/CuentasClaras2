package com.grupo7.cuentasclaras2.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.grupo7.cuentasclaras2.modelos.FormaDividir;
import com.grupo7.cuentasclaras2.modelos.FormatosDivision;

public class FormaDividirDTO {
    private long id;
    private FormatosDivision formaDividir;
    private List<DivisionIndividualDTO> divisionIndividual = new ArrayList<>();

    public FormaDividirDTO() {
    }

    public FormaDividirDTO(FormaDividir formaDividir) {
        this.id = formaDividir.getId();
        this.formaDividir = formaDividir.getFormaDividir();
        this.divisionIndividual = formaDividir.getDivisionIndividual().stream()
                .map(DivisionIndividualDTO::new)
                .collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FormatosDivision getFormaDividir() {
        return formaDividir;
    }

    public void setFormaDividir(FormatosDivision formaDividir) {
        this.formaDividir = formaDividir;
    }

    public List<DivisionIndividualDTO> getDivisionIndividual() {
        return divisionIndividual;
    }

    public void setDivisionIndividual(List<DivisionIndividualDTO> divisionIndividual) {
        this.divisionIndividual = divisionIndividual;
    }

}
