package com.sakshigarg.dailyexpensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeekSpendingAdapter(
    val weekSpendingActivity: WeekSpendingActivity,
    val myDataList: MutableList<Data>,
):RecyclerView.Adapter<WeekSpendingAdapter.MyViewHolder>()  {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imageView)
        var item: TextView = view.findViewById(R.id.item)
        var amount: TextView = view.findViewById(R.id.amount)
        var date: TextView = view.findViewById(R.id.date)
        var notes: TextView = view.findViewById(R.id.note)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return   MyViewHolder(
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
            "Other" -> holder.imageView.setImageResource(R.drawable.other)
        }
    }

    override fun getItemCount(): Int {
        return myDataList.size
    }




}