package picturePick.picture.main_user.profile_rating.data

/**
 * Created by main_user on 2017-10-18.
 */
class User_Data {
    companion object {
        var User_Coin = 0

        var add_picPhoto_num = arrayOf(false,false,false)

        val CLICK_PHOTO_URI : String = "CLICK_PHOTO_URI"
        val CLICK_PHOTO_POSITION : String = "CLICK_PHOTO_POSITION"
        val NOW_PICKPHOTO_NUM : String = "NOW_PHICKPHOTO_NUM"
        val ADD_PICKPHOTO_NUM : String = "ADD_PICKPHOTO_NUM"
        val CHEK_PHOTO_LIST : String = "CHECK_PHOTO_LIST"
    }
}

class User_Info(val email:String ,val  dis_name:String, val phoneNum:String,val profile : String,val id : String,val provider : String){
    constructor():this("","","","","","")
}