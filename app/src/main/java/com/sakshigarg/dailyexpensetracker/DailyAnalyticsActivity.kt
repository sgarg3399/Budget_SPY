package com.sakshigarg.dailyexpensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import org.w3c.dom.Text
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask

class DailyAnalyticsActivity : AppCompatActivity() {
    private lateinit var settingsToolbar:androidx.appcompat.widget.Toolbar
    private lateinit var mAuth:FirebaseAuth
    private var onlineUserId = ""
    private lateinit var expensesRef:DatabaseReference
    private lateinit var personalRef:DatabaseReference
    private lateinit var rupee: String

    private  lateinit var totalBudgetAmountTV: TextView
    private lateinit var analyticsTransportAmountTV:TextView
    private lateinit var analyticsFoodAmountTV:TextView
    private lateinit var analyticsHouseExpensesAmountTV:TextView
    private lateinit var analyticsEntertainmentAmountTV:TextView
    private lateinit var analyticsEducationAmountTV:TextView
    private lateinit var analyticsCharityAmountTV:TextView
    private lateinit var analyticsApparelAmountTV:TextView
    private lateinit var analyticsHealthAmountTV:TextView
    private lateinit var analyticsPersonalAmountTV:TextView
    private lateinit var analyticsOtherAmountTV:TextView

    private lateinit var linearLayoutFood:RelativeLayout
    private lateinit var linearLayoutTransport:RelativeLayout
    private lateinit var linearLayoutHouse:RelativeLayout
    private lateinit var linearLayoutEntertainment:RelativeLayout
    private lateinit var linearLayoutEducation:RelativeLayout
    private lateinit var linearLayoutCharity:RelativeLayout
    private lateinit var linearLayoutApparel:RelativeLayout
    private lateinit var linearLayoutHealth:RelativeLayout
    private lateinit var linearLayoutPersonal:RelativeLayout
    private lateinit var linearLayoutOther:RelativeLayout

    private lateinit var anyChartView:AnyChartView

    private lateinit var transportStatus:ImageView
    private lateinit var foodStatus:ImageView
    private lateinit var houseStatus:ImageView
    private lateinit var entertainmentStatus:ImageView
    private lateinit var educationStatus:ImageView
    private lateinit var charityStatus:ImageView
    private lateinit var apparelStatus:ImageView
    private lateinit var healthStatus:ImageView
    private lateinit var personalStatus:ImageView
    private lateinit var otherStatus:ImageView

    private lateinit var progress_ratio_transport:TextView
    private lateinit var progress_ratio_food:TextView
    private lateinit var progress_ratio_house:TextView
    private lateinit var progress_ratio_entertainment:TextView
    private lateinit var progress_ratio_education:TextView
    private lateinit var progress_ratio_charity:TextView
    private lateinit var progress_ratio_apparel:TextView
    private lateinit var progress_ratio_health:TextView
    private lateinit var progress_ratio_personal:TextView
    private lateinit var progress_ratio_others:TextView

    private lateinit var linearLayoutAnalysis:RelativeLayout
    private lateinit var monthSpendAmount: TextView
    private lateinit var monthRatioSpending:TextView
    private lateinit var monthRatioSpending_Image :ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_analytics)

        settingsToolbar = findViewById(R.id.my_Food_Toolbar)
        setSupportActionBar(settingsToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setTitle("Today Analytics")
        settingsToolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })

        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth.currentUser!!.uid
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)
        personalRef =  FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId)

        totalBudgetAmountTV = findViewById(R.id.totalBudgetTV)
        rupee = resources.getString(R.string.rupee)

        //general analytics
        monthSpendAmount= findViewById(R.id.monthSpendAmount)
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis)
        monthRatioSpending = findViewById(R.id.monthRatioSpending)
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_image)

        analyticsTransportAmountTV = findViewById(R.id.analyticsTransportAmount)
        analyticsFoodAmountTV = findViewById(R.id.analyticsFoodAmount)
        analyticsHouseExpensesAmountTV = findViewById(R.id.analyticsHouseAmount)
        analyticsEntertainmentAmountTV = findViewById(R.id.analyticsEntertainmentAmount)
        analyticsEducationAmountTV = findViewById(R.id.analyticsEducationAmount)
        analyticsCharityAmountTV = findViewById(R.id.analyticsCharityAmount)
        analyticsApparelAmountTV = findViewById(R.id.analyticsApparelAmount)
        analyticsHealthAmountTV = findViewById(R.id.analyticsHealthAmount)
        analyticsPersonalAmountTV = findViewById(R.id.analyticsPersonalAmount)
        analyticsOtherAmountTV = findViewById(R.id.analyticsOtherAmount)

        //RecyclerViews
        linearLayoutTransport = findViewById(R.id.relativeLayoutTransport)
        linearLayoutFood = findViewById(R.id.relativeLayoutFood)
        linearLayoutHealth = findViewById(R.id.relativeLayoutHealth)
        linearLayoutApparel = findViewById(R.id.relativeLayoutApparel)
        linearLayoutCharity = findViewById(R.id.relativeLayoutCharity)
        linearLayoutEducation = findViewById(R.id.relativeLayoutEducation)
        linearLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment)
        linearLayoutHouse = findViewById(R.id.relativeLayoutHouse)
        linearLayoutPersonal = findViewById(R.id.relativeLayoutPersonal)
        linearLayoutOther = findViewById(R.id.relativeLayoutOther)

        //textviews
        progress_ratio_apparel = findViewById(R.id.progress_ratio_apparel)
        progress_ratio_charity = findViewById(R.id.progress_ratio_charity)
        progress_ratio_education = findViewById(R.id.progress_ratio_education)
        progress_ratio_entertainment = findViewById(R.id.progress_ratio_entertainment)
        progress_ratio_food = findViewById(R.id.progress_ratio_food)
        progress_ratio_health = findViewById(R.id.progress_ratio_health)
        progress_ratio_house = findViewById(R.id.progress_ratio_house)
        progress_ratio_others = findViewById(R.id.progress_ratio_other)
        progress_ratio_personal = findViewById(R.id.progress_ratio_personal)
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport)

        //imageviews
        apparelStatus = findViewById(R.id.Apparel_status)
        charityStatus = findViewById(R.id.Charity_status)
        educationStatus = findViewById(R.id.Education_status)
        entertainmentStatus = findViewById(R.id.Entertainment_status)
        foodStatus = findViewById(R.id.Food_status)
        healthStatus = findViewById(R.id.Health_status)
        houseStatus = findViewById(R.id.house_status)
        otherStatus = findViewById(R.id.Other_status)
        personalStatus = findViewById(R.id.Personal_status)
        transportStatus = findViewById(R.id.transport_status)

        anyChartView = findViewById(R.id.anyChartView)

        getTotalDayTransportExpense()
        getTotalDayFoodExpense()
        getTotalDayHouseExpense()
        getTotalDayEntertainmentExpense()
        getTotalDayEducationExpense()
        getTotalDayCharityExpense()
        getTotalDayApparelExpense()
        getTotalDayHealthExpense()
        getTotalDayPersonalExpense()
        getTotalDayOtherExpense()

        getTotalDaySpending()
//        loadGraph()
//        setStatusAndImageResource()

        Timer().schedule(timerTask {
            loadGraph()
            setStatusAndImageResource()
        }, 2000)


    }

    private fun getTotalDaySpending() {
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)

        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("date").equalTo(date)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.childrenCount>0){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal
                    }
                    totalBudgetAmountTV.setText("Total day's spending: "+rupee+totalAmount)
                    monthSpendAmount.setText("Total Spent: "+rupee+totalAmount)
                }else{
                    totalBudgetAmountTV.setText("You have not spent today")
                    anyChartView.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )

    }

    private fun getTotalDayOtherExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Other"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsOtherAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayOther").setValue(totalAmount)
                }else{
                    linearLayoutOther.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayPersonalExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Personal"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsPersonalAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayPer").setValue(totalAmount)
                }else{
                    linearLayoutPersonal.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayHealthExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Health"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsHealthAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayHea").setValue(totalAmount)
                }else{
                    linearLayoutHealth.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayApparelExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Apparel"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsApparelAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayApp").setValue(totalAmount)
                }else{
                    linearLayoutApparel.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayCharityExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Charity"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsCharityAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayCha").setValue(totalAmount)
                }else{
                    linearLayoutCharity.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayEducationExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Education"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsEducationAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayEdu").setValue(totalAmount)
                }else{
                    linearLayoutEducation.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayEntertainmentExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Entertainment"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsEntertainmentAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayEnt").setValue(totalAmount)
                }else{
                    linearLayoutEntertainment.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayHouseExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "House"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsHouseExpensesAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayHouse").setValue(totalAmount)
                }else{
                    linearLayoutHouse.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayFoodExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Food"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsFoodAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayFood").setValue(totalAmount)
                }else{
                    linearLayoutFood.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalDayTransportExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
        var cal: Calendar = Calendar.getInstance()
        var date: String = dateFormat.format(cal.time)
        var itemNday = "Transport"+date
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId)

        val query: Query = reference.orderByChild("itemDay").equalTo(itemNday)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        analyticsTransportAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("dayTrans").setValue(totalAmount)
                }else{
                    linearLayoutTransport.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun loadGraph(){
        personalRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    var traTotal:Int
                    if(snapshot.hasChild("dayTrans")){
                        traTotal= Integer.parseInt(snapshot.child("dayTrans").getValue().toString())
                    }else{
                        traTotal=0
                    }
                    var foodTotal:Int
                    if(snapshot.hasChild("dayFood")){
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString())
                    }else{
                        foodTotal=0
                    }
                    var houseTotal:Int
                    if(snapshot.hasChild("dayHouse")){
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString())
                    }else{
                        houseTotal=0
                    }
                    var entTotal:Int
                    if(snapshot.hasChild("dayEnt")){
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString())
                    }else{
                        entTotal=0
                    }
                    var eduTotal:Int
                    if(snapshot.hasChild("dayEdu")){
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString())
                    }else{
                        eduTotal=0
                    }

                    var appTotal:Int
                    if(snapshot.hasChild("dayApp")){
                        appTotal = Integer.parseInt(snapshot.child("dayApp").getValue().toString())
                    }else{
                        appTotal=0
                    }

                    var heaTotal:Int
                    if(snapshot.hasChild("dayHea")){
                        heaTotal = Integer.parseInt(snapshot.child("dayHea").getValue().toString())
                    }else{
                        heaTotal=0
                    }

                    var perTotal:Int
                    if(snapshot.hasChild("dayPer")){
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString())
                    }else{
                        perTotal=0
                    }

                    var chaTotal:Int
                    if(snapshot.hasChild("dayCha")){
                        chaTotal = Integer.parseInt(snapshot.child("dayCha").getValue().toString())
                    }else{
                        chaTotal=0
                    }

                    var othTotal:Int
                    if(snapshot.hasChild("dayOther")){
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString())
                    }else{
                        othTotal=0
                    }

                    val pie:Pie = AnyChart.pie()
                    val data:MutableList<DataEntry> = ArrayList<DataEntry>()
                    data.add(ValueDataEntry("Transport",traTotal))
                    data.add(ValueDataEntry("House",houseTotal))
                    data.add(ValueDataEntry("Food",foodTotal))
                    data.add(ValueDataEntry("Entertainment",entTotal))
                    data.add(ValueDataEntry("Education",eduTotal))
                    data.add(ValueDataEntry("Charity",chaTotal))
                    data.add(ValueDataEntry("Apparel",appTotal))
                    data.add(ValueDataEntry("Health",heaTotal))
                    data.add(ValueDataEntry("Personal",perTotal))
                    data.add(ValueDataEntry("Other",othTotal))

                    pie.data(data)
                    pie.title("Daily Analytics")
                    pie.labels().position("outside")
                    pie.legend().title().enabled(true)
                    pie.legend().title().text("Items spend on") .padding(0,0,10,0)
                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER)
                    anyChartView.setChart(pie)

                }

                else{
                    Toast.makeText(this@DailyAnalyticsActivity,"Child does not exits",Toast.LENGTH_SHORT).show()
                }



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Child does not exist",Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun setStatusAndImageResource(){
        personalRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    var traTotal:Float
                    if(snapshot.hasChild("dayTrans")){
                        traTotal=
                            Integer.parseInt(snapshot.child("dayTrans").getValue().toString()).toFloat()
                    }else{
                        traTotal= 0F
                    }
                    var foodTotal:Float
                    if(snapshot.hasChild("dayFood")){
                        foodTotal =
                            Integer.parseInt(snapshot.child("dayFood").getValue().toString())
                                .toFloat()
                    }else{
                        foodTotal=0F
                    }
                    var houseTotal:Float
                    if(snapshot.hasChild("dayHouse")){
                        houseTotal =
                            Integer.parseInt(snapshot.child("dayHouse").getValue().toString())
                                .toFloat()
                    }else{
                        houseTotal=0F
                    }
                    var entTotal:Float
                    if(snapshot.hasChild("dayEnt")){
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString())
                            .toFloat()
                    }else{
                        entTotal=0F
                    }
                    var eduTotal:Float
                    if(snapshot.hasChild("dayEdu")){
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString())
                            .toFloat()
                    }else{
                        eduTotal=0F
                    }

                    var appTotal:Float
                    if(snapshot.hasChild("dayApp")){
                        appTotal =  Integer.parseInt(snapshot.child("dayApp").getValue().toString())
                            .toFloat()
                    }else{
                        appTotal=0F
                    }

                    var heaTotal:Float
                    if(snapshot.hasChild("dayHea")){
                        heaTotal = Integer.parseInt(snapshot.child("dayHea").getValue().toString())
                            .toFloat()
                    }else{
                        heaTotal=0F
                    }

                    var perTotal:Float
                    if(snapshot.hasChild("dayPer")){
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString())
                            .toFloat()
                    }else{
                        perTotal=0F
                    }

                    var chaTotal:Float
                    if(snapshot.hasChild("dayCha")){
                        chaTotal =Integer.parseInt(snapshot.child("dayCha").getValue().toString())
                            .toFloat()
                    }else{
                        chaTotal=0F
                    }

                    var othTotal:Float
                    if(snapshot.hasChild("dayOther")){
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString())
                            .toFloat()
                    }else{
                        othTotal=0F
                    }

                   var monthTotalSpentAmount:Float

                   if(snapshot.hasChild("today")){
                       monthTotalSpentAmount = Integer.parseInt(snapshot.child("today").getValue().toString())
                           .toFloat()
                   }else{
                       monthTotalSpentAmount=0F
                   }

                    //Getting ratios

                    val traRatio:Float
                    if(snapshot.hasChild("dayTransRatio")){
                        traRatio=
                            Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString()).toFloat()
                    }else{
                        traRatio= 0F
                    }

                    val foodRatio:Float
                    if(snapshot.hasChild("dayFoodRatio")){
                        foodRatio = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString()).toFloat()
                    }else{
                        foodRatio=0F
                    }

                    val houseRatio:Float
                    if(snapshot.hasChild("dayHouseRatio")){
                        houseRatio = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString())
                            .toFloat()
                    }else{
                        houseRatio=0F
                    }

                    val entRatio:Float
                    if(snapshot.hasChild("dayEntRatio")){
                        entRatio = Integer.parseInt(snapshot.child("dayEntRatio").getValue().toString())
                            .toFloat()
                    }else{
                        entRatio=0F
                    }
                    val eduRatio:Float
                    if(snapshot.hasChild("dayEduRatio")){
                        eduRatio = Integer.parseInt(snapshot.child("dayEduRatio").getValue().toString())
                            .toFloat()
                    }else{
                        eduRatio=0F
                    }

                    val appRatio:Float
                    if(snapshot.hasChild("dayAppRatio")){
                        appRatio = Integer.parseInt(snapshot.child("dayAppRatio").getValue().toString())
                            .toFloat()
                    }else{
                        appRatio=0F
                    }

                    val heaRatio:Float
                    if(snapshot.hasChild("dayHealthRatio")){
                        heaRatio = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString())
                            .toFloat()
                    }else{
                        heaRatio=0F
                    }

                    val perRatio:Float
                    if(snapshot.hasChild("dayPerRatio")){
                        perRatio = Integer.parseInt(snapshot.child("dayPerRatio").getValue().toString())
                            .toFloat()
                    }else{
                        perRatio=0F
                    }

                    val chaRatio:Float
                    if(snapshot.hasChild("dayCharRatio")){
                        chaRatio = Integer.parseInt(snapshot.child("dayCharRatio").getValue().toString())
                            .toFloat()
                    }else{
                        chaRatio=0F
                    }

                    val othRatio:Float
                    if(snapshot.hasChild("dayOtherRatio")){
                        othRatio = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString())
                            .toFloat()
                    }else{
                        othRatio=0F
                    }

                    var monthTotalSpentAmountRatio:Int
                    if(snapshot.hasChild("dailyBudget")){
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("dailyBudget").getValue().toString())
                    }else{
                        monthTotalSpentAmountRatio =0
                    }

                    val monthPercent = (monthTotalSpentAmount/monthTotalSpentAmountRatio)*100

                    if(monthPercent<50){
                        monthRatioSpending.setText(""+monthPercent+"%" +  " used of"+rupee+monthTotalSpentAmountRatio+". Status:")
                        monthRatioSpending_Image.setImageResource(R.drawable.green)
                    }else if(monthPercent>=50 && monthPercent<100){
                        monthRatioSpending.setText(""+monthPercent+"%" +  " used of"+rupee+monthTotalSpentAmountRatio+". Status:")
                        monthRatioSpending_Image.setImageResource(R.drawable.brown)
                    }else{
                        monthRatioSpending.setText(""+monthPercent+"%" +  " used of"+rupee+monthTotalSpentAmountRatio+". Status:")
                        monthRatioSpending_Image.setImageResource(R.drawable.red)
                    }

                    val transportPercent = (traTotal/traRatio)*100

                    if(transportPercent<50){
                        progress_ratio_transport.setText(""+transportPercent+"% " +"used of "+traRatio+". Status:")
                        transportStatus.setImageResource(R.drawable.green)
                    }else if(transportPercent>=50 && transportPercent<100){
                        progress_ratio_transport.setText(""+transportPercent+"% "+"used of "+traRatio+". Status:")
                        transportStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_transport.setText(""+transportPercent+"% "+"used of "+traRatio+". Status:")
                        transportStatus.setImageResource(R.drawable.red)
                    }

                    val foodPercent = (foodTotal/foodRatio)*100

                    when {
                        foodPercent<50 -> {
                            progress_ratio_food.setText(""+foodPercent+"% "+"used of "+foodRatio+". Status:")
                            foodStatus.setImageResource(R.drawable.green)
                        }
                        50 <= foodPercent && foodPercent <= 99 -> {
                            progress_ratio_food.setText(""+foodPercent+"% "+"used of "+foodRatio+". Status:")
                            foodStatus.setImageResource(R.drawable.brown)
                        }
                        else -> {
                            progress_ratio_food.setText(""+foodPercent+"% "+"used of "+foodRatio+". Status:")
                            foodStatus.setImageResource(R.drawable.red)
                        }
                    }

                    val housePercent = (houseTotal/houseRatio)*100

                    if(housePercent<50){
                        progress_ratio_house.setText(""+housePercent+"% "+"used of "+houseRatio+". Status:")
                        houseStatus.setImageResource(R.drawable.green)
                    }else if(50 <= housePercent && housePercent <= 99){
                        progress_ratio_house.setText(""+housePercent+"% "+"used of "+houseRatio+". Status:")
                        houseStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_house.setText(""+housePercent+"% "+"used of "+houseRatio+". Status:")
                        houseStatus.setImageResource(R.drawable.red)
                    }

                    val entPercent = (entTotal/entRatio)*100

                    if(entPercent<50){
                        progress_ratio_entertainment.setText(""+entPercent+"% "+"used of "+entRatio+". Status:")
                        entertainmentStatus.setImageResource(R.drawable.green)
                    }else if(50 <= entPercent && entPercent <= 99){
                        progress_ratio_entertainment.setText(""+entPercent+"% "+"used of "+entRatio+". Status:")
                        entertainmentStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_entertainment.setText(""+entPercent+"% "+"used of "+entRatio+". Status:")
                        entertainmentStatus.setImageResource(R.drawable.red)
                    }

                    val eduPercent = (eduTotal/eduRatio)*100

                    if(eduPercent<50){
                        progress_ratio_education.setText(""+eduPercent+"% "+"used of "+eduRatio+". Status:")
                        educationStatus.setImageResource(R.drawable.green)
                    }else if(50 <= eduPercent && eduPercent <= 99){
                        progress_ratio_education.setText(""+eduPercent+"% "+"used of "+eduRatio+". Status:")
                        educationStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_education.setText(""+eduPercent+"% "+"used of "+eduRatio+". Status:")
                        educationStatus.setImageResource(R.drawable.red)
                    }

                    val chaPercent = (chaTotal/chaRatio)*100

                    if(chaPercent<50){
                        progress_ratio_charity.setText(""+chaPercent+"% "+"used of "+chaRatio+". Status:")
                        charityStatus.setImageResource(R.drawable.green)
                    }else if(50 <= chaPercent && chaPercent <= 99){
                        progress_ratio_charity.setText(""+chaPercent+"% "+"used of "+chaRatio+". Status:")
                        charityStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_charity.setText(""+chaPercent+"% "+"used of "+chaRatio+". Status:")
                        charityStatus.setImageResource(R.drawable.red)
                    }

                    val appPercent = (appTotal/appRatio)*100

                    if(appPercent<50){
                        progress_ratio_apparel.setText(""+appPercent+"% "+"used of "+appRatio+". Status:")
                        apparelStatus.setImageResource(R.drawable.green)
                    }else if(50 <= appPercent && appPercent <= 99){
                        progress_ratio_apparel.setText(""+appPercent+"% "+"used of "+appRatio+". Status:")
                        apparelStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_apparel.setText(""+appPercent+"% "+"used of "+appRatio+". Status:")
                        apparelStatus.setImageResource(R.drawable.red)
                    }

                    val heaPercent = (heaTotal/heaRatio)*100

                    if(heaPercent<50){
                        progress_ratio_health.setText(""+heaPercent+"% "+"used of "+heaRatio+". Status:")
                        healthStatus.setImageResource(R.drawable.green)
                    }else if(50 <= heaPercent && heaPercent <= 99){
                        progress_ratio_health.setText(""+heaPercent+"% "+"used of "+heaRatio+". Status:")
                        healthStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_health.setText(""+heaPercent+"% "+"used of "+heaRatio+". Status:")
                        healthStatus.setImageResource(R.drawable.red)
                    }

                    val perPercent = (perTotal/perRatio)*100

                    if(perPercent<50){
                        progress_ratio_personal.setText(""+perPercent+"% "+"used of "+perRatio+". Status:")
                        personalStatus.setImageResource(R.drawable.green)
                    }else if(50 <= perPercent && perPercent <= 99){
                        progress_ratio_personal.setText(""+perPercent+"% "+"used of "+perRatio+". Status:")
                        personalStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_personal.setText(""+perPercent+"% "+"used of "+perRatio+". Status:")
                        personalStatus.setImageResource(R.drawable.red)
                    }

                    val otherPercent = (othTotal/othTotal)*100

                    if(otherPercent<50){
                        progress_ratio_others.setText(""+otherPercent+"% "+"used of "+othRatio+". Status:")
                        otherStatus.setImageResource(R.drawable.green)
                    }else if(50 <= otherPercent && otherPercent <= 99){
                        progress_ratio_others.setText(""+otherPercent+"% "+"used of "+othRatio+". Status:")
                        otherStatus.setImageResource(R.drawable.brown)
                    }else{
                        progress_ratio_others.setText(""+otherPercent+"% "+"used of "+othRatio+". Status:")
                        otherStatus.setImageResource(R.drawable.red)
                    }





                }

                else{
                    Toast.makeText(this@DailyAnalyticsActivity,"Child does not exits",Toast.LENGTH_SHORT).show()
                }



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Child does not exist",Toast.LENGTH_SHORT).show()
            }
        }
        )
    }
}