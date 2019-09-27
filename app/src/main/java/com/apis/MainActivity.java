package com.apis;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apis.database.DbController;
import com.apis.models.Lote;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String nomeLote;
    private String nomeExperimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pedirPermissoes();

        configurarLista();

        //Botão flutuante
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarLote();
            }
        });

    }

    public void configurarLista() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerAnimais);

        DbController database = new DbController(this);
        ArrayList<Lote> lotes = database.retornarLotes();

        recyclerView.setAdapter(new LoteAdapter(lotes, this));
        LinearLayoutManager layout = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layout);
    }

    public void salvarLote() {

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.prompt_lote, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText txtNomeLote = (EditText) promptView.findViewById(R.id.textNomeAnimal);
        final EditText txtExperimento = (EditText) promptView.findViewById(R.id.textExperimento);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        nomeLote = txtNomeLote.getText().toString();
                        nomeExperimento = txtExperimento.getText().toString();

                        ////Salva no BD
                        DbController database = new DbController(getBaseContext());
                        if (!database.adicionarLote(nomeLote, nomeExperimento)) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void pedirPermissoes() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        }


    }
}
