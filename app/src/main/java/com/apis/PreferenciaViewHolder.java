package com.apis;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PreferenciaViewHolder extends RecyclerView.ViewHolder {

    final CheckBox nome;
    final TextView txtDelete;

    public PreferenciaViewHolder(View view) {
        super(view);
        nome = (CheckBox) view.findViewById(R.id.checkItem);
        txtDelete = (TextView) view.findViewById(R.id.textDeletePref);
    }

}
