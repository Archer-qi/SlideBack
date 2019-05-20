package com.archer.slideback

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.view.menu.MenuAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.vshidai.slideback.SlideBackView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    val list = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in 0..40) {
            list.add(i)
        }

        list_item.adapter = MyAdapter()

        list_item.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                startActivity(Intent(this@MainActivity,SecondActivity::class.java))
            }
        }
    }

    inner class MyAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflate = View.inflate(this@MainActivity, R.layout.item_single_text,null)
            val textView = inflate.findViewById<TextView>(R.id.text)
            textView.text = list.get(position).toString()
            return inflate
        }

        override fun getItem(position: Int): Int = list[position]

        override fun getItemId(position: Int): Long = 0

        override fun getCount(): Int = list.size
    }
}
