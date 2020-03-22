package com.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apis.database.DbController;
import com.apis.models.Animal;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalViewHolder> {

    private ArrayList<Animal> animais;
    private Context context;


    public AnimalAdapter(ArrayList animais, Context context){
        this.animais = animais;
        this.context = context;
    }

    public void removerAnimal(Animal animal){
        int position = animais.indexOf(animal);
        animais.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_animal, parent, false);
        AnimalViewHolder animalViewHolder = new AnimalViewHolder(view);
        return animalViewHolder;
    }

    @Override
    public void onBindViewHolder(AnimalViewHolder holder, final int position)
    {
        holder.nome.setText(animais.get(position).getNome());
        holder.idAnimal.setText("ID: "+animais.get(position).getId());

        final Animal animal = animais.get(position);

        //Action botão excluir
        holder.btnExcluir.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este animal?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DbController database = new DbController(view.getContext());
                                if(database.excluir(animal.getId(), "Animal")) {
                                    removerAnimal(animal);
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


        //Acessar tela de Adição de Comportamento
        holder.itemLista.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Passa o id do animal clicado para a Activity AdicionarComportamento
                Intent intent = new Intent(context, AdicionarComportamento.class);
                intent.putExtra("animal_nome", animais.get(position).getNome());
                intent.putExtra("animal_id", animais.get(position).getId());
                intent.putExtra("lote_id", animais.get(position).getLoteId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return animais.size();
    }

}
