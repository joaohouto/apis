package com.apis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apis.database.DbController;
import com.apis.models.Animal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListaAnimais extends AppCompatActivity {

    private String nomeLote;
    private String nomeAnimal;
    private int idLote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_animais);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pegarDadosActivityPassada();

        configurarLista();

        //Botão flutuante
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               salvarAnimal();
            }
        });

        //Botão de exportar dados
        ImageButton btnExportar = findViewById(R.id.btnExportar);
        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbController database = new DbController(getBaseContext());
                Toast.makeText(getApplicationContext(), database.exportarDados(idLote), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pegarDadosActivityPassada(){

        if (getIntent().hasExtra("lote_nome") && getIntent().hasExtra("lote_id")){
            nomeLote = getIntent().getStringExtra("lote_nome");
            idLote = getIntent().getIntExtra("lote_id", 9999);

            TextView txtNomeLote = (TextView)findViewById(R.id.txtNomeLote);
            txtNomeLote.setText(nomeLote);

            TextView txtIdLote = (TextView)findViewById(R.id.txtIdLote);
            txtIdLote.setText("ID: "+idLote);

        }

    }

    public void configurarLista(){

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerAnimais);

        DbController database = new DbController(this);
        ArrayList<Animal> animais = database.retornarAnimais(idLote);

        recyclerView.setAdapter(new AnimalAdapter(animais, this));
        LinearLayoutManager layout = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layout);
    }

    public void salvarAnimal(){

        LayoutInflater layoutInflater = LayoutInflater.from(ListaAnimais.this);
        View promptView = layoutInflater.inflate(R.layout.prompt_animal, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListaAnimais.this);
        alertDialogBuilder.setView(promptView);

        final EditText txtNomeAnimal = (EditText) promptView.findViewById(R.id.textNomeAnimal);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        nomeAnimal = txtNomeAnimal.getText().toString();

                        ////Salva no BD
                        DbController database = new DbController(getBaseContext());
                        if (!database.adicionarAnimal(nomeAnimal, idLote)) {
                            //Exibe mensagem de erro
                            Toast.makeText(getApplicationContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show();
                        }
                        configurarLista();
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

}
