package com.rolan.dialogpriority

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rolan.army.Army.Companion.with
import com.rolan.army.RequestListener
import com.rolan.army.SingleTask
import com.rolan.army.TaskPriority

/**
 * Created by Rolan.Wang on 2/3/21.4:25 PM
 * 描述：
 */
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        findViewById<View>(R.id.click_test2).setOnClickListener { view -> view.postDelayed({ clickTest() }, 800) }
    }

    fun showDialog(dialogName: String?) {
        val builder = AlertDialog.Builder(this)
                .setTitle(dialogName)
                .setOnDismissListener { with(this@SecondActivity).cancel(dialogName) }
                .setMessage("hello world")
        builder.create().show()
    }

    private fun clickTest() {
        with(this).load("low dialog")
                .priority(TaskPriority.LOW)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("low dialog")
                    }
                })
                .post()
        with(this).load("normal dialog")
                .priority(TaskPriority.NORMAL)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("normal dialog")
                    }
                })
                .post()
        with(this).load("importance dialog")
                .priority(TaskPriority.IMPORTANCE)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("importance dialog")
                    }
                })
                .post()
        with(this).load("admin dialog")
                .priority(TaskPriority.ADMIN)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("admin dialog")
                    }
                })
                .post()
    }
}