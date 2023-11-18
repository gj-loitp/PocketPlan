package com.roy93group.noteking.data.notelist

import com.roy93group.noteking.data.Checkable
import java.lang.NullPointerException
import java.util.*

class NoteList : LinkedList<Note>(), Checkable {
    /**
     * Creates a note with the given parameters and saves it to file.
     * @param title Displayed title of the note.
     * @param content Contents of the note.
     * @param color Color of the note.
     */
    fun addNote(title: String, content: String, color: NoteColors) {
        this.push(Note(title, content, color))
    }

    /**
     * Small helper function to add a note object, used for undoing deletions
     */
    fun addFullNote(note: Note): Int {
        addNote(note.title, note.content!!, note.color)
        return this.indexOf(note)
    }

    override fun check() {
        this.forEach {
            if(it.color == null || it.title == null) {
                throw NullPointerException()
            }
        }
    }

}