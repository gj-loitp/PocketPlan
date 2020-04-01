package com.example.j7_003.logic

import android.content.Context
import java.io.File


class Database(private val context: Context) {

    var taskList = ArrayList<Task>()
    private var file = File(context.filesDir, "TaskList")

    init {
        createTaskListFile()
        update()
        loadDebugList()
    }

    /**
     * To be implemented...
     * Will load in the task list from local files.
     */
    private fun loadTaskList() {
        //replace this by reading from file
        //loadDebugList()

    }

    /**
     * Task list used to debug some task list functionality.
     */
    fun loadDebugList(){
        //loads in list of default values, testing empty strings, long strings, duplicate elements
        taskList = arrayListOf<Task>(
            Task(file.readText(), 3),
            Task("KotlinKotlinKotlinKotlin", 2),
            Task("Python", 1),
            Task("C++", 2),
            Task("", 2),
            Task("Python", 1),
            Task("HTML", 2),
            Task("Javascript", 2),
            Task("CSS", 2)
        )
    }

    /**
     * To be implemented...
     * Will update storage files from remote host and will be called on init.
     */
    private fun update() {

    }

    /**
     * If the file to safe tasks in doesn't exit, then the file
     * will be created
     */
    private fun createTaskListFile() {
        val isNewFileCreated: Boolean = file.createNewFile()

        if(isNewFileCreated) {
            file.writeText("test, 2\nnew, 4\n")
        }
    }

    /**
     * To be implemented...
     * Will read each line in file and split each line into taskName and taskPriority
     * and create with the a task with the given parameters.
     *
     * file content example:
     * 1. new Task, 3
     * 2. new new Task, 1
     * ...
     *
     * task name: "new Task", priority: 3
     */
    private fun debugReadFile() {
        file.forEachLine { n -> val taskProperties =  n.split(", ");
            println("${taskProperties[0]} ${taskProperties[1].toInt()}")
        }
    }
}