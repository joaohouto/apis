package com.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apis.database.DbController;
import com.apis.models.Lote;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class LoteAdapter extends RecyclerView.Adapter<LoteViewHolder>{

    private ArrayList<Lote> lotes;
    private Context context;


    public LoteAdapter(ArrayList lotes, Context context){
        this.lotes = lotes;
        this.context = context;
    }

    public void removerLote(Lote lote){
        int position = lotes.indexOf(lote);
        lotes.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public LoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_lote, parent, false);
        LoteViewHolder loteViewHolder = new LoteViewHolder(view);
        return loteViewHolder;
    }

    @Override
    public void onBindViewHolder(LoteViewHolder holder, final int position)
    {
        holder.nome.setText(lotes.get(position).getNome());
        holder.experimento.setText("["+lotes.get(position).getId()+"] "+lotes.get(position).getExperimento());

        final Lote lote = lotes.get(position);

        //Action botão excluir
        holder.btnExcluir.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este lote?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DbController database = new DbController(view.getContext());
                                if(database.excluir(lote.getId(), "Lote")) {
                                    removerLote(lote);
                                    Snackbar.make(view, "Excluído!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }else{
                                    Snackbar.make(view, "Erro ao excluir!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });


        //Acessar lote
        holder.itemLista.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

        //Passa o id e o nome do lote clicado para a Activity ListaAnimais
        Intent intent = new Intent(context, ListaAnimais.class);
        intent.putExtra("lote_nome", lotes.get(position).getNome());
        intent.putExtra("lote_id", lotes.get(position).getId());
        context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return lotes.size();
    }
}
