package com.apis.models;

import java.io.Serializable;

public class Lote implements Serializable {

    private int id;
    private String nome;
    private String experimento;

    public Lote(int id, String nome, String experimento){
        this.id = id;
        this.nome = nome;
        this.experimento = experimento;
    }

    public int getId(){ return this.id; }
    public String getNome(){ return this.nome; }
    public String getExperimento(){ return this.experimento; }

    @Override
    public boolean equals(Object o){
        return this.id == ((Lote)o).id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}
