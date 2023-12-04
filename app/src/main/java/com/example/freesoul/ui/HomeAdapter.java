package com.example.freesoul.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freesoul.R;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private Context context;
    private List<HomeModel> personList;
    private static ClickListener clickListener;
    public HomeAdapter(Context context, ArrayList<HomeModel> personList){
        this.context = context;
        this.personList = personList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final HomeModel person = personList.get(position);
        holder.tvName.setText(person.getName());
        holder.tvNumber.setText(person.getNumber());

        holder.tvCall.setOnClickListener (v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+person.getNumber()));
            if
            (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE}, 1);
                return;
            }
            context.startActivity(intent);
        });
        holder.tvMessage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("sms:"+person.getNumber()));
            context.startActivity(intent);
        });
        holder.tvWhatsapp.setOnClickListener(v -> {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setPackage("com.whatsapp");
            String url = "https://api.whatsapp.com/send?phone="+person.getNumber()+"&text=";
            sendIntent.setData(Uri.parse(url));
            context.startActivity(sendIntent);
        });
        holder.personLayout.setOnClickListener(v -> {
            String dataName = holder.tvName.getText().toString();
            String dataNumber = holder.tvNumber.getText().toString();
            String dataInstagram = person.getInstagram();
            String dataGroup = person.getGroup();
            Intent intent = new Intent(context, DetailPersonActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("cname", dataName);
            bundle.putString("cnumber", dataNumber);
            bundle.putString("cinstagram", dataInstagram);
            bundle.putString("cgroup", dataGroup);
            intent.putExtras(bundle);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout personLayout;
        TextView tvName, tvNumber, tvCall, tvMessage, tvWhatsapp, tvDelete;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            personLayout = itemView.findViewById(R.id.person_layout);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvCall = itemView.findViewById(R.id.tv_call);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvWhatsapp = itemView.findViewById(R.id.tv_whatsapp);
            tvDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(),itemView);;
        }
    }
    public void
    setOnItemClickListener(HomeAdapter.ClickListener clickListener) {
        HomeAdapter.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
