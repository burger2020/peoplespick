package picturePick.picture.main_user.profile_rating.data

import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by main_user on 2017-10-14.
 */
class pic_basic_data{
    companion object {
        var pick_photo_num:Int = 6 //post 등록시 사진 기본 선택가능 갯수
        val rating_list_add = 5    //리스트 refrash 할때 추가할 갯수
    }
}

class pic_pic_string_data{
    companion object {
        //TODO 파이어베이스 데이터베이스 기본 가져올 갯수
        val INIT_DATA_NUM = 10


        val EDIT_COMPLETE = 10004
        val RATING_COMPLETE = 10005
        val ratingPage_tagClick = 10023

        //SharedPreferences
        val SharedPreferenceS = "SharedPreferenceS"
        val DATA_CHECK = "DATA_CHECK"
        val AUTH_DATA = "AUTH_DATA"
        val MY_POST_NUM_CHECK = "MY_POST_NUM_CHECK"  // 내 포스트 갯수 가져오기
        val POST_ID = "POST_ID"  // 포스트 저장 키값
        val MY_RATING_POST_NUM_CHECK = "MY_RATING_POST_NUM_CHECK"  // 내 포스트 갯수 가져오기
        val RATING_POST_ID = "RATING_POST_ID"  // 포스트 저장 키값


        //TODO 어뎁터 타입 게시물
        val HEADER_TYPE = 1
        val ITEM_TYPE = 2
        val FOOTER_TYPE = 3


        //TODO 관심태그당 게시물 뷰 갯수
        val INTEREST_VIEW_NUM = 5


        //TODO 제공업체 확인
        val PROVIDER_GOOGLE="google.com"
        val PROVIDER_PASSWORD="password"

        //TODO 어뎁터 섹션 구분
        val HOME = "HOME"
        val SEARCH = "SEARCH"
        val MYPOST = "MYPOST"
        val INTEREST = "INTEREST"

        //TODO 사진 평가 페이지
        val HOME_RATING_DATA = "HOME_RATING_DATA"
        val HOME_RATING_COMMENT_DATA = "HOME_RATING_COMMENT_DATA"
        val HOME_RATING_RATING_LIST = "HOME_RATING_RATING_LIST"
        val HOME_RATING_RATING_BOOL= "HOME_RATING_RATING_BOOL"
        val HOME_RATING_RATING_POSITION= "HOME_RATING_RATING_POSITION"
        val HOME_RATING_NON= 44
        val HOME_COMMENT_NON= false
        val HOME_RATING_END= 10
        val HOME_RATING_PHOTOS = "HOME_RATING_PHOTOS"
        val HOME_RATING_COMMENT = "HOME_RATING_COMMENT"
        val HOME_RATING_OPTION_COMMENT = "HOME_RATING_OPTION_COMMENT"
        val HOME_RATING_OPTION_CAPTURE = "HOME_RATING_OPTION_CAPTURE"
        val HOME_RATING_OPTION_INVISIBLE = "HOME_RATING_OPTION_INVISIBLE"
        val HOME_RATING_OPTION_MOSAIC = "HOME_RATING_OPTION_MOSAIC"
        val HOME_RATING_RATING_NUM= "HOME_RATING_RATING_NUM"
        val HOME_RATING_RATING_SAVE_TIME= "HOME_RATING_RATING_SAVE_TIME"
        val HOME_RATING_RATING_END_TIME= "HOME_RATING_RATING_END_TIME"
        val HOME_RATING_POSTER_NAME = "HOME_RATING_POSTER_NAME"
        val HOME_RATING_POSTER_PROFILE = "HOME_RATING_POSTER_PROFILE"
        val HOME_RATING_POSTER_TOKEN = "HOME_RATING_POSTER_TOKEN"
        val HOME_RATING_POSTER_PROVIDER = "HOME_RATING_POSTER_PROVIDER"
        val HOME_RATING_POST_RECOMMENT = "HOME_RATING_POST_RECOMMENT"

        //TODO 사진 평가 스크롤링 페이지
        val HOME_RATING_SCROLL_PHOTOS = "HOME_RATING_SCROLL_PHOTOS"
        val HOME_RATING_SCROLL_HEART_BOOL = "HOME_RATING_SCROLL_HEART_BOOL"
        val HOME_RATING_SCROLL_POSITION = "HOME_RATING_SCROLL_POSITION"
        val HOME_RATING_SCROLL_CAPTURE_BOOL = "HOME_RATING_SCROLL_CAPTURE_BOOL"
        val HOME_RATING_SCROLL_RATE_BOOL = "HOME_RATING_SCROLL_RATE_BOOL"
        val HOME_RATING_SCROLL_RATE_RESULT = "HOME_RATING_SCROLL_RATE_RESULT"

        //TODO 사진 평가 스크롤링 페이지 finish
        val HOME_RATING_SCROLL_HEART_RESULT = "HOME_RATING_SCROLL_HEART_RESULT"
        val HOME_RATING_SCROLL_HEART_POSITION = "HOME_RATING_SCROLL_HEART_POSITION"
        val HOME_RATING_SCROLL_HEART_RESULT_BOOL = "HOME_RATING_SCROLL_HEART_POSITION"

        //TODO 게시물 수정 시
        val POST_CUSTOM_DATA = "POST_CUSTOM_DATA"
        val POST_UPDATE_DATA = "POST_UPDATE_DATA"

        //TODO 댓글창 열때 인텐트
        val COMMENT_LIST_SAVE_TIME = "COMMENT_LIST_SAVE_TIME"
        val COMMENT_LIST_NUM = "COMMENT_LIST_NUM"
        val COMMENT_LIST_VIEW = "COMMENT_LIST_VIEW"
        val COMMENT_LIST_NUM_REQ = "COMMENT_LIST_NUM_REQ"

        //TODO 태그 검색 히스토리 불러올때
        val SEARCH_HISTORY_LIST = "SEARCH_HISTORY_LIST"
        val SEARCH_HISTORY_LIST_NUM = "SEARCH_HISTORY_LIST_NUM"

        //TODO 파이어베이스 데이터베이스용
        val DATABASE_PHOTO_ARRAY = "DATABASE_PHOTO_ARRAY"
        val DATABASE_PHOTO_OPTION = "DATABASE_PHOTO_OPTION"
        val DATABASE_PHOTO_COMMENT = "DATABASE_PHOTO_COMMENT"
        val DATABASE_PHOTO_REP = "DATABASE_PHOTO_REP"
        val DATABASE_SAVE_NAME = "DATABASE_SAVE_NAME"

        val RATE_LIST = "RATE_LIST"
        val RATING_POST = "RATING_POST"
        val MY_POST_ID = "MY_POST_ID"
        val RECOMMENT = "RECOMMENT"
        val LIST = "LIST"
        val TAG_LIST = "TAG_LIST"
        val TAG = "TAG"
        val TAG_SIZE = "TAG_SIZE"

        //TODO 파이어베이스 AUTH 용
        val AUTH_NON_PROFILE = "AUTH_NON_PROFILE"
        val PROFILE_CHANGE= "PROFILE_CHANGE"
        val PROFILE_STATE= "PROFILE_STATE"
        val NON_PROFILE= "NON_PROFILE"
        val CUSTOM_PROFILE= "CUSTOM_PROFILE"
        val AUTH_RESULT= 11552

        //TODO 파이어베이스 STORAGE 프로필 용
        val PROFILE = "PROFILE_PATH_"


        //TODO 카메라/갤러리 REQUEST
        val CAMERA_PERMISION = 5432
        val WRITE_PERMISION = 5433
        val CAMERA_CODE = 5321
        val GALLERY_CODE = 5322
    }
}

data class option_info_data(var mosaic_bool:Boolean, var captuer_bool:Boolean, var comment_bool:Boolean, var invisible_bool:Boolean) : Serializable{
    constructor():this(false,false,false,false)
}

data class Post_Main_Data(val photo_list_num:Int, var photo_url: ArrayList<String>, var comment:String, var option_info_data: option_info_data, val save_time:Long,
                     val rating_end_time:Long, val poster_name: String, var post_rater_num : Int, val poster_profile: String, var recomment_num : Int, val poster_token : String,
                          val tag : ArrayList<String>) : Serializable {

    constructor():this(0,arrayListOf<String>(),"", option_info_data(),0,0,"",0,"",0,"", arrayListOf())
    constructor(recomment_num:Int):this(0,arrayListOf<String>(),"", option_info_data(),0,0,"",0,"",0,"", arrayListOf()){
        this.recomment_num = recomment_num
    }
    constructor(post_rater_num:Int,a:Int):this(0,arrayListOf<String>(),"", option_info_data(),0,0,"",0,"",0,"", arrayListOf()){
        this.post_rater_num = post_rater_num
    }
    constructor(comment: String, photo_url: ArrayList<String>,option_info_data: option_info_data)
            :this(0,arrayListOf<String>(),"", option_info_data(),0,0,"",0,"",0,"", arrayListOf())
    {
        this.comment = comment
        this.photo_url = photo_url
        this.option_info_data = option_info_data
    }

    @Exclude
    fun raterNum_update():Map<String,Any>{
        var result = HashMap<String,Any>()
        result.put("post_rater_num",post_rater_num)
        return result
    }
    @Exclude
    fun commentNum_update():Map<String,Any>{
        var result = HashMap<String,Any>()
        result.put("recomment_num",recomment_num)
        return result
    }
}
data class post_rater_data(var rater_num : Int,var comment_data : ArrayList<comment_data>,var rating_result : ArrayList<Int> ):Serializable{
    constructor():this(0, arrayListOf<comment_data>(), arrayListOf())
    constructor(comment_data: ArrayList<comment_data>):this(0, arrayListOf<comment_data>(), arrayListOf()){
        this.comment_data = comment_data
    }
    constructor(rater_num: Int, rating_result: ArrayList<Int>):this(0, arrayListOf<comment_data>(), arrayListOf()){
        this.rater_num = rater_num
        this.rating_result = rating_result
    }

    @Exclude
    fun rating_update():Map<String,Any>{
        var result = HashMap<String,Any>()
        result.put("rating_result",rating_result)
        result.put("rater_num",rater_num)
        return result
    }
    @Exclude
    fun comment_update():Map<String,Any>{
        var result = HashMap<String,Any>()
        result.put("comment_data",comment_data)
        return result
    }
}

data class comment_data(val name: String,val proffile : String, val comment : String, val comment_time:Long,val commenter_id:String):Serializable{
    constructor():this("","","",0,"")
}
