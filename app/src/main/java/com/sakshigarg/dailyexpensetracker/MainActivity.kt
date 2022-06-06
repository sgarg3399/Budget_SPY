package com.sakshigarg.dailyexpensetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var weekBtn : ImageView
    lateinit var todayBtn : ImageView
    lateinit var budgetBtn : ImageView
    lateinit var monthBtn:ImageView
    lateinit var analyticsBtn : ImageView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var weekSpendingTV:TextView
    private lateinit var budgetTV:TextView
    private lateinit var todaySpendingTV:TextView
    private lateinit var monthSpendingTV:TextView
    private lateinit var remainingBudgetTV:TextView

    private  var totalAmountMonth=0
    private  var totalAmountBudget=0
    private  var totalAmountBudgetB=0
    private  var totalAmountBudgetC=0

    private lateinit var mAuth:FirebaseAuth
    private lateinit var budgetRef:DatabaseReference
    private lateinit var expensesRef:DatabaseReference
    private lateinit var personalRef:DatabaseReference
    private  var onlineUserID=""

     lateinit var budgetCardView: CardView
     lateinit var todayCardView: CardView
     lateinit var historyCardView: CardView
    lateinit var weekCardView: CardView
    lateinit var monthCardView: CardView
    lateinit var analyticsCardView: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weekBtn = findViewById(R.id.weekIv)
        todayBtn = findViewById(R.id.todayIv)
        budgetBtn = findViewById(R.id.BudgetIv)
        monthBtn = findViewById(R.id.monthIv)
        analyticsBtn = findViewById(R.id.analyticsIv)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Budget Spy")

        budgetTV = findViewById(R.id.budgetTv)
        monthSpendingTV = findViewById(R.id.monthTv)
        weekSpendingTV = findViewById(R.id.weekTv)
        todaySpendingTV = findViewById(R.id.todayTv)
        remainingBudgetTV = findViewById(R.id.savingsTv)
        weekCardView = findViewById(R.id.weekCardView)
        monthCardView = findViewById(R.id.monthCardView)
        analyticsCardView = findViewById(R.id.analyticsCardView)

        mAuth = FirebaseAuth.getInstance()
        onlineUserID= FirebaseAuth.getInstance().currentUser!!.uid
        budgetRef=FirebaseDatabase.getInstance().getReference("budget").child(onlineUserID)
        expensesRef=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)
        personalRef=FirebaseDatabase.getInstance().getReference("personal").child(onlineUserID)






        budgetCardView = findViewById(R.id.budgetCardView)
        todayCardView = findViewById(R.id.todayCardView)
        historyCardView = findViewById(R.id.historyCardView)

        historyCardView.setOnClickListener {
            startActivity(Intent(this,HistoryActivity::class.java) )
        }
        budgetBtn.setOnClickListener {
            startActivity(Intent(this,BudgetActivity::class.java) )
        }
        todayBtn.setOnClickListener {
            startActivity(Intent(this,TodaySpendingActivity::class.java))
        }

        weekBtn.setOnClickListener {
            var intent:Intent = Intent(this,WeekSpendingActivity::class.java)
            intent.putExtra("type","week")
            startActivity(intent)
        }

        weekCardView.setOnClickListener {
            var intent:Intent = Intent(this,WeekSpendingActivity::class.java)
            intent.putExtra("type","week")
            startActivity(intent)
        }

        monthBtn.setOnClickListener {
            var intent = Intent(this,WeekSpendingActivity::class.java)
            intent.putExtra("type","month")
            startActivity(intent)
        }
        monthCardView.setOnClickListener {
            var intent = Intent(this,WeekSpendingActivity::class.java)
            intent.putExtra("type","month")
            startActivity(intent)
        }

        analyticsBtn.setOnClickListener {
            var intent = Intent(this,ChooseAnalyticActivity::class.java)
            startActivity(intent)
        }

        analyticsCardView.setOnClickListener {
            var intent = Intent(this,ChooseAnalyticActivity::class.java)
            startActivity(intent)
        }

        todayCardView.setOnClickListener {
            var intent = Intent(this,TodaySpendingActivity::class.java)
            startActivity(intent)
        }
        budgetCardView.setOnClickListener {
            var intent = Intent(this,BudgetActivity::class.java)
            startActivity(intent)
        }

        budgetRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.childrenCount>0){
                    for( ds:DataSnapshot in snapshot.children ){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmountBudgetB+=pTotal

                    }
                    totalAmountBudgetC= totalAmountBudgetB
                    personalRef.child("budget").setValue(totalAmountBudgetC)
                }else{
                    personalRef.child("budget").setValue(0)
                    Toast.makeText(this@MainActivity,"Please set a BUDGET",Toast.LENGTH_LONG).show()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )

        getBudgetAmount()
        getTodaySpentAmount()
        getWeekSpentAmount()
        getMonthSpentAmount()
        getSavings()
    }

    private fun getSavings() {

        personalRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    var budget:Int
                    if(snapshot.hasChild("budget")){
                        budget= Integer.parseInt(snapshot.child("budget").getValue().toString())
                    }else{
                        budget=0
                    }
                    var monthSpending:Int
                    if(snapshot.hasChild("month")){
                        monthSpending = Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()))
                    }else{
                        monthSpending=0
                    }

                    var savings = budget-monthSpending
                    val rupee = resources.getString(R.string.rupee)
                    remainingBudgetTV.setText(rupee+savings)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getWeekSpentAmount() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val weeks = Weeks.weeksBetween(epoch,now)
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query:Query = reference.orderByChild("week").equalTo(weeks.weeks.toDouble())

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var totalAmount =0
                for(ds:DataSnapshot in snapshot.children){
                    var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                    var total: Object? = map.get("amount")
                    var pTotal:Int = Integer.parseInt(total.toString())
                    totalAmount+=pTotal

                    val rupee = resources.getString(R.string.rupee)
                    weekSpendingTV.setText(rupee+totalAmount)
                }
                personalRef.child("week").setValue(totalAmount)


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }

    private fun getMonthSpentAmount() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months = Months.monthsBetween(epoch,now)
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query:Query = reference.orderByChild("month").equalTo(months.months.toDouble())

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var totalAmount =0
                for(ds:DataSnapshot in snapshot.children){
                    var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                    var total: Object? = map.get("amount")
                    var pTotal:Int = Integer.parseInt(total.toString())
                    totalAmount+=pTotal

                    val rupee = resources.getString(R.string.rupee)
                    monthSpendingTV.setText(rupee+totalAmount)
                }
                personalRef.child("month").setValue(totalAmount)
                totalAmountMonth = totalAmount




            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }

    private fun getTodaySpentAmount() {
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)
        var query:Query = reference.orderByChild("date").equalTo(date)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAmount =0


                for(ds:DataSnapshot in snapshot.children){
                    var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                    var total: Object? = map.get("amount")
                    var pTotal:Int = Integer.parseInt(total.toString())
                    totalAmount+=pTotal
                    val rupee = resources.getString(R.string.rupee)
                    todaySpendingTV.setText(rupee+totalAmount)
                }
                personalRef.child("today").setValue(totalAmount)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getBudgetAmount() {
        budgetRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rupee = resources.getString(R.string.rupee)
                var totalAmount =0;
                if(snapshot.exists() && snapshot.childrenCount>0){
                    for( ds:DataSnapshot in snapshot.children ){

//                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
//                        val total: Object? = map.get("amount")
//                        val pTotal:Int = Integer.parseInt(total.toString())
//                        totalAmountBudget+=pTotal
                        val data: Data? = ds.getValue(Data::class.java)
                        totalAmount+= data?.amount!!


                        budgetTV.setText(rupee+totalAmount.toString())

                    }

                }else{
                    totalAmountBudget=0
                    budgetTV.setText(rupee+0.toString())
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater= menuInflater
         inflater.inflate(R.menu.menu,menu)
        return  true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.account){
            startActivity(Intent(this, AccountActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}