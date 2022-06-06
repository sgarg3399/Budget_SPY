package com.sakshigarg.dailyexpensetracker

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TodayItemsAdapter(
    var todaySpendingActivity:Activity,
    val myDataList: MutableList<Data>,
) :RecyclerView.Adapter<TodayItemsAdapter.MyViewHolder>() {

    //private lateinit var mContext:Context
   // private lateinit var myDataList: MutableList<Data>
    private var post_key:String =""
    private var item:String=""
    private var note:String=""
    private var amount:Int =0
    private lateinit var rupee: String






    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
      var imageView:ImageView= view.findViewById(R.id.imageView)
      var item:TextView = view.findViewById(R.id.item)
      var amount:TextView= view.findViewById(R.id.amount)
      var date:TextView = view.findViewById(R.id.date)
      var notes:TextView= view.findViewById(R.id.note)





    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.retrieve_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data:Data = myDataList.get(position)
        holder.item.setText("Item: "+data.item)
        holder.amount.setText("Amount: "+"\u20B9"+data.amount)
        holder.date.setText("On: "+data.date)
        holder.notes.setText("Note: "+data.notes)

        when(data.item){
            "Transport" -> holder.imageView.setImageResource(R.drawable.transport)
            "Food" -> holder.imageView.setImageResource(R.drawable.food)
            "House" -> holder.imageView.setImageResource(R.drawable.house)
            "Entertainment" -> holder.imageView.setImageResource(R.drawable.entertainment)
            "Education" -> holder.imageView.setImageResource(R.drawable.education)
            "Charity" -> holder.imageView.setImageResource(R.drawable.charity)
            "Apparel" -> holder.imageView.setImageResource(R.drawable.apparel)
            "Health" -> holder.imageView.setImageResource(R.drawable.health)
            "Personal" -> holder.imageView.setImageResource(R.drawable.personal)
            "Other" ->holder.imageView.setImageResource(R.drawable.other)
        }

        holder.itemView.setOnClickListener{
            post_key= data.id!!
            item= data.item!!
            amount= data.amount!!
            note= data.notes!!
            updateData()


        }
    }

    private fun updateData() {
        val myDialog = AlertDialog.Builder(todaySpendingActivity)
        val inflater = LayoutInflater.from(todaySpendingActivity)
        val view: View = inflater.inflate(R.layout.update_layout, null)
        myDialog.setView(view)
        val dialog = myDialog.create()

        val mItem:TextView = view.findViewById(R.id.itemName)
        val mAmount:TextView = view.findViewById(R.id.amount)
        val mNotes:TextView = view.findViewById(R.id.note)


        mItem.text = item
        mAmount.text= amount.toString()

        mNotes.text = note

        var delBut:Button = view.findViewById(R.id.btnDelete)
        var btnupdate:Button = view.findViewById(R.id.btnUpdate)
        btnupdate.setOnClickListener {
            amount = Integer.parseInt(mAmount.text.toString())
            note = mNotes.text.toString();
            var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy");
            var cal: Calendar = Calendar.getInstance()
            var date: String = dateFormat.format(cal.time)

            var epoch = MutableDateTime()
            epoch.setDate(0)
            var now = DateTime()
            var months: Months = Months.monthsBetween(epoch, now)
            val weeks = Weeks.weeksBetween(epoch,now)
            val itemday= item+date
            val itemWeek = item+weeks.weeks
            val itemMonth = item+months.months

            var data =
                Data(item, date, post_key, itemday,itemWeek,itemMonth, amount,weeks.weeks, months.months,note)
            val reference = FirebaseDatabase.getInstance().reference.child("expenses").child(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
            reference.child(post_key).setValue(data).addOnCompleteListener {
                if (it.isSuccessful()) {
                    Toast.makeText(todaySpendingActivity, "Updated successfully.!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(todaySpendingActivity, it.exception.toString(), Toast.LENGTH_LONG).show()
                }

            }

            dialog.dismiss()
        }

        delBut.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().reference.child("expenses").child(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
            reference.child(post_key).removeValue().addOnCompleteListener {
                if (it.isSuccessful()) {
                    Toast.makeText(todaySpendingActivity, "Deleted successfully.!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(todaySpendingActivity, it.exception.toString(), Toast.LENGTH_LONG).show()
                }

            }

            dialog.dismiss()
        }


        dialog.show()
    }

    override fun getItemCount(): Int {
        return myDataList.size
    }
}



