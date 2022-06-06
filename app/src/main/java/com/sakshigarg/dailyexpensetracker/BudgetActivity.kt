package com.sakshigarg.dailyexpensetracker

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
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

class BudgetActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton

    private lateinit var budgetRef: DatabaseReference
    private lateinit var personalRef: DatabaseReference
    private lateinit var mauth: FirebaseAuth
    private lateinit var loader: ProgressDialog
    private lateinit var toolbars: Toolbar
    private lateinit var totalBudgetTV: TextView
    private lateinit var recyclerView: RecyclerView

    private var post_key:String =""
    private var item:String=""
    private var amount:Int =0
    private lateinit var rupee: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        toolbars = findViewById(R.id.toolbar)
        setSupportActionBar(toolbars)
        supportActionBar?.title = "Monthly Budget"

        mauth = FirebaseAuth.getInstance()
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget")
            .child(mauth.currentUser!!.uid)

        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(mauth.currentUser!!.uid)
        loader = ProgressDialog(this)

        totalBudgetTV = findViewById(R.id.totalBudgetTV)
        recyclerView = findViewById(R.id.recyclerView)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager
        rupee = resources.getString(R.string.rupee)

        budgetRef.addValueEventListener( object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAmount =0;

                for (snap: DataSnapshot in snapshot.children){
                    val data: Data? = snap.getValue(Data::class.java)
                    totalAmount+= data?.amount!!

                    totalBudgetTV.text = "Month budget: "+rupee+totalAmount
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        )


        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            additem()
        }

        budgetRef.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.childrenCount>0){
                    var totalAmount=0
                    for( ds:DataSnapshot in snapshot.children ){


                        var data: Data? = ds.getValue(Data::class.java)
                        totalAmount+= data!!.amount!!

                        var stotal:String = ("Month Budget: "+rupee+totalAmount)
                        totalBudgetTV.setText(stotal)

                    }
                    var weeklyBudget = totalAmount/4
                    var dailyBudget = totalAmount/30

                    personalRef.child("budget").setValue(totalAmount)
                    personalRef.child("weeklyBudget").setValue(weeklyBudget)
                    personalRef.child("dailyBudget").setValue(dailyBudget)
                }else{
                    personalRef.child("budget").setValue(0)
                    personalRef.child("weeklyBudget").setValue(0)
                    personalRef.child("dailyBudget").setValue(0)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )

        getMonthTransportBudgetRations()
        getMonthFoodBudgetRations()
        getMonthHouseBudgetRations()
        getMonthEntertainmentBudgetRations()
        getMonthEducationBudgetRations()
        getMonthCharityBudgetRations()
        getMonthApparelBudgetRations()
        getMonthHealthBudgetRations()
        getMonthPersonalBudgetRations()
        getMonthOtherBudgetRations()

    }





    private fun getMonthOtherBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Other")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    val dayOtherRatio = pTotal/30
                    val weekOtherRatio = pTotal/4
                    val monthOtherRatio = pTotal

                    personalRef.child("dayOtherRatio").setValue(dayOtherRatio)
                    personalRef.child("weekOtherRatio").setValue(weekOtherRatio)
                    personalRef.child("monthOtherRatio").setValue(monthOtherRatio)

                }else{
                    personalRef.child("dayOtherRatio").setValue(0)
                    personalRef.child("weekOtherRatio").setValue(0)
                    personalRef.child("monthOtherRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthPersonalBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Personal")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    val dayPerRatio = pTotal/30
                    val weekPerRatio = pTotal/4
                    val monthPerRatio = pTotal

                    personalRef.child("dayPerRatio").setValue(dayPerRatio)
                    personalRef.child("weekPerRatio").setValue(weekPerRatio)
                    personalRef.child("monthPerRatio").setValue(monthPerRatio)

                }else{
                    personalRef.child("dayPerRatio").setValue(0)
                    personalRef.child("weekPerRatio").setValue(0)
                    personalRef.child("monthPerRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthHealthBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Health")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    val dayHealthRatio = pTotal/30
                    val weekHealthRatio = pTotal/4
                    val monthHealthRatio = pTotal

                    personalRef.child("dayHealthRatio").setValue(dayHealthRatio)
                    personalRef.child("weekHealthRatio").setValue(weekHealthRatio)
                    personalRef.child("monthHealthRatio").setValue(monthHealthRatio)

                }else{
                    personalRef.child("dayHealthRatio").setValue(0)
                    personalRef.child("weekHealthRatio").setValue(0)
                    personalRef.child("monthHealthRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthApparelBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Apparel")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    var dayAppRatio = pTotal/30
                    var weekAppRatio = pTotal/4
                    var monthAppRatio = pTotal

                    personalRef.child("dayAppRatio").setValue(dayAppRatio)
                    personalRef.child("weekAppRatio").setValue(weekAppRatio)
                    personalRef.child("monthAppRatio").setValue(monthAppRatio)

                }else{
                    personalRef.child("dayAppRatio").setValue(0)
                    personalRef.child("weekAppRatio").setValue(0)
                    personalRef.child("monthAppRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthCharityBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Charity")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        val map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        val total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    val dayCharRatio = pTotal/30
                    val weekCharRatio = pTotal/4
                    val monthCharRatio = pTotal

                    personalRef.child("dayCharRatio").setValue(dayCharRatio)
                    personalRef.child("weekCharRatio").setValue(weekCharRatio)
                    personalRef.child("monthCharRatio").setValue(monthCharRatio)

                }else{
                    personalRef.child("dayCharRatio").setValue(0)
                    personalRef.child("weekCharRatio").setValue(0)
                    personalRef.child("monthCharRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthEducationBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Education")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    var dayEduRatio = pTotal/30
                    var weekEduRatio = pTotal/4
                    var monthEduRatio = pTotal

                    personalRef.child("dayEduRatio").setValue(dayEduRatio)
                    personalRef.child("weekEduRatio").setValue(weekEduRatio)
                    personalRef.child("monthEduRatio").setValue(monthEduRatio)

                }else{
                    personalRef.child("dayEduRatio").setValue(0)
                    personalRef.child("weekEduRatio").setValue(0)
                    personalRef.child("monthEduRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthEntertainmentBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Entertainment")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    val dayEntRatio = pTotal/30
                    val weekEntRatio = pTotal/4
                    val monthEntRatio = pTotal

                    personalRef.child("dayEntRatio").setValue(dayEntRatio)
                    personalRef.child("weekEntRatio").setValue(weekEntRatio)
                    personalRef.child("monthEntRatio").setValue(monthEntRatio)

                }else{
                    personalRef.child("dayEntRatio").setValue(0)
                    personalRef.child("weekEntRatio").setValue(0)
                    personalRef.child("monthEntRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthHouseBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("House")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    var dayHouseRatio = pTotal/30
                    var weekHouseRatio = pTotal/4
                    var monthHouseRatio = pTotal

                    personalRef.child("dayHouseRatio").setValue(dayHouseRatio)
                    personalRef.child("weekHouseRatio").setValue(weekHouseRatio)
                    personalRef.child("monthHouseRatio").setValue(monthHouseRatio)

                }else{
                    personalRef.child("dayHouseRatio").setValue(0)
                    personalRef.child("weekHouseRatio").setValue(0)
                    personalRef.child("monthHouseRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthFoodBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Food")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    var dayFoodRatio = pTotal/30
                    var weekFoodRatio = pTotal/4
                    var monthFoodRatio = pTotal

                    personalRef.child("dayFoodRatio").setValue(dayFoodRatio)
                    personalRef.child("weekFoodRatio").setValue(weekFoodRatio)
                    personalRef.child("monthFoodRatio").setValue(monthFoodRatio)

                }else{
                    personalRef.child("dayFoodRatio").setValue(0)
                    personalRef.child("weekFoodRatio").setValue(0)
                    personalRef.child("monthFoodRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getMonthTransportBudgetRations() {
        val query= budgetRef.orderByChild("item").equalTo("Transport")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var pTotal=0
                    for(ds:DataSnapshot in snapshot.children){
                        var map: Map<String,Object> = ds.getValue() as Map<String, Object>
                        var total: Object? = map.get("amount")
                        pTotal = Integer.parseInt(total.toString())

                    }

                    var dayTransRatio = pTotal/30
                    var weekTransRatio = pTotal/4
                    var monthTransRatio = pTotal

                    personalRef.child("dayTransRatio").setValue(dayTransRatio)
                    personalRef.child("weekTransRatio").setValue(weekTransRatio)
                    personalRef.child("monthTransRatio").setValue(monthTransRatio)

                }else{
                    personalRef.child("dayTransRatio").setValue(0)
                    personalRef.child("weekTransRatio").setValue(0)
                    personalRef.child("monthTransRatio").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun additem() {
        val myDialog = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.input_layout, null)
        myDialog.setView(view)
        var dialog = myDialog.create()
        dialog.setCancelable(false)

        val itemSpinner: Spinner = view.findViewById(R.id.itemSpinner)
        val amount: EditText = view.findViewById(R.id.amount)
        val cancel: Button = view.findViewById(R.id.cancel)
        val save: Button = view.findViewById(R.id.save)

        save.setOnClickListener {
            val budgetAmount: String = amount.text.toString()
            val budgetItem: String = itemSpinner.selectedItem.toString()

            if (TextUtils.isEmpty(budgetAmount)) {
                amount.setError("Amount is required.!")
                return@setOnClickListener
            }

            if (budgetItem.equals("Select item")) {
                Toast.makeText(this, "Select a valid item", Toast.LENGTH_LONG).show()
            } else {
                loader.setMessage("Adding a budget item")
                loader.setCanceledOnTouchOutside(false)
                loader.show()

                val id: String = budgetRef.push().key!!
                val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
                val cal: Calendar = Calendar.getInstance()
                val date: String = dateFormat.format(cal.time)

                val epoch = MutableDateTime()
                epoch.setDate(0)
                val now = DateTime()
                val months: Months = Months.monthsBetween(epoch, now)
                val weeks = Weeks.weeksBetween(epoch,now)
                val itemday= budgetItem+date
                val itemWeek = budgetItem+weeks.weeks
                val itemMonth = budgetItem+months.months

                val data: Data =
                    Data(budgetItem, date, id, itemday,itemWeek,itemMonth, Integer.parseInt(budgetAmount),weeks.weeks, months.months,null)
                budgetRef.child(id).setValue(data).addOnCompleteListener {
                    if (it.isSuccessful()) {
                        Toast.makeText(this, "Budget item added successfully", Toast.LENGTH_LONG)
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

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(budgetRef, Data::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                //return null

                val view: View = layoutInflater.inflate(R.layout.retrieve_layout, null)
                return MyViewHolder(view)
            }

            protected override fun onBindViewHolder(
                holder: MyViewHolder,
                position: Int,
                model: Data
            ) {

                holder.setItemAmount("Allocated amount: "+rupee+model.amount)
                holder.setDate("On: "+model.date)
                holder.setItemName("BudgetItem: "+model.item)

                holder.notes.visibility = View.GONE

                when(model.item){
                    "Transport" -> holder.imageView.setImageResource(R.drawable.transport)
                    "Food" -> holder.imageView.setImageResource(R.drawable.food)
                    "House" -> holder.imageView.setImageResource(R.drawable.house)
                    "Entertainment" -> holder.imageView.setImageResource(R.drawable.entertainment)
                    "Education" -> holder.imageView.setImageResource(R.drawable.education)
                    "Charity" -> holder.imageView.setImageResource(R.drawable.charity)
                    "Apparel" -> holder.imageView.setImageResource(R.drawable.apparel)
                    "Health" -> holder.imageView.setImageResource(R.drawable.health)
                    "Personal" -> holder.imageView.setImageResource(R.drawable.personal)
                    "Other" -> holder.imageView.setImageResource(R.drawable.other)
                }

                holder.mView.setOnClickListener{
                    post_key= getRef(position).key!!
                    item= model.item!!
                    amount= model.amount!!
                    updateData()


                }
            }


            override fun onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                //mEmptyListMessage.setVisibility(if (itemCount == 0) View.VISIBLE else View.GONE)
            }
        }

        recyclerView.adapter = adapter
        adapter.startListening()
        adapter.notifyDataSetChanged()


    }



    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.imageView)
        var mView: View = view
        var notes: TextView = view.findViewById(R.id.note)

        fun setItemName(itemName: String) {
            var item: TextView = mView.findViewById(R.id.item)
            item.text = itemName
        }

        fun setItemAmount(itemAmount: String) {
            var amount: TextView = mView.findViewById(R.id.amount)
            amount.text = itemAmount
        }

        fun setDate(itemDate: String) {
            var date: TextView = mView.findViewById(R.id.date)
            date.text = itemDate
        }


    }

    private fun updateData() {
        val myDialog = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.update_layout, null)
        myDialog.setView(view)
        val dialog = myDialog.create()

        val mItem:TextView = view.findViewById(R.id.itemName)
        val mAmount:TextView = view.findViewById(R.id.amount)
        val mNotes:TextView = view.findViewById(R.id.note)

        mNotes.visibility = View.GONE
        mItem.text = item
        mAmount.text= amount.toString()

        var delBut:Button = view.findViewById(R.id.btnDelete)
        var btnupdate:Button = view.findViewById(R.id.btnUpdate)
        btnupdate.setOnClickListener {
            amount = Integer.parseInt(mAmount.text.toString())
            var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
            var cal: Calendar = Calendar.getInstance()
            var date: String = dateFormat.format(cal.time)

            var epoch: MutableDateTime = MutableDateTime()
            epoch.setDate(0)
            var now: DateTime = DateTime()
            var months: Months = Months.monthsBetween(epoch, now)
            val weeks = Weeks.weeksBetween(epoch,now)
            val itemday= item+date
            val itemWeek = item+weeks.weeks
            val itemMonth = item+months.months

            var data =
                Data(item, date, post_key, itemday,itemWeek,itemMonth, amount,weeks.weeks, months.months,null)
            budgetRef.child(post_key).setValue(data).addOnCompleteListener {
                if (it.isSuccessful()) {
                    Toast.makeText(this, "Updated successfully.!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                }

            }

            dialog.dismiss()
        }

        delBut.setOnClickListener {
            budgetRef.child(post_key).removeValue().addOnCompleteListener {
                if (it.isSuccessful()) {
                    Toast.makeText(this, "Deleted successfully.!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                }

            }

            dialog.dismiss()
        }


        dialog.show()
    }

}

