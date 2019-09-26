package com.apis.models;

import java.io.Serializable;

public class Comportamento implements Serializable {

    private int id;
    private String nome_animal;
    private String id_animal;
    private String data;
    private String hora;
    private String compFisio;
    private String compRepro;
    private String usoSombra;
    private String obs;

    public Comportamento(int id, String nome_animal, String id_animal, String data, String hora, String compFisio, String compRepro, String usoSombra, String obs) {
        this.id = id;
        this.nome_animal = nome_animal;
        this.id_animal = id_animal;
        this.data = data;
        this.hora = hora;
        this.compFisio = compFisio;
        this.compRepro = compRepro;
        this.usoSombra = usoSombra;
        this.obs = obs;
    }

    public int getId() { return id; }
    public String getId_animal() { return id_animal; }
    public String getNome_animal() { return nome_animal; }
    public String getData() { return data; }
    public String getHora() { return hora; }
    public String getCompFisio() { return compFisio; }
    public String getCompRepro() { return compRepro; }
    public String getUsoSombra() { return usoSombra; }
    public String getObs() { return obs; }


    @Override
    public boolean equals(Object o){
        return this.id == ((Comportamento)o).id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}
