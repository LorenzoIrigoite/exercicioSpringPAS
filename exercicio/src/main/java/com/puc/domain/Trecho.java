package com.puc.domain;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Trecho {
    private final Aerovia aerovia;
    private final int altitude;
    private final LocalDateTime entrada;
    private final OperacaoChegada operacaoChegada;
    private final int permanenciaMinutos;
    private final int vaga;
    private LocalDateTime chegada;

    public Trecho(Aerovia aerovia, int altitude, LocalDateTime entrada, OperacaoChegada operacaoChegada, int permanenciaMinutos) {
        this(aerovia, altitude, entrada, operacaoChegada, permanenciaMinutos, 1);
    }

    public Trecho(Aerovia aerovia, int altitude, LocalDateTime entrada, OperacaoChegada operacaoChegada, int permanenciaMinutos, int vaga) {
        if (aerovia == null || entrada == null || entrada.getSecond() != 0 || entrada.getNano() != 0 || operacaoChegada == null || permanenciaMinutos < 0 || vaga <= 0 || (operacaoChegada == OperacaoChegada.SOBREVOO && permanenciaMinutos != 0)) throw new IllegalArgumentException("dados inválidos do trecho");
        this.aerovia = aerovia; this.altitude = altitude; this.entrada = entrada; this.operacaoChegada = operacaoChegada; this.permanenciaMinutos = permanenciaMinutos; this.vaga = vaga;
    }
    public Aerovia aerovia() { return aerovia; }
    public int altitude() { return altitude; }
    public LocalDateTime entrada() { return entrada; }
    public OperacaoChegada operacaoChegada() { return operacaoChegada; }
    public int permanenciaMinutos() { return permanenciaMinutos; }
    public int vaga() { return vaga; }
    public LocalDateTime chegada() { return chegada; }
    void calcularChegada(Aeronave aeronave) {
        long minutos = BigDecimal.valueOf(aerovia.distanciaQuilometros()).divide(BigDecimal.valueOf(aeronave.velocidadeCruzeiro()), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60)).setScale(0, RoundingMode.HALF_UP).longValue();
        chegada = entrada.plusMinutes(minutos);
    }
    public LocalDateTime proximaEntrada() { return operacaoChegada == OperacaoChegada.POUSO ? chegada.plusMinutes(permanenciaMinutos) : chegada; }
}
