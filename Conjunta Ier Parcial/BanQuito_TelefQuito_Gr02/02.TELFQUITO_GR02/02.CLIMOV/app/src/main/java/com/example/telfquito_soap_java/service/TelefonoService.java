package com.example.telfquito_soap_java.service;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.telfquito_soap_java.models.TelefonoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TelefonoService {
    private static final String TAG = "TelefonoService";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";
    private static final String URL = "http://192.168.186.83:8080/TELFQUITO_SOAP_JAVA/WSTelefonos";
    private static final int TIMEOUT = 15000;

    public interface SoapCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    @SuppressLint("StaticFieldLeak")
    public void getAllTelefonos(final SoapCallback<List<TelefonoModel>> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<List<TelefonoModel>>>() {
            @Override
            protected AsyncTaskResult<List<TelefonoModel>> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "getAllTelefonos");
                    Log.d(TAG, "Request object created: " + request.toString());

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    Log.d(TAG, "Envelope configured");

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("http://ws.monster.edu.ec/WSTelefonos/getAllTelefonosRequest", envelope);

                        Log.d(TAG, "Request: " + transport.requestDump);
                        Log.d(TAG, "Response: " + transport.responseDump);

                        Object response = envelope.getResponse();
                        if (response instanceof Vector) {
                            Log.d(TAG, "Response received as Vector");
                            Vector<?> vectorResponse = (Vector<?>) response;
                            List<TelefonoModel> telefonos = new ArrayList<>();
                            for (Object obj : vectorResponse) {
                                if (obj instanceof SoapObject) {
                                    SoapObject telefonoObject = (SoapObject) obj;
                                    TelefonoModel telefono = new TelefonoModel();
                                    telefono.setCodTelefono(Integer.parseInt(telefonoObject.getProperty("codTelefono").toString()));
                                    telefono.setDisponible(Integer.parseInt(telefonoObject.getProperty("disponible").toString()));
                                    telefono.setMarca(telefonoObject.getProperty("marca").toString());
                                    telefono.setNombre(telefonoObject.getProperty("nombre").toString());
                                    telefono.setPrecio(telefonoObject.getProperty("precio").toString());
                                    telefonos.add(telefono);
                                }
                            }
                            return new AsyncTaskResult<>(telefonos);
                        } else if (response instanceof SoapObject) {
                            Log.d(TAG, "Response received as SoapObject");
                            SoapObject soapResponse = (SoapObject) response;
                            List<TelefonoModel> telefonos = new ArrayList<>();
                            for (int i = 0; i < soapResponse.getPropertyCount(); i++) {
                                SoapObject telefonoObject = (SoapObject) soapResponse.getProperty(i);
                                TelefonoModel telefono = new TelefonoModel();
                                telefono.setCodTelefono(Integer.parseInt(telefonoObject.getProperty("codTelefono").toString()));
                                telefono.setDisponible(Integer.parseInt(telefonoObject.getProperty("disponible").toString()));
                                telefono.setMarca(telefonoObject.getProperty("marca").toString());
                                telefono.setNombre(telefonoObject.getProperty("nombre").toString());
                                telefono.setPrecio(telefonoObject.getProperty("precio").toString());
                                telefonos.add(telefono);
                            }
                            return new AsyncTaskResult<>(telefonos);
                        } else if (response instanceof SoapPrimitive) {
                            Log.d(TAG, "Response received as SoapPrimitive: " + response.toString());
                            return new AsyncTaskResult<>(new Exception("Unexpected response type: SoapPrimitive"));
                        } else {
                            Log.d(TAG, "Unexpected response type: " + response.getClass().getName());
                            return new AsyncTaskResult<>(new Exception("Unexpected response type: " + response.getClass().getName()));
                        }
                    } catch (Exception e) {
                        return new AsyncTaskResult<>(e);
                    }
                } catch (Exception e) {
                    return new AsyncTaskResult<>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<TelefonoModel>> result) {
                if (result.getError() != null) {
                    callback.onError("Error: " + result.getError().getMessage());
                } else {
                    callback.onSuccess(result.getResult());
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getTelefonoById(final int id, final SoapCallback<TelefonoModel> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<TelefonoModel>>() {
            @Override
            protected AsyncTaskResult<TelefonoModel> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "getTelefonoById");

                    PropertyInfo idProperty = new PropertyInfo();
                    idProperty.setName("id");
                    idProperty.setValue(id);
                    idProperty.setType(Integer.class);
                    request.addProperty(idProperty);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("http://ws.monster.edu.ec/WSTelefonos/getTelefonoByIdRequest", envelope);

                        if (envelope.getResponse() instanceof SoapObject) {
                            SoapObject response = (SoapObject) envelope.getResponse();
                            TelefonoModel telefono = new TelefonoModel();
                            telefono.setCodTelefono(Integer.parseInt(response.getProperty("codTelefono").toString()));
                            telefono.setDisponible(Integer.parseInt(response.getProperty("disponible").toString()));
                            telefono.setMarca(response.getProperty("marca").toString());
                            telefono.setNombre(response.getProperty("nombre").toString());
                            telefono.setPrecio(response.getProperty("precio").toString());
                            return new AsyncTaskResult<>(telefono);
                        } else {
                            return new AsyncTaskResult<>(new Exception("Empty response received"));
                        }
                    } catch (Exception e) {
                        return new AsyncTaskResult<>(e);
                    }
                } catch (Exception e) {
                    return new AsyncTaskResult<>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TelefonoModel> result) {
                if (result.getError() != null) {
                    callback.onError("Error: " + result.getError().getMessage());
                } else {
                    callback.onSuccess(result.getResult());
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertTelefono(final TelefonoModel telefono, final SoapCallback<String> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<String>>() {
            @Override
            protected AsyncTaskResult<String> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "insertTelefono");

                    PropertyInfo telefonoProperty = new PropertyInfo();
                    telefonoProperty.setName("telefono");
                    telefonoProperty.setValue(telefono);
                    telefonoProperty.setType(TelefonoModel.class);
                    request.addProperty(telefonoProperty);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("http://ws.monster.edu.ec/WSTelefonos/insertTelefonoRequest", envelope);

                        if (envelope.getResponse() instanceof SoapPrimitive) {
                            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                            return new AsyncTaskResult<>(response.toString());
                        } else {
                            return new AsyncTaskResult<>(new Exception("Empty response received"));
                        }
                    } catch (Exception e) {
                        return new AsyncTaskResult<>(e);
                    }
                } catch (Exception e) {
                    return new AsyncTaskResult<>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<String> result) {
                if (result.getError() != null) {
                    callback.onError("Error: " + result.getError().getMessage());
                } else {
                    callback.onSuccess(result.getResult());
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void updateTelefono(final TelefonoModel telefono, final SoapCallback<String> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<String>>() {
            @Override
            protected AsyncTaskResult<String> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "updateTelefono");

                    PropertyInfo telefonoProperty = new PropertyInfo();
                    telefonoProperty.setName("telefono");
                    telefonoProperty.setValue(telefono);
                    telefonoProperty.setType(TelefonoModel.class);
                    request.addProperty(telefonoProperty);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("http://ws.monster.edu.ec/WSTelefonos/updateTelefonoRequest", envelope);

                        if (envelope.getResponse() instanceof SoapPrimitive) {
                            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                            return new AsyncTaskResult<>(response.toString());
                        } else {
                            return new AsyncTaskResult<>(new Exception("Empty response received"));
                        }
                    } catch (Exception e) {
                        return new AsyncTaskResult<>(e);
                    }
                } catch (Exception e) {
                    return new AsyncTaskResult<>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<String> result) {
                if (result.getError() != null) {
                    callback.onError("Error: " + result.getError().getMessage());
                } else {
                    callback.onSuccess(result.getResult());
                }
            }
        }.execute();
    }

    private static class AsyncTaskResult<T> {
        private T result;
        private Exception error;

        AsyncTaskResult(T result) {
            this.result = result;
        }

        AsyncTaskResult(Exception error) {
            this.error = error;
        }

        T getResult() {
            return result;
        }

        Exception getError() {
            return error;
        }
    }
}