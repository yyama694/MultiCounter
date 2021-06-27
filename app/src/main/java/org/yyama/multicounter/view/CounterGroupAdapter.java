package org.yyama.multicounter.view;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.yyama.multicounter.R;
import org.yyama.multicounter.model.CounterGroup;

class CounterGroupAdapter extends RecyclerView.Adapter<CounterGroupAdapter.MyViewHolder> {
    private CounterGroup counterGroup;
    private Activity activity;
    private ViewGroup parent;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView counterNum;
        public TextView counterTitle;
        public View counterRow;
        public int pos;

        public MyViewHolder(View v) {
            super(v);
            counterNum = v.findViewById(R.id.counter_num);
            counterRow = v;
            counterTitle = v.findViewById(R.id.counter_title);

            // つまみ（ハンドル）がタッチされたら、ドラッグできるようにする
            v.findViewById(R.id.handle).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        MainActivity mc = (MainActivity) v.getContext();
                        mc.getItemTouchHelper().startDrag(MyViewHolder.this);
                    }
                    return false;
                }
            });
        }
    }

    public CounterGroupAdapter(CounterGroup counterGroup, Activity activity) {
        this.activity = activity;
        this.counterGroup = counterGroup;
    }

    @Override
    public int getItemViewType(int position) {
        return counterGroup.get(position).getSize().getId();
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        Log.d("counterAdapter","onCreateViewHolder viewType" + viewType);
        View v=null;
        switch (viewType){
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.counter_small, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.counter, parent, false);
                break;
            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.counter_learge, parent, false);
                break;
        }
        MyViewHolder vh = new MyViewHolder(v);
        this.parent = parent;
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        MyViewHolder newHolder = new MyViewHolder(LayoutInflater.from(activity)
//                        .inflate(R.layout.counter_learge, parent, false));
//        bindViewHolder(newHolder, position);
        holder.counterNum.setText(String.valueOf(counterGroup.get(position).getNum()));
        holder.counterRow.setTag(counterGroup.get(position).getId());
        holder.counterTitle.setText(counterGroup.get(position).getTitle());
        holder.pos = position;
        Log.d("counterAdapter","itemCount num:" +String.valueOf(counterGroup.get(position).getNum()));
        Log.d("counterAdapter","itemCount:" +getItemCount());
    }

    @Override
    public int getItemCount() {
        return counterGroup.size();
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.d("counterAdapter" ,"onAttachedToRecyclerView");
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull CounterGroupAdapter.MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Log.d("counterAdapter" ,"onViewAttachedToWindow");
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull CounterGroupAdapter.MyViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d("counterAdapter" ,"onViewRecycled");
    }
}
