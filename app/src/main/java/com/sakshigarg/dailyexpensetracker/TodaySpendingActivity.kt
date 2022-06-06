package com.sakshigarg.dailyexpensetracker

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodaySpendingActivity : AppCompatActivity() {

    private lateinit var toolbars:Toolbar
    private lateinit var totalAmountSpentOn: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView:RecyclerView
    private lateinit var fab:FloatingActionButton
    private lateinit var loader:ProgressDialog
    private lateinit var mAuth:FirebaseAuth
    private  var onlineUserId =""
    private lateinit var expensesRef: DatabaseReference

    private lateinit var todayItemsAdapter: TodayItemsAdapter
     lateinit var myDataList: MutableList<Data>
    private lateinit var rupee: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_spending)

        toolbars = findViewById(R.id.toolbar)
        setSupportActionBar(toolbars)
        supportActionBar?.title = "Today's spending"
        totalAmountSpentOn = findViewById(R.id.totalAmountSpentOn)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        rupee = resources.getString(R.string.rupee)

        fab = findViewById(R.id.fab)
        loader= ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth.currentUser!!.uid
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        var linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        //var myDataList = MutableList<String>()
        myDataList = arrayListOf()
        todayItemsAdapter =TodayItemsAdapter(this, myDataList as ArrayList<Data>)
        recyclerView.adapter = todayItemsAdapter

        readItems()

        fab.setOnClickListener {
            addItemSpentOn()
        }
    }

    private fun readItems() {
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)

        var reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)
        var query:Query = reference.orderByChild("date").equalTo(date)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                myDataList.clear()

                for (snap: DataSnapshot in snapshot.children){
                    var data: Data? = snap.getValue(Data::class.java)
                    myDataList.add(data!!)
                }

                todayItemsAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

                var totalAmount =0
                for(ds:DataSnapshot in snapshot.children){
                    var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                    var total: Object? = map.get("amount")
                    var pTotal:Int = Integer.parseInt(total.toString())
                    totalAmount+=pTotal

                    totalAmountSpentOn.setText("Total Day's Spending: "+rupee+totalAmount)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }

    private fun addItemSpentOn() {
        val myDialog = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.input_layout, null)
        myDialog.setView(view)
        var dialog = myDialog.create()
        dialog.setCancelable(false)

        val itemSpinner: Spinner = view.findViewById(R.id.itemSpinner)
        val amount: EditText = view.findViewById(R.id.amount)
        val note: EditText = view.findViewById(R.id.note)
        val cancel: Button = view.findViewById(R.id.cancel)
        val save: Button = view.findViewById(R.id.save)

        note.visibility= View.VISIBLE

        save.setOnClickListener {
            val Amount: String = amount.text.toString()
            val Item: String = itemSpinner.selectedItem.toString()
            val notes:String = note.text.toString()

            if (TextUtils.isEmpty(Amount)) {
                amount.setError("Amount is required.!")
                return@setOnClickListener
            }

            if (Item.equals("Select item")) {
                Toast.makeText(this, "Select a valid item", Toast.LENGTH_LONG).show()
            }
            if(TextUtils.isEmpty(notes)){
                note.setError("Note is required")
                return@setOnClickListener
            }
            else {
                loader.setMessage("Adding a budget item")
                loader.setCanceledOnTouchOutside(false)
                loader.show()

                val id: String = expensesRef.push().key!!
                val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
                val cal: Calendar = Calendar.getInstance()
                val date: String = dateFormat.format(cal.time)

                val epoch = MutableDateTime()
                epoch.setDate(0)
                val now = DateTime()
                val weeks = Weeks.weeksBetween(epoch,now)
                val months: Months = Months.monthsBetween(epoch, now)

                val itemday= Item+date
                val itemWeek = Item+weeks.weeks
                val itemMonth = Item+months.months

                val data: Data =
                    Data(Item, date, id, itemday,itemWeek,itemMonth, Integer.parseInt(Amount),weeks.weeks ,months.months,notes)
                expensesRef.child(id).setValue(data).addOnCompleteListener {
                    if (it.isSuccessful()) {
                        Toast.makeText(this, "Expense added successfully", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }

                    loader.dismiss()
                }

            }
            dialog.dismiss()

        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }



        dialog.show()
    }
}