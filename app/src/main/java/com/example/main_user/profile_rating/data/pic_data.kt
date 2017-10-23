package com.example.main_user.profile_rating.data

import android.net.Uri

/**
 * Created by main_user on 2017-10-14.
 */
class pic_data(photo_url:ArrayList<Uri>)

class pic_pic_string_data{
    companion object {
        val EDIT_COMPLETE = 10004

        //TODO 일반
        val EDIT_COMPLETE_PHOTOS = "EDIT_COMPLETE_PHOTOS"
        val EDIT_COMPLETE_PHOTO_NUM = "EDIT_COMPLETE_PHOTO_NUM"
        val EDIT_COMPLETE_PHOTO_COMMENT= "EDIT_COMPLETE_PHOTO_NUM"
        val EDIT_COMPLETE_PHOTO_REP = "EDIT_COMPLETE_PHOTO_NUM"
        val EDIT_COMPLETE_MOSAIC = "EDIT_COMPLETE_MOSAIC"
        val EDIT_COMPLETE_CAPTURE = "EDIT_COMPLETE_CAPTURE"
        val EDIT_COMPLETE_COMMENT = "EDIT_COMPLETE_COMMENT"
        val EDIT_COMPLETE_INVISIBLE = "EDIT_COMPLETE_INVISIBLE"

        //TODO 파이어베이스 데이터베이스용
        val DATABASE_PHOTO_ARRAY = "DATABASE_PHOTO_ARRAY"
        val DATABASE_PHOTO_OPTION = "DATABASE_PHOTO_OPTION"
        val DATABASE_PHOTO_COMMENT = "DATABASE_PHOTO_COMMENT"
        val DATABASE_PHOTO_REP = "DATABASE_PHOTO_REP"
        val DATABASE_PHOTO_NUM = "DATABASE_PHOTO_NUM"
    }
}

data class option_info_data(val mosaic_bool:Boolean, val captuer_bool:Boolean,val comment_bool:Boolean ,val invisible_bool:Boolean)