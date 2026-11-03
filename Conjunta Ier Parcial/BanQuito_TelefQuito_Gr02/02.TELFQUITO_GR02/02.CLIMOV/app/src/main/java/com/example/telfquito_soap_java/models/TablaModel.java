package com.example.telfquito_soap_java.models;

public class TablaModel {
    private double capitalPagado;
    private int codCredito;
    private int cuota;
    private double interesPagado;
    private double saldo;
    private double valorCuota;

    // Getters and setters
    public double getCapitalPagado() {
        return capitalPagado;
    }

    public void setCapitalPagado(double capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    public int getCodCredito() {
        return codCredito;
    }

    public void setCodCredito(int codCredito) {
        this.codCredito = codCredito;
    }

    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
    }

    public double getInteresPagado() {
        return interesPagado;
    }

    public void setInteresPagado(double interesPagado) {
        this.interesPagado = interesPagado;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(double valorCuota) {
        this.valorCuota = valorCuota;
    }
}

