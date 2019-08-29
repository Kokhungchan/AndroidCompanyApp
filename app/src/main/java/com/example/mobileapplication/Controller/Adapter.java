package com.example.mobileapplication.Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobileapplication.Model.Company;
import com.example.mobileapplication.R;

import java.util.ArrayList;
import java.util.List;

// adapter for handling the recyclerviewin MainActivity.class
public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM=0;
    private static final int LOADING=1;
    private List<Company> companies;
    private Context context;
    private boolean isloadingAdded = false;
    private ItemClickListener OnItemClickListener;

    public Adapter(Context context, ItemClickListener clickListener){
        this.context = context;
        companies = new ArrayList<>();
        OnItemClickListener = clickListener;

    }

    public List<Company> getCompanies(){
        return companies;
    }

    public void setCompanies(List<Company> companies){
        this.companies = companies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View view = inflater.inflate(R.layout.list_loading, parent, false);
                viewHolder = new LoadingVH(view);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater){
        RecyclerView.ViewHolder viewHolder;
        View view1 = inflater.inflate(R.layout.list_layout, parent, false);
        viewHolder = new CompanyVH(view1);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Company company = companies.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                CompanyVH movieVH = (CompanyVH) holder;
                movieVH.textView.setText(company.getName());
                movieVH.textView2.setText(company.getCompanyAdd());
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return companies == null ? 0 : companies.size();
    }

    public int getItemViewType(int position) {
        return (position == companies.size() - 1 && isloadingAdded) ? LOADING : ITEM;
    }

    public interface ItemClickListener{
        void onClickItem(View view, int position);
    }

    public void setItemClickListener(ItemClickListener clickListener){
        OnItemClickListener = clickListener;
    }


    public Adapter(Context context){
        this.context = context;
        companies = new ArrayList<>();
    }


    /*------------------------------------------------------------------------------------------------*/

    public void add(Company c){
        companies.add(c);
        notifyItemInserted(companies.size() -1);
    }

    public void removeAll() {
        final int size = companies.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                companies.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    public void addAll(List<Company> cList){
        for (Company c : cList) {
            add (c);
        }
    }

    public void addLoadingFooter() {
        isloadingAdded = true;
        add(new Company());
    }

    public Company getItem(int position) {

        return companies.get(position);
    }

    public void removeLoadingFooter() {
        isloadingAdded = false;

        int position = companies.size() -1;
        Company item = getItem (position);

        if (item!=null) {
            companies.remove(position);
            notifyItemRemoved(position);
        }
    }

    /*-------------------------------------------------------------------------------------------*/

    protected class CompanyVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private TextView textView2;

        public CompanyVH (View itemView) {
            super (itemView);

            textView = itemView.findViewById(R.id.Company_title);
            textView2 = itemView.findViewById(R.id.Company_add);
           itemView.setOnClickListener(new View.OnClickListener()  {
                @Override
                        public void onClick(View view) {
                    OnItemClickListener.onClickItem(view, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view){
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder{
        public LoadingVH(View itemView){
            super(itemView);
        }
    }
}


