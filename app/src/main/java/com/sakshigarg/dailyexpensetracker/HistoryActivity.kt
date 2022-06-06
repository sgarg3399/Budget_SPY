package com.sakshigarg.dailyexpensetracker

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var todayItemsAdapter: TodayItemsAdapter
    private lateinit var myDataList : MutableList<Data>

    private lateinit var mAuth:FirebaseAuth
    private var onlineUserId = ""
    private lateinit var expensesRef:DatabaseReference
    private lateinit var personalRef:DatabaseReference

    private lateinit var settingsToolbar:Toolbar
    private lateinit var search:AppCompatButton
    private lateinit var historyTotalAmountSpent:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        settingsToolbar = findViewById(R.id.my_Food_Toolbar)
        setSupportActionBar(settingsToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setTitle("History")
        settingsToolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })

        search= findViewById(R.id.search)
        historyTotalAmountSpent = findViewById(R.id.historyTotalAmountSpent)
        mAuth = FirebaseAuth.getInstance()
        onlineUserId= mAuth.currentUser!!.uid

        recyclerView = findViewById(R.id.recycler_view_id_feed)
        var layoutManager:LinearLayoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        myDataList = arrayListOf()

        todayItemsAdapter = TodayItemsAdapter(this, myDataList as ArrayList<Data>)
        recyclerView.adapter = todayItemsAdapter

        search.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        var datePickerDialog:DatePickerDialog = DatePickerDialog(this,this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val months:Int = month+1
        var newDay:String
        if(dayOfMonth in 1..9){
            newDay = "0"+dayOfMonth
        }else{
            newDay = dayOfMonth.toString()
        }
        val date:String = newDay +"-"+"0"+months+"-"+year

        Toast.makeText(this, date, Toast.LENGTH_LONG).show()

        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)
        val query:Query = reference.orderByChild("date").equalTo(date)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {



                myDataList.clear()

                for (snap: DataSnapshot in snapshot.children){
                    var data: Data? = snap.getValue(Data::class.java)
                    myDataList.add(data!!)
                }

                todayItemsAdapter.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE

                var totalAmount =0
                for(ds: DataSnapshot in snapshot.children){
                    val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                    val total: Object? = map.get("amount")
                    val pTotal:Int = Integer.parseInt(total.toString())
                    totalAmount+=pTotal

                   if(totalAmount>0){
                        historyTotalAmountSpent.visibility = View.VISIBLE
                        historyTotalAmountSpent.setText("This day you spent: "+totalAmount)
                    }



                }

                if(!snapshot.exists()){
                    recyclerView.visibility = View.GONE
                    historyTotalAmountSpent.setText("You have not spent anything on this day")
                    return
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistoryActivity, error.message, Toast.LENGTH_LONG).show()
            }
        }
        )


    }
}