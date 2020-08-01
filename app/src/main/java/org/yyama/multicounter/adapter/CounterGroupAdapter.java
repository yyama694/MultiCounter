package org.yyama.multicounter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.yyama.multicounter.R;
import org.yyama.multicounter.model.Counter;

import java.util.List;

class CounterGroupAdapter extends RecyclerView.Adapter<CounterGroupAdapter.CounterGroupViewHolder> {
    private List<Counter> counterGroup;
    public class CounterGroupViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView num;
        public CounterGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.counter_title);
            num = (TextView) itemView.findViewById(R.id.counter_num);
        }
    }

    @NonNull
    @Override
    public CounterGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.counter,parent,false);
        CounterGroupViewHolder vh = new CounterGroupViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CounterGroupViewHolder holder, int position) {
        holder.title.setText(counterGroup.get(position).getTitle());
        holder.num.setText(String.valueOf(counterGroup.get(position).getNum()));
    }

    @Override
    public int getItemCount() {
        return counterGroup.size();
    }

}
