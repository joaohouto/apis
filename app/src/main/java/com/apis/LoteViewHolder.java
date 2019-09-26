package com.apis;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class LoteViewHolder extends RecyclerView.ViewHolder {

    final ConstraintLayout itemLista;
    final TextView nome;
    final TextView experimento;
    final ImageButton btnExcluir;

    public LoteViewHolder(View view) {
        super(view);
        itemLista = (ConstraintLayout) view.findViewById(R.id.itemLista);
        nome = (TextView) view.findViewById(R.id.lbl_nome_animal);
        experimento = (TextView) view.findViewById(R.id.lbl_id_animal);
        btnExcluir = (ImageButton) view.findViewById(R.id.btnDelete);
    }

}
