package com.example.recyclerviewedit_del_ins

import android.content.Intent
import android.icu.text.AlphabeticIndex
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cell.view.*
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager


interface AdapterDelegate {
    fun showDetailsActivity(selectedIndex: Int? = null)
}


class MainActivity : AppCompatActivity(), AdapterDelegate {
    companion object {
        const val REQUEST_CODE = 100
        const val KEY = "activity_main.KEY"
    }

    enum class LOADTYPE(var value: Int) {
        LOADMORE(0), NORMAL(1)
    }

    var Maxsize = 20
    var loadmoresize = 5
    var list = MutableList(5) { it }
    var selectedIndex: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PULLTOREFRESH()
        setRecyclerview()
    }

    private fun PULLTOREFRESH() {
        pulltorefresh.setOnRefreshListener {
            Handler().postDelayed({
                list = MutableList(10) { it }
                Maxsize = 20
                recyclerview.adapter = NumberAdapter(this)
                pulltorefresh.isRefreshing = false
                Log.d("list", "${list.size} $Maxsize $loadmoresize")
            }, 300)
        }
    }

    private fun setRecyclerview() {
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        recyclerview.adapter = NumberAdapter(this)
    }

    override fun showDetailsActivity(selectedIndex: Int?) {
        this.selectedIndex = selectedIndex
        val intent = Intent(this, DetailsActivity::class.java)
        if (selectedIndex != null) {
            intent.putExtra(KEY, list[selectedIndex!!])
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_CODE == requestCode && resultCode == DetailsActivity.RESULT_CODE) {
            if (data != null) {
                val value = data.getIntExtra(DetailsActivity.KEY, Int.MIN_VALUE)
                if (value != Int.MIN_VALUE) {
                    if (selectedIndex != null) {
                        list[selectedIndex!!] = value
                        recyclerview.adapter?.notifyItemChanged(selectedIndex!!)
                    } else {
                        if (list.size == Maxsize) {
                            Maxsize = Maxsize + 1
                        }
                        list.add(list.size, value)
                        Log.d("list2", "${list.size} $Maxsize $loadmoresize")
                        recyclerview.adapter = NumberAdapter(this)
                        recyclerview.scrollToPosition(list.size - 1)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        showDetailsActivity()
        return true
    }


    inner class NumberAdapter(
        var delegate: AdapterDelegate
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == LOADTYPE.NORMAL.value) {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell, parent, false)
                val viewholder = NumberViewholder(itemView)
                itemView.setOnLongClickListener {
                    if (list.size == Maxsize) {
                        Maxsize -= 1
                    }
                    val position = viewholder.adapterPosition
                    list.removeAt(position)
                    notifyItemRemoved(position)
                    Log.d("list5", "${list.size} $Maxsize $loadmoresize")
                    true
                }
                itemView.setOnClickListener {
                    val selectedIndex = viewholder.adapterPosition
                    delegate.showDetailsActivity(selectedIndex)
                }
                return viewholder
            } else {
                Log.d("list4", "${list.size} $Maxsize $loadmoresize")
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.loadmore_cell, parent, false)
                val viewholder = LoadMoreViewholder(itemView)
                return viewholder
            }
        }

        override fun getItemCount(): Int = if (list.size == Maxsize) Maxsize else list.size + 1


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is NumberViewholder) {
                if (position < list.size) {
                    holder.itemView.textView.text = list[position].toString()
                }
            } else {
                LoadMore()
            }
        }

        fun LoadMore() {
            if (list.size >= Maxsize) {
                return
            }
            Handler().postDelayed({
                if (list.size + loadmoresize >= Maxsize) {
                    list.addAll(list.size, MutableList(Maxsize - list.size) { it + 1 + list.last() })
                    notifyDataSetChanged()
                } else {
                    list.addAll(list.size, MutableList(loadmoresize) { it + 1 + list.last() })
                    notifyDataSetChanged()
                }

                Log.d("list1", "${list.size} $Maxsize $loadmoresize")
            }, 1000)
        }

        override fun getItemViewType(position: Int): Int {
//        if (position == Maxsize-1) {
//            return LoadType.Normal.value
//        }
            if (position == list.size) {
                return LOADTYPE.LOADMORE.value
            } else
                return LOADTYPE.NORMAL.value
        }
    }

    inner class NumberViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class LoadMoreViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)
}