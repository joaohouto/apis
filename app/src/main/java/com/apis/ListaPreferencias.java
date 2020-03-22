package com.apis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apis.R;
import com.apis.database.DbController;
import com.apis.models.Animal;
import com.apis.models.FileControl;
import com.apis.models.Preferencia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListaPreferencias extends AppCompatActivity {

    private String nomePreferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_preferencias);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Comportamentos");

        configurarLista();

        //Arquivo de comportamentos temporarios
        FileControl fc = new FileControl();
        fc.deleteTmpFile();

        //Botão flutuante
        FloatingActionButton fab = findViewById(R.id.btnAdicionarComportamento);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarComportamento();
            }
        });
    }

    public void configurarLista(){

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerPreferencias);

        DbController database = new DbController(this);
        ArrayList<Preferencia> preferencias = database.retornarPreferencia();

        TextView nenhumaPref = (TextView) findViewById(R.id.txtNenhumComportamentoPersonalizado);
        ImageView alertImg = (ImageView) findViewById(R.id.alertImg);
        if(preferencias.size() > 0){
            nenhumaPref.setVisibility(View.INVISIBLE);
            alertImg.setVisibility(View.INVISIBLE);
        }else {
            nenhumaPref.setVisibility(View.VISIBLE);
            alertImg.setVisibility(View.VISIBLE);
        }


        recyclerView.setAdapter(new PreferenciaAdapter(preferencias, true, this));
        LinearLayoutManager layout = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layout);
    }

    private void adicionarComportamento(){
        LayoutInflater layoutInflater = LayoutInflater.from(ListaPreferencias.this);
        View promptView = layoutInflater.inflate(R.layout.prompt_preferencia, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListaPreferencias.this);
        alertDialogBuilder.setView(promptView);

        final EditText txtNomePreferencia = (EditText) promptView.findViewById(R.id.textNomePreferencia);

        txtNomePreferencia.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        nomePreferencia = txtNomePreferencia.getText().toString();

                        ////Salva no BD
                        DbController database = new DbController(getBaseContext());
                        if (!database.adicionarPreferencia(nomePreferencia, "")) {
                            Toast.makeText(getApplicationContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show();
                        }
                        configurarLista();
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                finish();
                break;
            default:break;
        }
        return true;
    }
}
