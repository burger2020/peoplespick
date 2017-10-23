package com.example.main_user.profile_rating

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by main_user on 2017-10-18.
 */

class fonts : AppCompatActivity() {

    private var type:Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = Typeface.createFromAsset(this.assets, "fonts/NanumBarunGothic.ttf")
        setGlobalFont(window.decorView)

    }
    fun setGlobalFont(view: View){
        if(view!=null){
            if(view is ViewGroup){
                var viewGroup : ViewGroup = view as ViewGroup
                var vgCng : Int = viewGroup.childCount
                for(i in 0..vgCng){
                    var v:View = viewGroup.getChildAt(i)
                    if(v is TextView){
                        v.setTypeface(type) as TextView
                    }
                    setGlobalFont(v)
                }

            }
        }
    }
}