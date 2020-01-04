package com.apis.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import com.apis.models.Animal;
import com.apis.models.Lote;

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
    public boolean adicionarComportamento(int id_animal, String nome_animal, String data, String hora, String compFisio, String compRepro, String usoSombra, String obs){

        ContentValues cv = new ContentValues();
        cv.put("Animal_nome", nome_animal);
        cv.put("Animal_id", id_animal);
        cv.put("data", data);
        cv.put("hora", hora);
        cv.put("fisiologico", compFisio);
        cv.put("reprodutivo", compRepro);
        cv.put("usosombra", usoSombra);
        cv.put("observacao", obs);

        return database.getWritableDatabase().insert("Comportamento", null, cv) > 0;
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

        return data + " às "+hora;
    }




    //Exportar dados
    public String exportarDados(int idLote){

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT * FROM Comportamento WHERE Animal_id IN (SELECT id FROM Animal WHERE Lote_id = "+idLote+");", null);
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nome_animal = cursor.getString(cursor.getColumnIndex("Animal_nome"));
            String id_animal = cursor.getString(cursor.getColumnIndex("Animal_id"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String hora = cursor.getString(cursor.getColumnIndex("hora"));
            String compFisio = cursor.getString(cursor.getColumnIndex("fisiologico"));
            String compRepro = cursor.getString(cursor.getColumnIndex("reprodutivo"));
            String usoSombra = cursor.getString(cursor.getColumnIndex("usosombra"));
            String obs = cursor.getString(cursor.getColumnIndex("observacao"));

            String conteudo = "{\n" +
                    "\t\"animal_id\": "+id_animal+",\n" +
                    "\t\"animal_nome\": "+nome_animal+",\n" +
                    "\t\"data\": "+data+",\n" +
                    "\t\"hora\": "+hora+",\n" +
                    "\t\"fisiologico\": "+compFisio+",\n" +
                    "\t\"reprodutivo\": "+compRepro+",\n" +
                    "\t\"sombra\": "+usoSombra+",\n" +
                    "\t\"observacao\": "+obs+"\n" +
                    "}, ";

            try {
                try {

                    File f = new File(Environment.getExternalStorageDirectory() + "/apis", "dados_"+retornarNomeLote(idLote).replace(" ", "")+".json");

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

        }
        cursor.close();

        return "Dados exportados para \"apis/dados.json\"";

    }

}
