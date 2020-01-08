package com.apis.models;

import java.io.Serializable;

public class Preferencia implements Serializable {

    private int id;
    private String nome;
    private String valor;

    public Preferencia(int id, String nome, String valor){
        this.id = id;
        this.nome = nome;
        this.valor = valor;
    }

    public int getId(){ return this.id; }
    public String getNome(){ return this.nome; }
    public String getValor(){ return this.valor; }

    @Override
    public boolean equals(Object o){
        return this.id == ((Preferencia)o).id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}
