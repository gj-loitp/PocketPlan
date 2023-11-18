package com.roy93group.noteking.data.todolist

import com.google.gson.annotations.SerializedName

class Task(
    @SerializedName(value = "t")
    var title: String,

    @SerializedName(value = "p")
    var priority: Int,

    @SerializedName(value = "i")
    var isChecked: Boolean
)