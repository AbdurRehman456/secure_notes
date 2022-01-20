package com.example.securenotesapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotesapp.EditActivity.Companion.mId
import com.example.securenotesapp.MainActivity.Companion.is_in_action_mode
import com.example.securenotesapp.MainActivity.Companion.listNew
import com.google.android.gms.ads.*

class AdaptarClass(
    private var context: Context?,
    private var List1: List<DataClass>,
    private val onItemClickListner: onItemClickListner
) : RecyclerView.Adapter<AdaptarClass.ViewHolder>(), Filterable {
    private var db: DbHelper? = null
    private var arraylist: List<Any>? = null

    init {
        db = DbHelper(context!!)
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val textviewTitle: TextView = itemview.findViewById(R.id.recyler_list_Title)
        val textviewDescription: TextView = itemview.findViewById(R.id.recyler_list_Description)
        val textViewid: TextView = itemview.findViewById(R.id.id)
        var layout: RelativeLayout = itemview.findViewById(R.id.mRelativerecycler)
        val checkbox: CheckBox = itemview.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyler_list, parent, false)
        arraylist = ArrayList(List1)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.textviewTitle.text = List1[position].mTitle
        holder.textviewDescription.text = List1[position].mDescription
        holder.textViewid.text = List1[position].id.toString()
        val sID = List1[position]

        if (!is_in_action_mode) {
            holder.checkbox.visibility = View.GONE
        } else {
            holder.checkbox.visibility = View.VISIBLE
            holder.checkbox.isChecked = false
        }

        for (i in 0 until listNew.size) {
            if (listNew[i] == List1[position].id) {
                holder.checkbox.isChecked = true
            }
        }


        ///     onclick listener on layout
        holder.layout.setOnClickListener {

            if (is_in_action_mode == true) {
                holder.layout.setOnClickListener {

                }

            } else {
                val title = holder.textviewTitle.text.toString()
                val desc = holder.textviewDescription.text.toString()
                mId = List1[position].id
                onItemClickListner.onClick(position, title, desc)
            }

        }
        /////         onlong click on layout
        holder.layout.setOnLongClickListener {

            onItemClickListner.onLongClick(position)
            return@setOnLongClickListener true
        }
        holder.checkbox.isChecked = false
        for (i in 0 until listNew.size) {
            if (listNew[i] == List1[position].id) {
                holder.checkbox.isChecked = true
            }
        }

        holder.checkbox.setOnClickListener {
            if (holder.checkbox.isChecked) {
                listNew.add(sID.id)
                onItemClickListner.onSelectNote(listNew)
                Log.e("abdul", "rehman $mId : " + listNew.toString())
            } else {
                listNew.remove(sID.id)
                onItemClickListner.onSelectNote(listNew)
                Log.e("qadir ..........remove", "rehman $mId : " + listNew.toString())
            }
        }
        if (position % 2 == 0) {

            (holder.layout.background).setColorFilter(
                Color.parseColor("#FB9DA7"),
                PorterDuff.Mode.SRC_IN
            )
        } else if (position % 3 == 0)
            (holder.layout.background).setColorFilter(
                Color.parseColor("#FBDEA2"),
                PorterDuff.Mode.SRC_IN
            )
        else {
            (holder.layout.background).setColorFilter(
                Color.parseColor("#8EB695"),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    override fun getItemCount(): Int {
        return List1.size
    }

    fun filterList(filteredList: List<DataClass>) {
        List1 = filteredList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterList: MutableList<Any> = ArrayList()
                val charText = constraint.toString().toLowerCase().trim { it <= ' ' }
                if (charText.isEmpty()) {
                    filterList.addAll(arraylist!!)
                } else {
                    for (obj in arraylist!!) {
                        if (obj is DataClass) {
                            obj.mTitle
                            if (obj.mTitle!!.toLowerCase().contains(charText)) {
                                filterList.add(obj)
                            }
                        }
                    }

                }
                val results = FilterResults()
                results.values = filterList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                List1.toMutableList().clear()
                List1.toMutableList().addAll(results!!.values as List<DataClass>)
                notifyDataSetChanged()
            }

        }
    }

}





