package com.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;

import com.apis.database.DbController;
import com.apis.models.FileControl;
import com.apis.models.Lote;
import com.apis.models.Preferencia;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PreferenciaAdapter extends RecyclerView.Adapter<PreferenciaViewHolder>{

    private ArrayList<Preferencia> preferencias;
    private Context context;
    private boolean exibirExcluir;


    public PreferenciaAdapter(ArrayList preferencias, boolean exibirExcluir, Context context){
        this.preferencias = preferencias;
        this.context = context;
        this.exibirExcluir = exibirExcluir;
    }

    public void removerPreferencia(Preferencia preferencia){
        int position = preferencias.indexOf(preferencia);
        preferencias.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public PreferenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_preferencia, parent, false);
        PreferenciaViewHolder preferenciaViewHolder = new PreferenciaViewHolder(view);
        return preferenciaViewHolder;
    }

    @Override
    public void onBindViewHolder(PreferenciaViewHolder holder, final int position)
    {
        holder.nome.setText(preferencias.get(position).getNome());

        final Preferencia preferencia = preferencias.get(position);

        //Action botão excluir
        holder.txtDelete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este comportamento?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DbController database = new DbController(view.getContext());

                                if(database.excluir(preferencia.getId(), "Preferencia")) {
                                    removerPreferencia(preferencia);
                                    Toast.makeText(context, "Excluído!", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context, "Erro ao excluir!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });

        //Click no checkbox
        holder.nome.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileControl fc = new FileControl();
                fc.updateValue(preferencias.get(position).getNome());

            }
        });

        if(this.exibirExcluir) {
            holder.txtDelete.setVisibility(View.VISIBLE);
        } else {
            holder.txtDelete.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return preferencias.size();
    }
}
