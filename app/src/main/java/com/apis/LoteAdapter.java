package com.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apis.database.DbController;
import com.apis.models.FileControl;
import com.apis.models.Lote;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
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
                        .setMessage("Tem certeza que deseja excluir este lote? Seus dados também serão excluídos.")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DbController database = new DbController(view.getContext());

                                if(database.excluir(lote.getId(), "Lote")) {
                                    removerLote(lote);

                                    //Apaga arquivo
                                    FileControl fileControl = new FileControl();
                                    fileControl.deleteLoteFile(context, lote.getId());

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

        //Action botão EXPORTAR
        holder.btnExportarDados.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tela de compartilhamento
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File fileWithinMyDir = new File(Environment.getExternalStorageDirectory() + "/apis/", "dados_Lote"+lote.getId()+".cvs");

                if(fileWithinMyDir.exists()) {
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + "/apis/dados_Lote"+lote.getId()+".cvs"));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Dados Lote " + lote.getId() + ": " + lote.getNome());

                    context.startActivity(Intent.createChooser(intentShareFile, "Enviar para"));
                }
            }
        });


        //Acessar lote
        holder.itemLista.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
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
