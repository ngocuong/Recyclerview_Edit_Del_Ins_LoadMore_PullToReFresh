package com.example.recyclerviewedit_del_ins

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    companion object{
        const val RESULT_CODE = 100
        const val KEY = "package com.example.recyclerviewedit_del_ins.KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val gettext=intent.getIntExtra(MainActivity.KEY, Int.MIN_VALUE)
        if (gettext!=Int.MIN_VALUE){
            editText.setText("$gettext")
        }
    }
    fun onClickFinish(view:View){
        val text = editText.text.toString().toIntOrNull()
        if (text==null){
            Toast.makeText(this,"Hãy nhâp 1 số nguyên",Toast.LENGTH_SHORT).show()
            return
        }
        intent.putExtra(KEY,text)
        setResult(RESULT_CODE,intent)
        finish()
    }
}
