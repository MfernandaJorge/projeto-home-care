package com.pmh.backendhomemedcare.model.enums;

import java.time.LocalTime;

public enum FatorTransito {

    NORMAL(1.0),
    PICO_ALMOCO(1.2),
    PICO_FIM_EXPEDIENTE(1.3);

    private final double fator;

    FatorTransito(double fator) {
        this.fator = fator;
    }

    public double getFator() {
        return fator;
    }

    /**
     * Retorna o fator de trânsito aplicável conforme o horário informado.
     */
    public static FatorTransito fromHorario(LocalTime hora) {
        if (hora.isAfter(LocalTime.of(11, 30)) && hora.isBefore(LocalTime.of(13, 30))) {
            return PICO_ALMOCO;
        }
        if (hora.isAfter(LocalTime.of(17, 0)) && hora.isBefore(LocalTime.of(19, 0))) {
            return PICO_FIM_EXPEDIENTE;
        }
        return NORMAL;
    }
}
