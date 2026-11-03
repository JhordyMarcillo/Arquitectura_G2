package com.example.telfquito_soap_java.controller;

import com.example.telfquito_soap_java.service.CompraService;
import com.example.telfquito_soap_java.models.TablaModel;
import com.example.telfquito_soap_java.models.FacturaModel;
import android.util.Log;

import java.util.List;

public class CompraController {
    private static final String TAG = "CompraController";
    private CompraService compraService;

    public CompraController() {
        this.compraService = new CompraService();
    }

    public void comprarEfectivo(int codTelefono, String codcCedula, CompraService.SoapCallback<String> callback) {
        compraService.comprarEfectivo(codTelefono, codcCedula, new CompraService.SoapCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Compra en efectivo realizada con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al realizar la compra en efectivo: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public void comprarCredito(int codTelefono, String cedula, int plazoMeses, CompraService.SoapCallback<String> callback) {
        compraService.comprarCredito(codTelefono, cedula, plazoMeses, new CompraService.SoapCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Compra a crédito realizada con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al realizar la compra a crédito: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public void consultarTablaAmortizacion(String cedula, CompraService.SoapCallback<List<TablaModel>> callback) {
        compraService.consultarTablaAmortizacion(cedula, new CompraService.SoapCallback<List<TablaModel>>() {
            @Override
            public void onSuccess(List<TablaModel> result) {
                Log.d(TAG, "Tabla de amortización consultada con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al consultar la tabla de amortización: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public void obtenerFactura(String cedula, CompraService.SoapCallback<List<FacturaModel>> callback) {
        compraService.obtenerFactura(cedula, new CompraService.SoapCallback<List<FacturaModel>>() {
            @Override
            public void onSuccess(List<FacturaModel> result) {
                Log.d(TAG, "Factura obtenida con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al obtener la factura: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }
}