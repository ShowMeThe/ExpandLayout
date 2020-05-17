package com.showMeThe.show

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_int.view.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val list = ArrayList<Boolean>()
        for(i in 0..20){
            list.add(false)
        }

        val adapter = IntAdapter(list)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)



    }





    class IntAdapter(var list:ArrayList<Boolean>) : RecyclerView.Adapter<IntAdapter.ViewHolder>() {

        class  ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_int,parent,false))
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.apply {
                val item = list[position]
                tvPos.text = "${list[position]}"
                tvPos2.text = "${position}"


                expandParent.toggleStateImmediately(item)
                expanded.setOnStateChangeListener {
                    list[position] = it
                }
                expandParent.setOnClickListener {
                    expandParent.toggle(true)
                }

            }
        }


    }




}
