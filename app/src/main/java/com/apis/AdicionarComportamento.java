package com.apis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apis.database.DbController;
import com.apis.database.DbHelper;
import com.apis.models.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdicionarComportamento extends AppCompatActivity {

    private String nomeAnimal;
    private int idAnimal;
    private int idLote;

    private String compFisio = "";
    private String compRepro = "";
    private String usoSombra = "";
    private String obS = "";

    private DateTime dateTime = new DateTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_comportamento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pegarDadosActivityPassada();

        //Seta TextView última atualização
        DbController database = new DbController(this);
        TextView atualizadoEm = (TextView) findViewById(R.id.atualizadoEm);
        atualizadoEm.setText("Atualizado em "+ database.pegarUltimoUpdateAnimal(idAnimal));

        //Click do botão 'Salvar'
        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                salvarDados();
            }
        });

    }

    private void pegarDadosActivityPassada(){

        if (getIntent().hasExtra("animal_nome") && getIntent().hasExtra("animal_id") && getIntent().hasExtra("lote_id")){
            nomeAnimal = getIntent().getStringExtra("animal_nome");
            idAnimal = getIntent().getIntExtra("animal_id", 9999);
            idLote = getIntent().getIntExtra("lote_id", 9999);

            TextView txtInfo = (TextView)findViewById(R.id.lbl_info);
            txtInfo.setText(nomeAnimal);
        }

    }


    public void salvarDados(){

        ///Pega os dados
        RadioGroup btnGroupFisio = (RadioGroup) findViewById(R.id.btnGroupFisio);
        switch (btnGroupFisio.getCheckedRadioButtonId()) {
            case R.id.radioPastej:
                compFisio = "Pastejando";
                break;
            case R.id.radioOP:
                compFisio = "Ociosa em pé";
                break;
            case R.id.radioOD:
                compFisio = "Ociosa deitada";
                break;
            case R.id.radioRumPe:
                compFisio = "Ruminando em pé";
                break;
            case R.id.radioRumDeit:
                compFisio = "Ruminando deitada";
                break;
        }

        RadioGroup btnGroupRepro = (RadioGroup) findViewById(R.id.btnGroupRepro);
        switch (btnGroupRepro.getCheckedRadioButtonId()) {
            case R.id.radioAcMonta:
                compRepro = "Aceita monta";
                break;
            case R.id.radioMontaOutra:
                compRepro = "Monta outra";
                break;
            case R.id.radioInqueta:
                compRepro = "Inquieta";
                break;
        }

        RadioGroup btnGroupSombra = (RadioGroup) findViewById(R.id.btnGroupSombra);
        switch (btnGroupSombra.getCheckedRadioButtonId()) {
            case R.id.radioSol:
                usoSombra = "Sol";
                break;
            case R.id.radioSombra:
                usoSombra = "Sombra";
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

        final TextView lblNome = (TextView) promptView.findViewById(R.id.lbl_nome);
        final TextView lblFisio = (TextView) promptView.findViewById(R.id.lbl_fisio);
        final TextView lblRepro = (TextView) promptView.findViewById(R.id.lbl_repro);
        final TextView lblSombra = (TextView) promptView.findViewById(R.id.lbl_sombra);
        final TextView lblObs = (TextView) promptView.findViewById(R.id.lbl_obs);

        lblNome.setText("Nome: "+nomeAnimal);
        lblFisio.setText("Comportamento fisiológico: "+compFisio);
        lblRepro.setText("Comportamento reprodutivo: "+compRepro);
        lblSombra.setText("Uso da sombra: "+usoSombra);
        lblObs.setText("Observação: "+obS);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {

                        ////Salva no BD
                        DbController database = new DbController(getBaseContext());
                        if (database.adicionarComportamento(idAnimal, nomeAnimal, dateTime.pegarData(), dateTime.pegarHora(), compFisio, compRepro, usoSombra, obS)) {

                            Toast.makeText(getApplicationContext(), "Salvo!", Toast.LENGTH_LONG).show();
                            //salva os dados no txt também
                            salvarTxt(idAnimal, nomeAnimal, dateTime.pegarData(), dateTime.pegarHora(), compFisio, compRepro, usoSombra, obS);

                            //Trata os dados e define o lembrete de adicionar mais dados
                            int horasAlarme = Integer.parseInt(dateTime.pegarHoras());
                            int minutosAlarme = Integer.parseInt(dateTime.pegarMinutos());

                            StartUpBootReceiver.setAlarm(getBaseContext(), horasAlarme, minutosAlarme);

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
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    public void salvarTxt(int idAnimal, String nomeAnimal, String data, String hora, String compFisio, String compRepro, String usoSombra, String obS){

            String conteudo = "ID: "+idAnimal+";"+nomeAnimal+";Data/Hora: "+data+" "+hora+";Fisiologico: "+compFisio+"; Reprodutivo: "+compRepro+"; Uso de sombra: "+usoSombra+"; Obs: "+obS;

            try {
                try {

                    File f = new File(Environment.getExternalStorageDirectory() + "/apis", "dados_Lote"+idLote+".cvs");
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



}
