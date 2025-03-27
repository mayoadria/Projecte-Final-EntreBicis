package com.copernic.backend.Backend.Excepciones;

public class ExcepcionEmailDuplicado extends RuntimeException{

    public ExcepcionEmailDuplicado(String message) {
        super(message);
    }

}
