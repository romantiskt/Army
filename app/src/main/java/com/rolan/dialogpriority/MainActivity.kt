package com.rolan.dialogpriority

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rolan.army.Army
import com.rolan.army.Army.Companion.openLog
import com.rolan.army.RequestListener
import com.rolan.army.SingleTask
import com.rolan.army.TaskPriority
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var handler = Handler()
    var tvMsg: TextView? = null
    var buffer = StringBuffer()
    var lastTask:SingleTask?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openLog()
        setContentView(R.layout.activity_main)
        tvMsg = findViewById(R.id.tv_msg)
        findViewById<View>(R.id.click_test1).setOnClickListener(this)
        findViewById<View>(R.id.click_test2).setOnClickListener(this)
        findViewById<View>(R.id.click_task_add).setOnClickListener(this)
        findViewById<View>(R.id.click_task_remove).setOnClickListener(this)
        findViewById<View>(R.id.click_task_clear).setOnClickListener(this)
    }

    fun showDialog(dialogName: String?) {
        val builder = AlertDialog.Builder(this)
                .setTitle(dialogName)
                .setOnDismissListener { Army.with(this@MainActivity).cancel(dialogName) }
                .setMessage("hello world")
        builder.create().show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.click_test1 -> showTest1()
            R.id.click_test2 -> {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }
            R.id.click_task_add -> addTask()
            R.id.click_task_remove -> removeTask()
            R.id.click_task_clear -> {
                Army.getIns(this)!!.clear()
                buffer = StringBuffer()
                tvMsg!!.text = ""
            }
        }
    }

    private fun removeTask() {
        if (lastTask==null) return
        val msg = "[remove]${lastTask?.taskName}_${lastTask?.id}"
        log(msg)
        val taskName = lastTask?.taskName
        lastTask=null
        Army.with(this@MainActivity).cancel(taskName)
    }

    private fun log(msg: String) {
        buffer.append(msg)
        buffer.append("\n")
        tvMsg?.text = buffer.toString()
        Log.d("wang", msg)
    }

    private fun addTask() {
        val taskName = (System.currentTimeMillis() / 1000).toString()
        val msg = "[add]$taskName"
        log(msg)
        Army.with(this).load(taskName)
                .priority(TaskPriority.LOW)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        val msg = "[start]${newTask?.taskName}_${newTask?.id }"
                        lastTask=newTask;
                        log(msg)
                    }
                })
                .post()
    }

    private fun showTest1() {
        Army.with(this).load("low dialog")
                .priority(TaskPriority.LOW.setPriorityExtra(10))
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("low dialog")
                    }
                })
                .post()
        Army.with(this).load("normal dialog")
                .priority(TaskPriority.NORMAL)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("normal dialog")
                    }
                })
                .post()
        Army.with(this).load("importance dialog")
                .priority(TaskPriority.IMPORTANCE)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("importance dialog")
                    }
                })
                .post()
        Army.with(this).load("admin dialog")
                .priority(TaskPriority.ADMIN)
                .listener(object : RequestListener {
                    override fun startTask(newTask: SingleTask?) {
                        showDialog("admin dialog")
                    }
                })
                .post()
    }
}