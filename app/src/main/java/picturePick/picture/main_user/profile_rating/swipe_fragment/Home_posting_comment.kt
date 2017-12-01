package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.instacart.library.truetime.TrueTimeRx
import kotlinx.android.synthetic.main.activity_home_posting_comment_list.*
import kotlinx.android.synthetic.main.toolbar_comment_act.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_get_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_put_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.comment_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_NUM
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_NUM_REQ
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_SAVE_TIME
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_VIEW
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.post_rater_data

class Home_posting_comment : AppCompatActivity(),View.OnClickListener {
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.comment_back_key->{
                onBackPressed()
            }
        }
    }

    companion object {
        var post_Rater_data: post_rater_data = post_rater_data()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_posting_comment_list)

        val actionBar : ActionBar? = supportActionBar
        actionBar?.elevation = 5F
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(false)            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar?.setDisplayShowTitleEnabled(false)        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar?.setDisplayShowHomeEnabled(false)            //홈 아이콘을 숨김처리합니다.
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionbar = inflater.inflate(R.layout.toolbar_comment_act, null)
        actionBar?.customView = actionbar
        //액션바 양쪽 공백 없애기

        comment_back_key.setOnClickListener(this)

        var adapter : Home_posting_comment_lists // 어뎁터
        val comment_list = comment_list  //리사이클러뷰
        val comment_send_btn = comment_send_button  // 댓글달기 버튼
        val comment_txt = comment_text
        var pre_comment = true

        val Intent = intent

        val post_id = Intent.getLongExtra(COMMENT_LIST_SAVE_TIME,0)  //포스팅 시간(id
        val recomment_num = Intent.getIntExtra(COMMENT_LIST_NUM,0)   //댓글 갯수
        val comment_view = Intent.getBooleanExtra(COMMENT_LIST_VIEW,false) //홈에서 넘어왔는지 평가창에서 넘어왔는지 확인

        adapter = Home_posting_comment_lists(applicationContext, post_Rater_data)
        comment_list.adapter = adapter
        comment_list.layoutManager = (LinearLayoutManager(this))

        //홈화면에서 댓글창 넘어왔을때 보기만 할수있도록
        if(comment_view){
            comment_send_btn.visibility = View.GONE
            comment_txt.visibility = View.GONE
        }
        if(recomment_num>0){ // 댓글 1개이상일때
            //댓글 목록 받아오기

            firestore_.collection(RATING_POST).document(""+(10000000000000-post_id)).get().addOnSuccessListener ({
                post_Rater_data = it.toObject(post_Rater_data::class.java)

                adapter = Home_posting_comment_lists(applicationContext, post_Rater_data)
                comment_list.adapter = adapter
                adapter.notifyDataSetChanged()
                for(i in 0 until post_Rater_data.comment_data.size)
                    comment_list.scrollToPosition(i) //스크롤 밑으로
            })
        }
        else{ //댓글 없을떄
            post_Rater_data = post_rater_data()
            adapter = Home_posting_comment_lists(this, post_Rater_data)
            comment_list.adapter = adapter
        }

        //댓글 삭제 버튼
        post_comment_Viewholder.CommetDelete(object : post_comment_Viewholder.interface_delete_comment{
            override fun interface_delete_comment(comment_position: Int) {

                //다이얼로그 띄우고 삭제 취소 선택
                val alertDialogBuilder = AlertDialog.Builder(this@Home_posting_comment)

                // 제목셋팅
                alertDialogBuilder.setTitle(getString(R.string.comment_remove))
                alertDialogBuilder
                        .setMessage(getString(R.string.comment_remove_))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.comment_remove_ok),
                                { dialog, id ->
                                    // 댓글 삭제

                                    val delete_data = post_Rater_data.comment_data[comment_position]
                                    post_Rater_data.comment_data.remove(delete_data)
                                    adapter = Home_posting_comment_lists(applicationContext, post_Rater_data)
                                    comment_list.adapter = adapter
                                    comment_list.scrollToPosition(post_Rater_data.comment_data.size-1) //스크롤 밑으로
                                    adapter.notifyDataSetChanged()


                                    firestore_.collection(RATING_POST).document(""+(10000000000000-post_id)).get().addOnCompleteListener({
                                        post_Rater_data = it.result.toObject(post_Rater_data::class.java)

                                        post_Rater_data.comment_data.remove(delete_data)


                                        //데이터 다지워지면 null 뜨니까 공백데이터 채우기
                                        if(post_Rater_data.comment_data.size==0){
                                            post_Rater_data.comment_data.add(comment_data("","","",0,""))
                                            //일부 필드만 업데이트
                                            firestore_.collection("post").document(""+(10000000000000-post_id)).update(
                                                    "recomment_num",post_Rater_data.comment_data.size
                                            )
                                            firestore_.collection(RATING_POST).document(""+(10000000000000-post_id)).set(post_Rater_data).addOnCompleteListener({
                                            })
//                                MainActivity.Photo_Database.child("post").child(""+post_id).updateChildren(map)
//                                MainActivity.Photo_Database.child(RATING_POST).child("" + post_id).updateChildren(map2)

                                            firestore_.collection("USER_DATA").document(user_info.id ).update(mapOf("" + post_id+ user_info.id+"commnet" to false))

                                            my_post_put_data.putBoolean(""+post_id+ user_info.id+"comment",true)
                                            my_post_put_data.commit()
                                        }
                                        else {
                                            //일부 필드만 업데이트
                                            firestore_.collection("post").document(""+(10000000000000-post_id)).update(
                                                    "recomment_num",post_Rater_data.comment_data.size
                                            )
                                            firestore_.collection(RATING_POST).document(""+(10000000000000-post_id)).set(post_Rater_data)

                                            if(!(0 until  post_Rater_data.comment_data.size).any { post_Rater_data.comment_data[it].commenter_id == user_info.id }){
                                                firestore_.collection("USER_DATA").document(user_info.id ).update(mapOf("" + post_id+ user_info.id+"commnet" to false))

                                                my_post_put_data.putBoolean(""+post_id+ user_info.id+"comment",true)
                                                my_post_put_data.commit()
                                            }
                                        }
                                    })
                                })
                        .setNegativeButton(getString(R.string.comment_remove_cancel),
                                { dialog, id ->
                                    // 다이얼로그 취소한다
                                    dialog.cancel()
                                })

                val dialog : AlertDialog = alertDialogBuilder.create()
                dialog.show()

            }
        })
        //댓글 달기 버튼 누를시
        comment_send_btn.setOnClickListener({
            if(comment_txt.text.toString() != ""&&pre_comment) {
                pre_comment = false

                firestore_.collection(RATING_POST).document(""+(10000000000000-post_id)).get().addOnCompleteListener({
                    post_Rater_data = it.result.toObject(post_Rater_data::class.java)

                    if(post_Rater_data.comment_data[0].proffile == "")
                        post_Rater_data.comment_data[0] = comment_data(MainActivity.user_info.dis_name,  MainActivity.user_info.profile,comment_txt.text.toString(), TrueTimeRx.now().time,user_info.id)
                    else
                        post_Rater_data.comment_data.add(comment_data(MainActivity.user_info.dis_name,  MainActivity.user_info.profile,comment_txt.text.toString(), TrueTimeRx.now().time,user_info.id))

                    adapter = Home_posting_comment_lists(applicationContext, post_Rater_data)
                    comment_list.adapter = adapter
                    adapter.notifyDataSetChanged()

                    comment_list.scrollToPosition(post_Rater_data.comment_data.size-1) //스크롤 밑으로
                    comment_txt.setText("") // 텍스트뷰 초기화

//data class comment_data(val name: String,val proffile : String, val comment : String, val comment_time:Long,val commenter_id:String):Serializable{

                    //일부 필드만 업데이트
                    firestore_.collection("post").document(""+(10000000000000-post_id)).update(
                            "recomment_num",post_Rater_data.comment_data.size
                    )
                    firestore_.collection(RATING_POST).document(""+(10000000000000-post_id)).set(post_Rater_data)


                    if(my_post_get_data.getBoolean(user_info.id+"RATE_NON_FIRST",false))
                        firestore_.collection("USER_DATA").document(user_info.id).update(mapOf("comment"+post_id+ user_info.id to true))
                    else {
                        firestore_.collection("USER_DATA").document(user_info.id ).set(mapOf("comment" + post_id+ user_info.id to true))
                        my_post_put_data.putBoolean(user_info.id+"RATE_NON_FIRST",true)
                    }

                    my_post_put_data.putBoolean("comment"+post_id+ user_info.id,true)
                    my_post_put_data.commit()

                    pre_comment = true
                })
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(COMMENT_LIST_NUM_REQ, post_Rater_data.comment_data.size)

        setResult(0,intent)
        finish()
    }
}
