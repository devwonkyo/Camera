package com.example.camera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {
    EditText update_edittext;
    Button update_button;
    Button delete_button;
    String content;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        update_button = findViewById(R.id.button_update);
        delete_button = findViewById(R.id.button_delete);
        update_edittext = findViewById(R.id.update_edittext);

        getAndSetIntentData();

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDBhelper = new MyDatabaseHelper(UpdateActivity.this);
                myDBhelper.updateData(id,update_edittext.getText().toString());
                finish();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });



    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("content")&&getIntent().hasExtra("id")){
            content = getIntent().getStringExtra("content");
            id = getIntent().getStringExtra("id");
            update_edittext.setText(content);
        }else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myDBhelper = new MyDatabaseHelper(UpdateActivity.this);
                myDBhelper.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}