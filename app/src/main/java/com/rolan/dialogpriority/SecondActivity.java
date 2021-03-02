package com.rolan.dialogpriority;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rolan.army.Army;
import com.rolan.army.RequestListener;
import com.rolan.army.TaskPriority;

/**
 * Created by Rolan.Wang on 2/3/21.4:25 PM
 * 描述：
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.click_test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickTest();
                    }
                },800);

            }
        });
    }

    public void showDialog(String dialogName){
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setTitle(dialogName)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Army.with(SecondActivity.this).cancel(dialogName);
                    }
                })
                .setMessage("这里是文本0");

        builder.create().show();

    }

    private void clickTest() {
        Army.with(this).load("low dialog")
                .priority(TaskPriority.LOW)
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
