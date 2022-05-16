package com.enpik.inventorymanagement.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import com.enpik.inventorymanagement.R
import com.enpik.inventorymanagement.model.GetManufacturerResponseModel

class CustomAdapter(val context: Activity, val manufacturers: MutableList<GetManufacturerResponseModel>) :
    ArrayAdapter<GetManufacturerResponseModel>(context, R.layout.custom_item, manufacturers) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.custom_item, null, true)
        val titleText = rowView.findViewById(R.id.text) as TextView
        titleText.text = manufacturers.get(position).name
        return rowView
    }
}