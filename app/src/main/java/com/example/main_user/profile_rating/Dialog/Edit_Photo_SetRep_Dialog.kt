package com.example.main_user.profile_rating.Dialog

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.example.main_user.profile_rating.R
import kotlinx.android.synthetic.main.dialog_edit_rep.*

/**
 * Created by main_user on 2017-10-22.
 */
class Edit_Photo_SetRep_Dialog :Dialog{
    var rep_photo_list : ArrayList<Uri>
    constructor(context:Context,rep_photo_list:ArrayList<Uri>):super(context){
        this.rep_photo_list = rep_photo_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_rep)
        val rep_adater = Edit_photo_setRep_dialog_adapter(context,rep_photo_list)
        val rep_list = eid_dialog_select_photo_list
        rep_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rep_list.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,context.resources.displayMetrics.heightPixels/3)

        rep_list.adapter = rep_adater

    }
}