package com.example.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ImageView imageHome;
    RecyclerView recyclerView;
    TextView textView_nodata;

    MyDatabaseHelper myDBhelper;
    ArrayList<String> list_contents;
    ArrayList<String> list_ids;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        imageHome = findViewById(R.id.image_home);
        textView_nodata = findViewById(R.id.textnodata);
        recyclerView = findViewById(R.id.listLayout_recyclerview);
        myDBhelper = new MyDatabaseHelper(ListActivity.this);
        list_contents = new ArrayList<>();
        list_ids = new ArrayList<>();

        dataInArrays();

        customAdapter = new CustomAdapter(list_contents,list_ids);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        imageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }

    void dataInArrays(){
        Cursor cursor = myDBhelper.readAllData();
        if(cursor.getCount() == 0 ){
            textView_nodata.setVisibility(View.VISIBLE);
        }else{
            textView_nodata.setVisibility(View.INVISIBLE);
            while(cursor.moveToNext()){
                list_ids.add(cursor.getString(0));
                list_contents.add(cursor.getString(1));
            }
        }
    }


    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{

        ArrayList list_contents;
        ArrayList list_ids;

        public CustomAdapter(ArrayList list_contents,ArrayList list_ids) {
            this.list_contents = list_contents;
            this.list_ids = list_ids;

        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.list_item,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
            holder.textView_list_num.setText(String.valueOf(position+1));
            holder.textView_list_content.setText(list_contents.get(position).toString());

            holder.item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),UpdateActivity.class);
                    intent.putExtra("content",String.valueOf(list_contents.get(position)));
                    intent.putExtra("id",String.valueOf(list_ids.get(position)));
                    startActivity(intent);
                    finish();
                }
            });


        }

        @Override
        public int getItemCount() {
            return list_contents.size();
        }


        public class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView textView_list_num;
            TextView textView_list_content;
            ConstraintLayout item_layout;


            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                textView_list_num = itemView.findViewById(R.id.list_num);
                textView_list_content = itemView.findViewById(R.id.list_content);
                item_layout = itemView.findViewById(R.id.item_layout);
            }
        }
    }
}