package com.sakshigarg.dailyexpensetracker

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.marginRight
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.PercentFormatter


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
    private lateinit var anyChartView: PieChart
    private lateinit var spent: TextView
    private lateinit var cld:ConnectionLiveData

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
        spent = findViewById(R.id.spent)
        supportActionBar!!.setTitle("Budget Spy")

        budgetTV = findViewById(R.id.budgetTv)
        monthSpendingTV = findViewById(R.id.monthTv)
        weekSpendingTV = findViewById(R.id.weekTv)
        todaySpendingTV = findViewById(R.id.todayTv)
        remainingBudgetTV = findViewById(R.id.savingsTv)
        weekCardView = findViewById(R.id.weekCardView)
        monthCardView = findViewById(R.id.monthCardView)
        analyticsCardView = findViewById(R.id.analyticsCardView)
        anyChartView = findViewById(R.id.anyChartView)

        mAuth = FirebaseAuth.getInstance()
        onlineUserID= FirebaseAuth.getInstance().currentUser!!.uid
        budgetRef=FirebaseDatabase.getInstance().getReference("budget").child(onlineUserID)
        expensesRef=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)
        personalRef=FirebaseDatabase.getInstance().getReference("personal").child(onlineUserID)

        budgetCardView = findViewById(R.id.budgetCardView)
        todayCardView = findViewById(R.id.todayCardView)
        historyCardView = findViewById(R.id.historyCardView)

        checkNetworkConnection();

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
        getTotalWeekOtherExpense()
        getTotalWeekTransportExpense()
        getTotalWeekFoodExpense()
        getTotalWeekHouseExpense()
        getTotalWeekEntertainmentExpense()
        getTotalWeekEducationExpense()
        getTotalWeekCharityExpense()
        getTotalWeekApparelExpense()
        getTotalWeekHealthExpense()
        getTotalWeekPersonalExpense()

        //loadGraph()
        setupPieChart()
        loadPieChartData()
    }

    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this, { isConnected ->
            if(isConnected){
                //Toast.makeText(this, "You are connected to internet", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Please check your connection", Toast.LENGTH_LONG).show()
            }
        })
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
                    spent.setText("You have spent  "+rupee+totalAmount+" this month")
                }
                personalRef.child("month").setValue(totalAmount)
                totalAmountMonth = totalAmount


                if(!snapshot.exists()){
                    anyChartView.visibility = View.GONE
                    spent.visibility = View.GONE
                    //spent.setText("You have not spent anything in this month")
                    return
                }
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

    private fun setupPieChart(){

        anyChartView.setDrawHoleEnabled(true)
        anyChartView.setUsePercentValues(true)
        anyChartView.setDrawEntryLabels(false)
        anyChartView.setEntryLabelTextSize(10F)
        anyChartView.setEntryLabelColor(Color.BLACK)
        anyChartView.setCenterText("Spending by Category")
        anyChartView.setCenterTextSize(22F)
        anyChartView.getDescription().setEnabled(false)
        anyChartView.setExtraOffsets(0F, 5F, 20F, 5F);
        anyChartView.setDragDecelerationFrictionCoef(0.95f);
        anyChartView.setRotationEnabled(true);
        anyChartView.setHighlightPerTapEnabled(true);

        val l: Legend = anyChartView.getLegend()
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        l.setOrientation(Legend.LegendOrientation.VERTICAL)
        l.setDrawInside(false)
        l.setEnabled(true)
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        l.textColor = Color.WHITE
    }

    private fun loadPieChartData(){
        personalRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    var traTotal:Int
                    if(snapshot.hasChild("monthTrans")){
                        traTotal= Integer.parseInt(snapshot.child("monthTrans").getValue().toString())
                    }else{
                        traTotal=0
                    }
                    var foodTotal:Int
                    if(snapshot.hasChild("monthFood")){
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString())
                    }else{
                        foodTotal=0
                    }
                    var houseTotal:Int
                    if(snapshot.hasChild("monthHouse")){
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString())
                    }else{
                        houseTotal=0
                    }
                    var entTotal:Int
                    if(snapshot.hasChild("monthEnt")){
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString())
                    }else{
                        entTotal=0
                    }
                    var eduTotal:Int
                    if(snapshot.hasChild("monthEdu")){
                        eduTotal = Integer.parseInt(snapshot.child("monthEdu").getValue().toString())
                    }else{
                        eduTotal=0
                    }

                    var appTotal:Int
                    if(snapshot.hasChild("monthApp")){
                        appTotal = Integer.parseInt(snapshot.child("monthApp").getValue().toString())
                    }else{
                        appTotal=0
                    }

                    var heaTotal:Int
                    if(snapshot.hasChild("monthHea")){
                        heaTotal = Integer.parseInt(snapshot.child("monthHea").getValue().toString())
                    }else{
                        heaTotal=0
                    }

                    var perTotal:Int
                    if(snapshot.hasChild("monthPer")){
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString())
                    }else{
                        perTotal=0
                    }

                    var chaTotal:Int
                    if(snapshot.hasChild("monthCha")){
                        chaTotal = Integer.parseInt(snapshot.child("monthCha").getValue().toString())
                    }else{
                        chaTotal=0
                    }

                    var othTotal:Int
                    if(snapshot.hasChild("monthOther")){
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString())
                    }else{
                        othTotal=0
                    }

                    //val pie: Pie = AnyChart.pie()
                    val entries:MutableList<PieEntry> = ArrayList<PieEntry>()

                    if(traTotal != 0){
                        entries.add(PieEntry(traTotal.toFloat(),"Transport"))
                    }
                    if(houseTotal !=0)
                        entries.add(PieEntry(houseTotal.toFloat(),"House"))
                    if(foodTotal!=0)
                        entries.add(PieEntry(foodTotal.toFloat(),"Food"))
                    if(entTotal!=0)
                        entries.add(PieEntry(entTotal.toFloat(),"Entertainment"))
                    if(eduTotal!=0)
                        entries.add(PieEntry(eduTotal.toFloat(),"Education"))

                    if(chaTotal!=0)
                        entries.add(PieEntry(chaTotal.toFloat(),"Charity"))

                    if(appTotal!=0)
                        entries.add(PieEntry(appTotal.toFloat(),"Apparel"))
                    if(heaTotal!=0)
                        entries.add(PieEntry(heaTotal.toFloat(),"Health"))
                    if(perTotal!=0)
                        entries.add(PieEntry(perTotal.toFloat(),"Personal"))
                    if(othTotal!=0)
                        entries.add(PieEntry(othTotal.toFloat(),"Other"))

                    val colors: ArrayList<Int> = ArrayList()

                    for (color in ColorTemplate.MATERIAL_COLORS) {
                        colors.add(color)
                    }

                    for (color in ColorTemplate.VORDIPLOM_COLORS) {
                        colors.add(color)
                    }

                    val dataSet = PieDataSet(entries, "Expense Category")
                    dataSet.colors = colors


                    val data = PieData(dataSet)
                    data.setDrawValues(true);
                    data.setValueFormatter(PercentFormatter(anyChartView));
                    data.setValueTextSize(12f);
                    data.setValueTextColor(Color.BLACK);

                    anyChartView.visibility = View.VISIBLE
                    spent.visibility = View.VISIBLE

                    getMonthSpentAmount()
                    anyChartView.data = data
                    anyChartView.invalidate()
                    anyChartView.animateY(1400, Easing.EaseInOutQuad);


                }

                else{
                    Toast.makeText(this@MainActivity,"Child does not exits",Toast.LENGTH_SHORT).show()
                }



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Child does not exist",Toast.LENGTH_SHORT).show()
            }
        }
        )
    }


    private fun getTotalWeekOtherExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months= Months.monthsBetween(epoch,now)
        val itemMonth = "Other"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsOtherAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthOther").setValue(totalAmount)
                }else{
                    //linearLayoutOther.visibility = View.GONE
                    personalRef.child("monthOther").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekPersonalExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()

        val months= Months.monthsBetween(epoch,now)
        var itemMonth = "Personal"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsPersonalAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthPer").setValue(totalAmount)
                }else{
                    //linearLayoutPersonal.visibility = View.GONE
                    personalRef.child("monthPer").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekHealthExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months= Months.monthsBetween(epoch,now)

        val itemMonths = "Health"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonths)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                       // analyticsHealthAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthHea").setValue(totalAmount)
                }else{
                    //linearLayoutHealth.visibility = View.GONE
                    personalRef.child("monthHea").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekApparelExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months= Months.monthsBetween(epoch,now)
        val itemMonth = "Apparel"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        val pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsApparelAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthApp").setValue(totalAmount)
                }else{
                   // linearLayoutApparel.visibility = View.GONE
                    personalRef.child("monthApp").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekCharityExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months= Months.monthsBetween(epoch,now)
        val itemMonth = "Charity"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        val pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsCharityAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthCha").setValue(totalAmount)
                }else{
                    //linearLayoutCharity.visibility = View.GONE
                    personalRef.child("monthCha").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekEducationExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months= Months.monthsBetween(epoch,now)

        val itemMonth = "Education"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsEducationAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthEdu").setValue(totalAmount)
                }else{
                    //linearLayoutEducation.visibility = View.GONE
                    personalRef.child("monthEdu").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekEntertainmentExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months= Months.monthsBetween(epoch,now)
        val itemMonth = "Entertainment"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        val pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsEntertainmentAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthEnt").setValue(totalAmount)
                }else{
                    //linearLayoutEntertainment.visibility = View.GONE
                    personalRef.child("monthEnt").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekHouseExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months= Months.monthsBetween(epoch,now)
        val itemMonth = "House"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        val pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsHouseExpensesAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthHouse").setValue(totalAmount)
                }else{
                    //linearLayoutHouse.visibility = View.GONE
                    personalRef.child("monthHouse").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekFoodExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()

        val months= Months.monthsBetween(epoch,now)
        val itemMonth = "Food"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsFoodAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthFood").setValue(totalAmount)
                }else{
                    //linearLayoutFood.visibility = View.GONE
                    personalRef.child("monthFood").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun getTotalWeekTransportExpense() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()

        val months= Months.monthsBetween(epoch,now)
        val itemMonth = "Transport"+months.months
        val reference:DatabaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID)

        val query: Query = reference.orderByChild("itemMonth").equalTo(itemMonth)

        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalAmount=0
                    for(ds: DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        var pTotal:Int = Integer.parseInt(total.toString())
                        totalAmount+=pTotal

                        //analyticsTransportAmountTV.setText("Spent: "+rupee+totalAmount)
                    }
                    personalRef.child("monthTrans").setValue(totalAmount)
                }else{
                    //linearLayoutTransport.visibility = View.GONE
                    personalRef.child("monthTrans").setValue(0)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
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