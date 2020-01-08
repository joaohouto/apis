package com.apis.models;

import java.io.Serializable;

public class Comportamento implements Serializable {

    private int id;
    private String nome_animal;
    private int id_animal;
    private String data;
    private String hora;
    private String comportamento;
    private String obs;

    public Comportamento(int id, String nome_animal, int id_animal, String data, String hora, String comportamento, String obs) {
        this.id = id;
        this.nome_animal = nome_animal;
        this.id_animal = id_animal;
        this.data = data;
        this.hora = hora;
        this.comportamento = comportamento;
        this.obs = obs;
    }

    public int getId() { return id; }
    public int getId_animal() { return id_animal; }
    public String getNome_animal() { return nome_animal; }
    public String getData() { return data; }
    public String getHora() { return hora; }
    public String getComportamento() { return comportamento; }
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
