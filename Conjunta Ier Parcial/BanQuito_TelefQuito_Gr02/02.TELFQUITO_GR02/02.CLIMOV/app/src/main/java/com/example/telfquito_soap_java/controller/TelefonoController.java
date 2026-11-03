package com.example.telfquito_soap_java.controller;

import com.example.telfquito_soap_java.service.TelefonoService;
import com.example.telfquito_soap_java.models.TelefonoModel;
import android.util.Log;

import java.util.List;

public class TelefonoController {
    private static final String TAG = "TelefonoController";
    private TelefonoService telefonoService;

    public TelefonoController() {
        this.telefonoService = new TelefonoService();
    }

    public void getAllTelefonos(TelefonoService.SoapCallback<List<TelefonoModel>> callback) {
        Log.d(TAG, "Obteniendo telefonos...");
        telefonoService.getAllTelefonos(new TelefonoService.SoapCallback<List<TelefonoModel>>() {
            @Override
            public void onSuccess(List<TelefonoModel> result) {
                Log.d(TAG, "Telefonos obtenidos con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al obtener los telefonos: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public void getTelefonoById(int id, TelefonoService.SoapCallback<TelefonoModel> callback) {
        telefonoService.getTelefonoById(id, new TelefonoService.SoapCallback<TelefonoModel>() {
            @Override
            public void onSuccess(TelefonoModel result) {
                Log.d(TAG, "Telefono obtenido con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al obtener el telefono: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public void insertTelefono(TelefonoModel telefono, TelefonoService.SoapCallback<String> callback) {
        telefonoService.insertTelefono(telefono, new TelefonoService.SoapCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Telefono insertado con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al insertar el telefono: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public void updateTelefono(TelefonoModel telefono, TelefonoService.SoapCallback<String> callback) {
        telefonoService.updateTelefono(telefono, new TelefonoService.SoapCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Telefono actualizado con éxito: " + result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al actualizar el telefono: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }
}