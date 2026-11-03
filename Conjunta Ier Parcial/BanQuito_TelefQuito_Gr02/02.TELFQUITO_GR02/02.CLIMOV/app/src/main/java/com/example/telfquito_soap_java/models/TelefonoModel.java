package com.example.telfquito_soap_java.models;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

public class TelefonoModel implements KvmSerializable, Serializable {
    private int codTelefono;
    private int disponible;
    private String marca;
    private String nombre;
    private String precio; // Change to String

    // Getters and setters
    public int getCodTelefono() {
        return codTelefono;
    }

    public void setCodTelefono(int codTelefono) {
        this.codTelefono = codTelefono;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return codTelefono;
            case 1:
                return disponible;
            case 2:
                return marca;
            case 3:
                return nombre;
            case 4:
                return precio;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                codTelefono = Integer.parseInt(value.toString());
                break;
            case 1:
                disponible = Integer.parseInt(value.toString());
                break;
            case 2:
                marca = value.toString();
                break;
            case 3:
                nombre = value.toString();
                break;
            case 4:
                precio = value.toString();
                break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "codTelefono";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "disponible";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "marca";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "nombre";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "precio";
                break;
            default:
                break;
        }
    }
}