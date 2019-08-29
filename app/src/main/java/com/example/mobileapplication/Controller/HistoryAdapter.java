package com.example.mobileapplication.Controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobileapplication.R;

import java.util.ArrayList;
import java.util.List;

// adapter for handling the recyclerview in History.class
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<String> mDataset = new ArrayList();
    private ItemClickListener onItemClickListener;

    public interface ItemClickListener {
        void onClickItem(View view, int position);
    }

    public void setItemClickListener(ItemClickListener clickListener){
        onItemClickListener = clickListener;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_layout_history, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.textView.setText(mDataset.get(i));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //----------------------------------------------------------------------------

    public List<String> getmDataset() {
        return mDataset;
    }

    public void addAll(List<String> cList){
        clear();
        for (String c : cList) {
            add (c);
        }
    }

    public void add(String c) {
        mDataset.add(0, c);
        notifyItemInserted(mDataset.size() -1);
    }

    public void clear() {
        while (getItemCount()>0){
            remove(getItem(0));
        }
    }

    public void remove(String c) {
        int pos = mDataset.indexOf(c);
        if (pos > -1) {
            mDataset.remove(pos);
        notifyItemRemoved(pos);
        }
    }

    public String getItem(int pos) {
        return mDataset.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder(View itemView){
            super(itemView);

            textView = itemView.findViewById(R.id.Company_title);
            itemView.setOnClickListener(new View.OnClickListener()  {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClickItem(view, getAdapterPosition());
                }
            });
        }
    }

    public HistoryAdapter(List<String> myDataset) {
        addAll(myDataset);
    }
}
