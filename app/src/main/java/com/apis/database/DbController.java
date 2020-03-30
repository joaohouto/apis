package com.apis.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import com.apis.models.Animal;
import com.apis.models.Comportamento;
import com.apis.models.FileControl;
import com.apis.models.Lote;
import com.apis.models.Preferencia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DbController {

    private DbHelper database;
    public DbController(Context ctx){
        database = new DbHelper(ctx);
    }


    ///LOTES
    public boolean adicionarLote(String nome, String experimento){

        ContentValues cv = new ContentValues();
        cv.put("nome", nome);
        cv.put("experimento", experimento);

        return database.getWritableDatabase().insert("Lote", null, cv) > 0;
    }
    public String retornarNomeLote(int idLote){

        String nome = "";
        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT nome FROM Lote WHERE id = " + idLote, null);
        while (cursor.moveToNext()) {
            nome = cursor.getString(cursor.getColumnIndex("nome"));
        }
        cursor.close();
        return nome;

    }
    public boolean loteExiste(String nomeLote){

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Lote WHERE nome = '"+nomeLote+"'", null);
        if(cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }
    public ArrayList<Lote> retornarLotes(){

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Lote", null);

        ArrayList<Lote> lotes = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nome = cursor.getString(cursor.getColumnIndex("nome"));
            String experimento = cursor.getString(cursor.getColumnIndex("experimento"));
            lotes.add(new Lote(id, nome, experimento));
        }
        cursor.close();
        return lotes;
    }


    ///ANIMAL
    public boolean adicionarAnimal(String nome, int loteId){

        ContentValues cv = new ContentValues();
        cv.put("nome", nome);
        cv.put("Lote_id", loteId);

        return database.getWritableDatabase().insert("Animal", null, cv) > 0;
    }
    public boolean animalExiste(String nomeAnimal){

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Animal WHERE nome = '"+nomeAnimal+"'", null);
        if(cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }
    public ArrayList<Animal> retornarAnimais(int loteId){

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Animal WHERE Lote_id = "+loteId, null);

        ArrayList<Animal> animais = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nome = cursor.getString(cursor.getColumnIndex("nome"));
            animais.add(new Animal(id, nome, loteId));
        }
        cursor.close();
        return animais;
    }


    ///COMPORTAMENTO
    public boolean adicionarComportamento(int id_animal, String nome_animal, String data, String hora, String comportamento, String obs){

        ContentValues cv = new ContentValues();
        cv.put("Animal_nome", nome_animal);
        cv.put("Animal_id", id_animal);
        cv.put("data", data);
        cv.put("hora", hora);
        cv.put("comportamento", comportamento);
        cv.put("observacao", obs);

        return database.getWritableDatabase().insert("Comportamento", null, cv) > 0;
    }
    public ArrayList<Comportamento> retornarComportamento(int animalId){

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Comportamento WHERE Animal_id = "+animalId, null);

        ArrayList<Comportamento> comportamentos = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nome_animal = cursor.getString(cursor.getColumnIndex("Animal_nome"));
            int id_animal = cursor.getInt(cursor.getColumnIndex("Animal_id"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String hora = cursor.getString(cursor.getColumnIndex("hora"));
            String comportamento = cursor.getString(cursor.getColumnIndex("comportamento"));
            String obs =  cursor.getString(cursor.getColumnIndex("observacao"));
            comportamentos.add(new Comportamento(id, nome_animal, id_animal, data, hora, comportamento, obs));
        }
        cursor.close();
        return comportamentos;
    }


    ///PREFERENCIA
    public boolean adicionarPreferencia(String nome_preferencia, String valor_preferencia){

        ContentValues cv = new ContentValues();
        cv.put("nome", nome_preferencia);
        cv.put("valor", valor_preferencia);

        return database.getWritableDatabase().insert("Preferencia", null, cv) > 0;
    }
    public ArrayList<Preferencia> retornarPreferencia(){

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Preferencia", null);

        ArrayList<Preferencia> preferencias = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nome_preferencia = cursor.getString(cursor.getColumnIndex("nome"));
            String valor_preferencia = cursor.getString(cursor.getColumnIndex("valor"));

            preferencias.add(new Preferencia(id, nome_preferencia, valor_preferencia));
        }
        cursor.close();
        return preferencias;
    }


    //Excluir
    public boolean excluir(int id, String tableName){
        return database.getWritableDatabase().delete(tableName, "id=?", new String[]{ id + "" }) > 0;
    }

    //Pegar ID
    public int pegarId(String tableName, String nomeLote) {

        int id = 0;
        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT id FROM " + tableName + " WHERE nome = " + nomeLote, null);
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return id;
    }

    //Pegar data/hora da última atualização lote
    public String pegarUltimoUpdateAnimal(int idAnimal) {

        String data = "";
        String hora = "";

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Comportamento WHERE Animal_id = " + idAnimal, null);
        while (cursor.moveToNext()) {
            data = cursor.getString(cursor.getColumnIndex("data"));
            hora = cursor.getString(cursor.getColumnIndex("hora"));
        }
        cursor.close();

        if(data == hora){
            return "";
        }else {
            return data + " às " + hora;
        }
    }

    //Exportar dados
    public boolean exportarDados(int idLote, int idAnimal){

        //Antes de fazer a consulta
        //Apaga o arquivo de dados do animal, se houver
        File oldFile = new File(Environment.getExternalStorageDirectory() + "/apis/exportado/", "dados_Lote"+idLote+"_Animal"+idAnimal+".cvs");
        oldFile.delete();

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Comportamento WHERE Animal_id = " + idAnimal, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nome_animal = cursor.getString(cursor.getColumnIndex("Animal_nome"));
            String id_animal = cursor.getString(cursor.getColumnIndex("Animal_id"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String hora = cursor.getString(cursor.getColumnIndex("hora"));
            String comportamento = cursor.getString(cursor.getColumnIndex("comportamento"));
            String obs = cursor.getString(cursor.getColumnIndex("observacao"));

            //String conteudo = "ID Animal;Data;Hora;Fisiologico;Reprodutivo;Uso de sombra;Observação;";
            String conteudo = id_animal+";"+nome_animal+";"+data+";"+hora+";"+comportamento+";"+obs;

            try {
                try {

                    //Cria outro arquivo, mais novo
                    File f = new File(Environment.getExternalStorageDirectory() + "/apis/exportado/", "dados_Lote"+idLote+"_Animal"+idAnimal+".cvs");

                    if (!f.exists()){
                        f.getParentFile().mkdirs();
                        f.createNewFile();
                    }

                    FileOutputStream out = new FileOutputStream(f, true);
                    out.write(conteudo.getBytes());
                    out.write('\n');
                    out.flush();
                    out.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            cursor.close();
        }

        return true;
    }

    //Apagar tudo
    public void apagarTudo() {
        database.getWritableDatabase().rawQuery("Delete from Lote", null);
        database.getWritableDatabase().rawQuery("Delete from Comportamento", null);
        database.getWritableDatabase().rawQuery("Delete from Animal", null);
        database.getWritableDatabase().rawQuery("Delete from Preferencia", null);

        FileControl fc = new FileControl();
        fc.deleteEverthing();

    }
}
