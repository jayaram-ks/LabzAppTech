package com.labzapp.technician.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun toastz(cnxt: Context, messg: String)
{
    val tst = Toast.makeText(cnxt, messg, Toast.LENGTH_LONG)
    tst.setGravity( Gravity.FILL_HORIZONTAL, 0, 0)
    tst.show()
}

fun snackze(cnxtv: View, messg: String,idz:Int){
    Snackbar.make(cnxtv,messg,Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).setAnchorView(idz).setBackgroundTint(Color.RED).show()
}

fun snackzsucc(cnxtv: View, messg: String,idz:Int){
    Snackbar.make(cnxtv,messg,Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).setAnchorView(idz).setBackgroundTint(Color.parseColor("#FF228B22")).setDuration(4000).show()
}

fun snackzcolor(cnxtv: View, messg: String,idz:Int,colorstring:String,sduration:Int){
    Snackbar.make(cnxtv,messg,Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).
    setAnchorView(idz).setBackgroundTint(Color.parseColor(colorstring)).setDuration(sduration).show()
}







