package com.project.locateme.Utills;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.locateme.R;
import com.project.locateme.ViewLocationActivity;

import java.util.List;


//Recyclerview adapter to show list of items
public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.MyVIewHolder> {

    List<mLocation>list;
    Context context;

    public RecyclerViewAdapter(List<mLocation> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyVIewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_location,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyVIewHolder holder, int position) {
        holder.latitude.setText(list.get(position).getLatitude()+"");
        holder.logntide.setText(list.get(position).getLongitude()+"");
        holder.location.setText(list.get(position).getMarkerName());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ViewLocationActivity.class);
                intent.putExtra("latitude",list.get(position).getLatitude());
                intent.putExtra("longitude",list.get(position).getLongitude());
                intent.putExtra("name",list.get(position).getMarkerName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyVIewHolder extends RecyclerView.ViewHolder {
        TextView view;
        TextView location;
        TextView latitude;
        TextView logntide;


        public MyVIewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView.findViewById(R.id.view);
            location=itemView.findViewById(R.id.location);
            latitude=itemView.findViewById(R.id.latitude);
            logntide=itemView.findViewById(R.id.longitude);

        }
    }
}
