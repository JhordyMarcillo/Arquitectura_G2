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

import com.example.telfquito_soap_java.models.FacturaModel;
import com.example.telfquito_soap_java.models.TablaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CompraService {
    private static final String TAG = "CompraService";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";
    private static final String URL = "http://192.168.186.83:8080/TELFQUITO_SOAP_JAVA/WSCompra";
    private static final int TIMEOUT = 15000;

    public interface SoapCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    @SuppressLint("StaticFieldLeak")
    public void comprarEfectivo(final int codTelefono, final String codcCedula, final SoapCallback<String> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<String>>() {
            @Override
            protected AsyncTaskResult<String> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "comprarEfectivo");

                    PropertyInfo codTelefonoProperty = new PropertyInfo();
                    codTelefonoProperty.setName("codTelefono");
                    codTelefonoProperty.setValue(codTelefono);
                    codTelefonoProperty.setType(Integer.class);
                    request.addProperty(codTelefonoProperty);

                    PropertyInfo codcCedulaProperty = new PropertyInfo();
                    codcCedulaProperty.setName("codcCedula");
                    codcCedulaProperty.setValue(codcCedula);
                    codcCedulaProperty.setType(String.class);
                    request.addProperty(codcCedulaProperty);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("", envelope);

                        if (envelope.getResponse() instanceof SoapPrimitive) {
                            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                            return new AsyncTaskResult<>(response.toString());
                        } else if (envelope.getResponse() != null) {
                            return new AsyncTaskResult<>(envelope.getResponse().toString());
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
    public void comprarCredito(final int codTelefono, final String cedula, final int plazoMeses, final SoapCallback<String> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<String>>() {
            @Override
            protected AsyncTaskResult<String> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "comprarCredito");

                    PropertyInfo codTelefonoProperty = new PropertyInfo();
                    codTelefonoProperty.setName("codTelefono");
                    codTelefonoProperty.setValue(codTelefono);
                    codTelefonoProperty.setType(Integer.class);
                    request.addProperty(codTelefonoProperty);

                    PropertyInfo cedulaProperty = new PropertyInfo();
                    cedulaProperty.setName("cedula");
                    cedulaProperty.setValue(cedula);
                    cedulaProperty.setType(String.class);
                    request.addProperty(cedulaProperty);

                    PropertyInfo plazoMesesProperty = new PropertyInfo();
                    plazoMesesProperty.setName("plazoMeses");
                    plazoMesesProperty.setValue(plazoMeses);
                    plazoMesesProperty.setType(Integer.class);
                    request.addProperty(plazoMesesProperty);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("", envelope);

                        if (envelope.getResponse() instanceof SoapPrimitive) {
                            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                            return new AsyncTaskResult<>(response.toString());
                        } else if (envelope.getResponse() != null) {
                            return new AsyncTaskResult<>(envelope.getResponse().toString());
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
    public void consultarTablaAmortizacion(final String cedula, final SoapCallback<List<TablaModel>> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<List<TablaModel>>>() {
            @Override
            protected AsyncTaskResult<List<TablaModel>> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "consultarTablaAmortizacion");

                    PropertyInfo cedulaProperty = new PropertyInfo();
                    cedulaProperty.setName("cedula");
                    cedulaProperty.setValue(cedula);
                    cedulaProperty.setType(String.class);
                    request.addProperty(cedulaProperty);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("", envelope);

                        Object response = envelope.getResponse();
                        List<TablaModel> tablas = new ArrayList<>();
                        if (response instanceof Vector) {
                            Vector<?> vectorResponse = (Vector<?>) response;
                            for (Object obj : vectorResponse) {
                                if (obj instanceof SoapObject) {
                                    SoapObject tablaObject = (SoapObject) obj;
                                    TablaModel tabla = new TablaModel();
                                    tabla.setCapitalPagado(Double.parseDouble(tablaObject.getProperty("capitalPagado").toString()));
                                    tabla.setCodCredito(Integer.parseInt(tablaObject.getProperty("codCredito").toString()));
                                    tabla.setCuota(Integer.parseInt(tablaObject.getProperty("cuota").toString()));
                                    tabla.setInteresPagado(Double.parseDouble(tablaObject.getProperty("interesPagado").toString()));
                                    tabla.setSaldo(Double.parseDouble(tablaObject.getProperty("saldo").toString()));
                                    tabla.setValorCuota(Double.parseDouble(tablaObject.getProperty("valorCuota").toString()));
                                    tablas.add(tabla);
                                }
                            }
                        } else if (response instanceof SoapObject) {
                            SoapObject soapResponse = (SoapObject) response;
                            for (int i = 0; i < soapResponse.getPropertyCount(); i++) {
                                SoapObject tablaObject = (SoapObject) soapResponse.getProperty(i);
                                TablaModel tabla = new TablaModel();
                                tabla.setCapitalPagado(Double.parseDouble(tablaObject.getProperty("capitalPagado").toString()));
                                tabla.setCodCredito(Integer.parseInt(tablaObject.getProperty("codCredito").toString()));
                                tabla.setCuota(Integer.parseInt(tablaObject.getProperty("cuota").toString()));
                                tabla.setInteresPagado(Double.parseDouble(tablaObject.getProperty("interesPagado").toString()));
                                tabla.setSaldo(Double.parseDouble(tablaObject.getProperty("saldo").toString()));
                                tabla.setValorCuota(Double.parseDouble(tablaObject.getProperty("valorCuota").toString()));
                                tablas.add(tabla);
                            }
                        } else {
                            return new AsyncTaskResult<>(new Exception("Unexpected response type: " + response.getClass().getName()));
                        }
                        return new AsyncTaskResult<>(tablas);
                    } catch (Exception e) {
                        return new AsyncTaskResult<>(e);
                    }
                } catch (Exception e) {
                    return new AsyncTaskResult<>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<TablaModel>> result) {
                if (result.getError() != null) {
                    callback.onError("Error: " + result.getError().getMessage());
                } else {
                    callback.onSuccess(result.getResult());
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void obtenerFactura(final String cedula, final SoapCallback<List<FacturaModel>> callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<List<FacturaModel>>>() {
            @Override
            protected AsyncTaskResult<List<FacturaModel>> doInBackground(Void... voids) {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "obtenerFactura");

                    PropertyInfo cedulaProperty = new PropertyInfo();
                    cedulaProperty.setName("cedula");
                    cedulaProperty.setValue(cedula);
                    cedulaProperty.setType(String.class);
                    request.addProperty(cedulaProperty);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        transport.call("", envelope);

                        Object response = envelope.getResponse();
                        List<FacturaModel> facturas = new ArrayList<>();
                        if (response instanceof Vector) {
                            Vector<?> vectorResponse = (Vector<?>) response;
                            for (Object obj : vectorResponse) {
                                if (obj instanceof SoapObject) {
                                    SoapObject facturaObject = (SoapObject) obj;
                                    FacturaModel factura = new FacturaModel();
                                    factura.setCodCompra(Integer.parseInt(facturaObject.getProperty("codCompra").toString()));
                                    factura.setDescuento(Double.parseDouble(facturaObject.getProperty("descuento").toString()));
                                    factura.setFecha(facturaObject.getProperty("fecha").toString());
                                    factura.setFormaPago(facturaObject.getProperty("formaPago").toString());
                                    factura.setMarcaTelefono(facturaObject.getProperty("marcaTelefono").toString());
                                    factura.setNombreCliente(facturaObject.getProperty("nombreCliente").toString());
                                    factura.setNombreTelefono(facturaObject.getProperty("nombreTelefono").toString());
                                    factura.setPreciofinal(Double.parseDouble(facturaObject.getProperty("preciofinal").toString()));
                                    facturas.add(factura);
                                }
                            }
                        } else if (response instanceof SoapObject) {
                            SoapObject soapResponse = (SoapObject) response;
                            for (int i = 0; i < soapResponse.getPropertyCount(); i++) {
                                SoapObject facturaObject = (SoapObject) soapResponse.getProperty(i);
                                FacturaModel factura = new FacturaModel();
                                factura.setCodCompra(Integer.parseInt(facturaObject.getProperty("codCompra").toString()));
                                factura.setDescuento(Double.parseDouble(facturaObject.getProperty("descuento").toString()));
                                factura.setFecha(facturaObject.getProperty("fecha").toString());
                                factura.setFormaPago(facturaObject.getProperty("formaPago").toString());
                                factura.setMarcaTelefono(facturaObject.getProperty("marcaTelefono").toString());
                                factura.setNombreCliente(facturaObject.getProperty("nombreCliente").toString());
                                factura.setNombreTelefono(facturaObject.getProperty("nombreTelefono").toString());
                                factura.setPreciofinal(Double.parseDouble(facturaObject.getProperty("preciofinal").toString()));
                                facturas.add(factura);
                            }
                        } else {
                            return new AsyncTaskResult<>(new Exception("Unexpected response type: " + response.getClass().getName()));
                        }
                        return new AsyncTaskResult<>(facturas);
                    } catch (Exception e) {
                        return new AsyncTaskResult<>(e);
                    }
                } catch (Exception e) {
                    return new AsyncTaskResult<>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<FacturaModel>> result) {
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