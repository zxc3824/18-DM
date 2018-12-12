package abc.kintegratedtest

import abc.kintegratedtest.R.drawable.rounded_color
import abc.kintegratedtest.R.id.*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomArrayAdapter(context : Context, resource : Int, objects: List<SpinnerItem>) : ArrayAdapter<SpinnerItem>(context, resource, 0, objects) {
    private val mContext = context
    private val mInflater = LayoutInflater.from(mContext)
    private val mResource = resource
    private val items = objects

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = createItemView(position, convertView, parent)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?) = createItemView(position, convertView, parent)

    private fun createItemView(i : Int, view: View?, viewGroup: ViewGroup?) : View {
        val v = mInflater.inflate(mResource, viewGroup, false)

        if (i == count - 1) v.setBackgroundResource(rounded_color)

        val icon : ImageView = v.findViewById(iv_selectedIcon)
        val txt : TextView = v.findViewById(tv_iconText)

        val offerData : SpinnerItem = items[i]

        icon.setImageResource(offerData.id)
        txt.text = offerData.txt

        return v
    }
}