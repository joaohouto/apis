package com.apis;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apis.database.DbController;
import com.apis.models.Comportamento;
import com.apis.models.DateTime;
import com.apis.models.Preferencia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class AdicionarComportamento extends AppCompatActivity {

    private String nomeAnimal;
    private String nomeLote;
    private int idAnimal;
    private int idLote;

    private String comportamento = "";
    private String obS = "";

    private DateTime dateTime = new DateTime();
    DbController database = new DbController(this);

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_comportamento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pegarDadosActivityPassada();
        pegarUltimaAtualizacao();
        configurarListaComportamentos();
        configurarListaPreferencias();

        getSupportActionBar().setTitle(nomeAnimal);

        //Click do botão 'Salvar'
        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                salvarDados();
            }
        });

    }

    public void configurarListaComportamentos() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerComportamento);

        ArrayList<Comportamento> comportamentos =  database.retornarComportamento(idAnimal);

        Collections.reverse(comportamentos);

        recyclerView.setAdapter(new ComportamentoAdapter(comportamentos, this));
        LinearLayoutManager layout = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layout);
    }

    public void configurarListaPreferencias(){

        RecyclerView recyclerViewComp = (RecyclerView) findViewById(R.id.recyclerComportamentoPersonalizado);

        DbController database = new DbController(this);
        ArrayList<Preferencia> preferencias = database.retornarPreferencia();


        recyclerViewComp.setAdapter(new PreferenciaAdapter(preferencias, this));
        LinearLayoutManager layoutComp = new LinearLayoutManager(this);

        recyclerViewComp.setLayoutManager(layoutComp);
    }

    private void pegarDadosActivityPassada(){

        if (getIntent().hasExtra("animal_nome") && getIntent().hasExtra("animal_id") && getIntent().hasExtra("lote_id")){
            nomeAnimal = getIntent().getStringExtra("animal_nome");
            idAnimal = getIntent().getIntExtra("animal_id", 9999);
            idLote = getIntent().getIntExtra("lote_id", 9999);

            nomeLote = database.retornarNomeLote(idLote);

        }

    }

    private void pegarUltimaAtualizacao() {
        TextView atualizadoEm = (TextView) findViewById(R.id.atualizadoEm);
        String lastUpdate = database.pegarUltimoUpdateAnimal(idAnimal);

        if(lastUpdate != ""){
            atualizadoEm.setText("Atualizado em " + lastUpdate);
        }else {
            atualizadoEm.setText("Não existem observações para este animal.");
        }

        //Verifica se irá mandar notificações
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final boolean receberNotificacao = sharedPreferences.getBoolean("notifications", false);

        final TextView lblNotificacoes = (TextView) findViewById(R.id.lbl_notificacoes_info);
        lblNotificacoes.setText(receberNotificacao ? ">> Notificações ativadas" : ">> Notificações desativadas");
    }

    public void salvarDados(){

        ///Pega os dados
        RadioGroup btnGroupFisio = (RadioGroup) findViewById(R.id.btnGroupFisio);
        switch (btnGroupFisio.getCheckedRadioButtonId()) {
            case R.id.radioPastej:
                comportamento = comportamento + "Pastejando;";
                break;
            case R.id.radioOP:
                comportamento = comportamento +  "Ociosa em pé;";
                break;
            case R.id.radioOD:
                comportamento = comportamento +  "Ociosa deitada;";
                break;
            case R.id.radioRumPe:
                comportamento = comportamento +  "Ruminando em pé;";
                break;
            case R.id.radioRumDeit:
                comportamento = comportamento +  "Ruminando deitada;";
                break;
            default:
                comportamento = comportamento +  ";";
                break;
        }

        RadioGroup btnGroupRepro = (RadioGroup) findViewById(R.id.btnGroupRepro);
        switch (btnGroupRepro.getCheckedRadioButtonId()) {
            case R.id.radioAcMonta:
                comportamento = comportamento + "Aceita monta;";
                break;
            case R.id.radioMontaOutra:
                comportamento = comportamento + "Monta outra;";
                break;
            case R.id.radioInqueta:
                comportamento = comportamento + "Inquieta;";
                break;
            default:
                comportamento = comportamento +  ";";
                break;
        }

        RadioGroup btnGroupSombra = (RadioGroup) findViewById(R.id.btnGroupSombra);
        switch (btnGroupSombra.getCheckedRadioButtonId()) {
            case R.id.radioSol:
                comportamento = comportamento + "Sol;";
                break;
            case R.id.radioSombra:
                comportamento = comportamento + "Sombra;";
                break;
            default:
                comportamento = comportamento +  ";";
                break;
        }



        EditText txtObs = (EditText) findViewById(R.id.textObs);
        obS = txtObs.getText().toString();
        ///Fim Pega os dados


        ///Alerta para confirmação dos dados
        LayoutInflater layoutInflater = LayoutInflater.from(AdicionarComportamento.this);
        View promptView = layoutInflater.inflate(R.layout.alert_comportamento, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdicionarComportamento.this);
        alertDialogBuilder.setView(promptView);

        final TextView lblNome = (TextView) promptView.findViewById(R.id.lbl_nome_alert);
        final TextView lblComportamento = (TextView) promptView.findViewById(R.id.lbl_comportamento);
        final TextView lblObs = (TextView) promptView.findViewById(R.id.lbl_obs_alert);

        lblNome.setText("Nome: "+nomeAnimal);
        lblComportamento.setText("Comportamento: "+comportamento.replace(";", " | "));
        lblObs.setText("Observação: "+obS);


        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {

                        ////Salva no BD
                        DbController database = new DbController(getBaseContext());
                        if (database.adicionarComportamento(idAnimal, nomeAnimal, dateTime.pegarData(), dateTime.pegarHora(), comportamento, obS)) {

                            //Trata o horário
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            boolean usarNtp = sharedPreferences.getBoolean("datetime_web", false);

                            String horaDefinida = dateTime.pegarHora();
                            String dataDefinida = dateTime.pegarData();

                            salvarTxt(idAnimal, nomeAnimal, dataDefinida, horaDefinida, comportamento, obS);

                            //Verifica se o usuário quer receber a notificação
                            boolean receberNotificacao = sharedPreferences.getBoolean("notifications", false);

                            if(receberNotificacao){

                                //Pega o intervalo de atualizações das preferencias do  app
                                String intervaloAtualizacaoString = sharedPreferences.getString("intervalo_atualizacao", "0");
                                int intervaloAtualizacao = Integer.parseInt(intervaloAtualizacaoString);

                                //Define o lembrete de adicionar mais dados
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    definirAlarme(intervaloAtualizacao);
                                }
                            }

                            Toast.makeText(getApplicationContext(), "Salvo!", Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                comportamento = "";
                                obS = "";
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    public void salvarTxt(int idAnimal, String nomeAnimal, String data, String hora, String comportamento, String obS){

        //String conteudo = "ID Animal;Data;Hora;Fisiologico;Reprodutivo;Uso de sombra;Observação;";
        String conteudo = idAnimal+";"+nomeAnimal+";"+data+";"+hora+";"+comportamento+";"+obS;

        try {
                try {

                    File f = new File(Environment.getExternalStorageDirectory() + "/apis", "dados_Lote"+idLote+"_"+database.retornarNomeLote(idLote).replace(" ", "")+".cvs");
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void definirAlarme(int tempo){
        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("animal_nome", nomeAnimal);
        intent.putExtra("animal_id", idAnimal);
        intent.putExtra("lote_id", idLote);
        intent.putExtra("lote_nome", nomeLote);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,  SystemClock.elapsedRealtime() + tempo * 60000, alarmIntent);
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
