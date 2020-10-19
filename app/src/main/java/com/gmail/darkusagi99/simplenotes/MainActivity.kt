package com.gmail.darkusagi99.simplenotes

import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.note.view.*

class MainActivity : AppCompatActivity() {

    var noteList = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadQuery("%")

    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private fun LoadQuery(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Content")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "ID")
        noteList.clear()
        if (cursor.moveToFirst()) {

            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val content = cursor.getString(cursor.getColumnIndex("Content"))

                noteList.add(Note(id, title, content))

            } while (cursor.moveToNext())
        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this, noteList)
        //set adapter
        notesLv.adapter = myNotesAdapter

        //get total number of tasks from ListView
        val total = notesLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "You have $total note(s) in list..."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        //searchView
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%" + newText + "%")
                return false
            }
        });

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNote -> {
                    startActivity(Intent(this, NewNoteActivity::class.java))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context: Context? = null

        constructor(context: Context, listNotesAdapter: ArrayList<Note>) : super() {
            this.listNotesAdapter = listNotesAdapter
            this.context = context
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.note, null)
            val myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.title
            myView.contentTv.text = myNote.content
            //delete button click
            myView.deleteBtn.setOnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.id.toString())
                dbManager.delete("ID=?", selectionArgs)
                LoadQuery("%")
            }
            //edit//update button click
            myView.editBtn.setOnClickListener {
                GoToUpdateFun(myNote)
            }
            //copy btn click
            myView.copyBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get content
                val desc = myView.contentTv.text.toString()
                //concatinate
                val s = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s // add to clipboard
                Toast.makeText(this@MainActivity, "Copied...", Toast.LENGTH_SHORT).show()
            }
            //share btn click
            myView.shareBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.contentTv.text.toString()
                //concatenate
                val s = title + "\n" + desc
                //share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }

    }

    private fun GoToUpdateFun(myNote: Note) {
        var intent = Intent(this, NewNoteActivity::class.java)
        intent.putExtra("ID", myNote.id)
        intent.putExtra("name", myNote.title)
        intent.putExtra("des", myNote.content)
        startActivity(intent) //start activity
    }

}
