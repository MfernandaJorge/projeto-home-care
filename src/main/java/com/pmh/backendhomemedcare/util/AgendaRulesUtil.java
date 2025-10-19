package com.pmh.backendhomemedcare.util;

import java.time.LocalTime;

public class AgendaRulesUtil {

    /**
     * Calcula o tempo total estimado (ida + atendimento + volta),
     * aplicando fatores de trânsito conforme o horário.
     *
     * @param duracaoAtendimentoMin duração do atendimento (minutos)
     * @param tempoDeslocamentoMin  tempo de deslocamento simples (ida)
     * @param horaInicio            hora de saída da clínica (aprox.)
     * @return tempo total em minutos
     */
    public static double calcularTempoTotal(double duracaoAtendimentoMin,
                                            double tempoDeslocamentoMin,
                                            LocalTime horaInicio) {
        if (tempoDeslocamentoMin <= 0) {
            // caso não haja deslocamento calculado (ex: endereço sem rota)
            return duracaoAtendimentoMin;
        }

        // aplicar fator de trânsito
        double fatorTransito = obterFatorTransito(horaInicio);

        double tempoIda = tempoDeslocamentoMin * fatorTransito;
        double tempoVolta = tempoDeslocamentoMin * fatorTransito;

        return duracaoAtendimentoMin + tempoIda + tempoVolta;
    }

    /**
     * Define o fator de trânsito (multiplicador) conforme o horário do dia.
     * Exemplo: 1.3 = 30% mais tempo.
     */
    private static double obterFatorTransito(LocalTime hora) {
        int h = hora.getHour();

        // Manhã (7h–9h): trânsito alto
        if (h >= 7 && h < 9) return 1.3;
        // Almoço (11h30–13h30): trânsito médio
        if (h >= 11 && h < 14) return 1.15;
        // Fim de tarde (17h–19h): trânsito alto
        if (h >= 17 && h < 19) return 1.3;

        // Fora de pico
        return 1.0;
    }

    private AgendaRulesUtil() {} // utilitária, não instanciável
}
