package com.gmail.darkusagi99.simplenotes

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_note.*

class NewNoteActivity : AppCompatActivity() {

    val dbTable = "Notes"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        try {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                id = bundle.getInt("ID", 0)
            }
            if (id != 0){
                if (bundle != null) {
                    titleEt.setText(bundle.getString("Title"))
                    descEt.setText(bundle.getString("Content"))
                }
            }
        } catch (ex:Exception){}
    }

    fun addFunc(view: View){
        val dbManager = DbManager(this)

        val values = ContentValues()
        values.put("Title", titleEt.text.toString())
        values.put("Content", descEt.text.toString())

        if (id ==0) {
            val insertId = dbManager.insert(values)
            if (insertId > 0){
                Toast.makeText(this, "Création OK", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Erreur lors de la création", Toast.LENGTH_SHORT).show()
            }
        } else {
            val selectionArgs = arrayOf(id.toString())
            val updateId = dbManager.update(values, "ID=?", selectionArgs)
            if (updateId > 0){
                Toast.makeText(this, "Création OK", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Erreur lors de la création", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
