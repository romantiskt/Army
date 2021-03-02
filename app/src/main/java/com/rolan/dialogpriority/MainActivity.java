package com.rolan.dialogpriority;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rolan.army.Army;
import com.rolan.army.ArmyLog;
import com.rolan.army.RequestListener;
import com.rolan.army.TaskPriority;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Army.openLog();
        setContentView(R.layout.activity_main);
        findViewById(R.id.click_test1).setOnClickListener(this);
        findViewById(R.id.click_test2).setOnClickListener(this);

    }


    public void showDialog(String dialogName){
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setTitle(dialogName)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Army.with(MainActivity.this).cancel(dialogName);
                    }
                })
                .setMessage("这里是文本0");

        builder.create().show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.click_test1:
                showTest1();
                break;
            case R.id.click_test2:
                Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showTest1() {
        Army.with(this).load("low dialog")
                .priority(TaskPriority.LOW.setPriorityExtra(10))
                .listener(new RequestListener() {
                    @Override
                    public void canShow() {
                        showDialog("low dialog");
                    }
                })
                .post();
        Army.with(this).load("normal dialog")
                .priority(TaskPriority.NORMAL)
                .listener(new RequestListener() {
                    @Override
                    public void canShow() {
                        showDialog("normal dialog");
                    }
                })
                .post();
        Army.with(this).load("importance dialog")
                .priority(TaskPriority.IMPORTANCE)
                .listener(new RequestListener() {
                    @Override
                    public void canShow() {
                        showDialog("importance dialog");
                    }
                })
                .post();
        Army.with(this).load("admin dialog")
                .priority(TaskPriority.ADMIN)
                .listener(new RequestListener() {
                    @Override
                    public void canShow() {
                        showDialog("admin dialog");
                    }
                })
                .post();

    }
}