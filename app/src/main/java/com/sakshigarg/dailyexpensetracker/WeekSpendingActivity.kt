package com.sakshigarg.dailyexpensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks

class WeekSpendingActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var totalWeekAmount: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var weekSpendingAdapter: WeekSpendingAdapter
    private lateinit var myDataList: MutableList<Data>
    private var type=""

    private lateinit var mAuth: FirebaseAuth
    private lateinit var rupee: String
    private  var onlineUserId =""
    private lateinit var expensesRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_spending)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Week spending"
        totalWeekAmount = findViewById(R.id.totalWeekTV)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        rupee = resources.getString(R.string.rupee)


        var linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth.currentUser!!.uid


        myDataList = arrayListOf()
        weekSpendingAdapter = WeekSpendingAdapter(this, myDataList as ArrayList<Data>)
        recyclerView.adapter = weekSpendingAdapter

        if(intent.extras!=null){
            type= intent.getStringExtra("type").toString()
            if(type.equals("week")){
                readWeekSpendingItems()
            }else if(type.equals("month")){
                readMonthSpendingItems()
            }
        }




    }

    private fun readMonthSpendingItems() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months = Months.monthsBetween(epoch,now)
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query:Query = expensesRef.orderByChild("month").equalTo(months.months.toDouble())

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                myDataList.clear()
                for (snap: DataSnapshot in snapshot.children){
                    var data: Data? = snap.getValue(Data::class.java)
                    myDataList.add(data!!)
                }

                weekSpendingAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

                var totalAmount =0
                for(ds:DataSnapshot in snapshot.children){
                    var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                    var total: Object? = map.get("amount")
                    var pTotal:Int = Integer.parseInt(total.toString())
                    totalAmount+=pTotal

                    totalWeekAmount.setText("Total Month's Spending: "+rupee+totalAmount)
                }




            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }

    private fun readWeekSpendingItems() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val weeks = Weeks.weeksBetween(epoch,now)
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query:Query = expensesRef.orderByChild("week").equalTo(weeks.weeks.toDouble())

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                myDataList.clear()
                for (snap: DataSnapshot in snapshot.children){
                    var data: Data? = snap.getValue(Data::class.java)
                    myDataList.add(data!!)
                }

                weekSpendingAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

                var totalAmount =0
                for(ds:DataSnapshot in snapshot.children){
                    var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                    var total: Object? = map.get("amount")
                    var pTotal:Int = Integer.parseInt(total.toString())
                    totalAmount+=pTotal

                    totalWeekAmount.setText("Total Week's Spending: "+rupee+totalAmount)
                }




            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }
}


