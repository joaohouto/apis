package com.apis.models;

import java.io.Serializable;

public class Animal implements Serializable {

    private int id;
    private String nome;

    public Animal(int id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public int getId(){ return this.id; }
    public String getNome(){ return this.nome; }

    @Override
    public boolean equals(Object o){
        return this.id == ((Animal)o).id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}
