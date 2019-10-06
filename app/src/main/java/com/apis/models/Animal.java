package com.apis.models;

import java.io.Serializable;

public class Animal implements Serializable {

    private int id;
    private String nome;
    private int LoteId;

    public Animal(int id, String nome, int LoteId){
        this.id = id;
        this.nome = nome;
        this.LoteId = LoteId;
    }

    public int getId(){ return this.id; }
    public String getNome(){ return this.nome; }
    public int getLoteId(){ return this.LoteId; }

    @Override
    public boolean equals(Object o){
        return this.id == ((Animal)o).id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}
