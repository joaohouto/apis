package com.apis.models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.apis.database.DbController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ContentHandler;
import java.util.ArrayList;

public class FileControl {

    public void writeTmp(String value){
        try {

            File f = new File(Environment.getExternalStorageDirectory() + "/apis/temp", "marked_options.tmp");
            if (!f.exists()){
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(f, true);
            out.write(value.getBytes());
            out.write('\n');
            out.flush();
            out.close();


        } catch (FileNotFoundException e) {

        } catch (IOException e){

        }
    }

    public void deleteValue(String value){

        ArrayList<String> valores = new ArrayList<String>();
        String path = Environment.getExternalStorageDirectory() + "/apis/temp/marked_options.tmp";


        try {
            FileReader arq = new FileReader(path);
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine();

            while (linha != null) {
                if(!linha.equals(value)){
                    valores.add(linha); //Adiciona os valores diferentes do especificado
                }

                linha = lerArq.readLine();
            }

            arq.close();
        } catch (IOException e) {
            Log.e("FILE: ", "Erro na abertura do arquivo: "+ e.getMessage());
        }

        File oldFile = new File(path);
        oldFile.delete();

        //Grava os novos valores
        while(!valores.isEmpty()){
            writeTmp(valores.remove(0));
        }

    }

    public void updateValue(String value){

        ArrayList<String> valores = new ArrayList<String>();
        String path = Environment.getExternalStorageDirectory() + "/apis/temp/marked_options.tmp";
        boolean existeValorIgual = false;


        try {
            FileReader arq = new FileReader(path);
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine();

            while (linha != null) {
                if(linha.equals(value)){
                    existeValorIgual = true;
                }
                linha = lerArq.readLine();
            }

            arq.close();
        } catch (IOException e) {
            Log.e("FILE: ", "Erro na abertura do arquivo: "+ e.getMessage());
        }

        if(existeValorIgual){
            deleteValue(value);
            Log.e("FILE: ", "Excluido!");
        } else {
            writeTmp(value);
            Log.e("FILE: ", "Salvo!");
        }
    }

    public void deleteTmpFile(){
        String path = Environment.getExternalStorageDirectory() + "/apis/temp/marked_options.tmp";
        File oldFile = new File(path);
        oldFile.delete();
    }

    public ArrayList<String> returnValues() {

        ArrayList<String> valores = new ArrayList<String>();
        String path = Environment.getExternalStorageDirectory() + "/apis/temp/marked_options.tmp";


        try {
            FileReader arq = new FileReader(path);
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine();

            while (linha != null) {
                valores.add(linha);
                linha = lerArq.readLine();
            }

            arq.close();
        } catch (IOException e) {
            Log.e("FILE: ", "Erro na abertura do arquivo: " + e.getMessage());
        }

        return valores;
    }

    public void deleteLoteFile(Context context, int idLote){

        DbController database = new DbController(context);

        String path = Environment.getExternalStorageDirectory() + "/apis/dados_Lote" + idLote + "_" + database.retornarNomeLote(idLote).replace(" ", "") + ".cvs";
        File oldFile = new File(path);
        oldFile.delete();
    }
}
