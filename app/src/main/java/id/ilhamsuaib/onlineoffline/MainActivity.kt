package id.ilhamsuaib.onlineoffline

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log.i
import android.view.LayoutInflater
import android.widget.Toast
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.database.*
import com.google.gson.Gson
import id.ilhamsuaib.onlineoffline.databinding.ItemListBinding
import id.ilhamsuaib.onlineoffline.model.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add.view.*

class MainActivity : AppCompatActivity() {

    private val tag = this::class.java.simpleName
    private val itemRef = FirebaseDatabase.getInstance().getReference("item")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAdd.setOnClickListener {
            showAddItemDialog()
        }
        getItem()
    }

    private fun showAddItemDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add, ConstraintLayout(this), false)
        AlertDialog.Builder(this)
                .setTitle("Tambah item")
                .setView(view)
                .setPositiveButton("Simpan", { dialog, _ ->
                    saveItem(view.etNama.text.toString())
                    dialog.cancel()
                })
                .setNegativeButton("Batal", { dialog, _ ->
                    dialog.cancel()
                })
                .show()
    }

    private fun saveItem(nama: String) {
        val item = Item(id = System.currentTimeMillis(), nama = nama)
        itemRef.push().setValue(item, { databaseError, _ ->
            if (databaseError != null)
                showMessage("Data could not be saved ${databaseError.message}")
            else
                showMessage("Data saved successfully.")
        })
    }

    private fun getItem() {
        itemRef.keepSynced(true)
        itemRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError?) {
                databaseError?.toException()?.printStackTrace()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val listItem = mutableListOf<Item>()
                dataSnapshot?.children?.forEach {
                    val item = it.getValue(Item::class.java)
                    item?.let { listItem.add(it) }
                }
                i(tag, "itemList : ${Gson().toJsonTree(listItem)}")
                displayData(listItem)
            }
        })
    }

    private fun displayData(listItem: List<Item>) {
        recItem.layoutManager = LinearLayoutManager(this)
        LastAdapter(listItem, BR.item)
                .map<Item, ItemListBinding>(R.layout.item_list)
                .into(recItem)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
