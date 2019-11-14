package com.apis;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
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



        /// Criar uma Intent para abrir uma Activity
        Intent intent = new Intent(this, ListaAnimais.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CANAL")
//                                                                        ^ ID DO CANAL EM QUE A NOTIFICACAO SERA DISPARADA

                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Meu titulo")
                .setContentText("Meu texto informativo")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .setContentIntent(pendingIntent) /// Define a Intent que sera aberta ao clicar na notificação

                .setAutoCancel(true);

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

    private void createNotificationChannel() {

        // Cria o canal de notificação para a API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CANAL", "nomeDoCanal", importancia);
//                                                            ^ ID DO CANAL
            channel.setDescription("Descrição do canal");

            // Registra o canal no sistema, você não pode mudar a importância ou outros comportamentos depois disso
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                intent.putExtra("lote_id", idLote);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
