package com.apis.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {

    public String pegarData() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String pegarHora() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
