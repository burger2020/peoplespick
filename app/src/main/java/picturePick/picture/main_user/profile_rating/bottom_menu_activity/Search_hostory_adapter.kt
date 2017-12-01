package picturePick.picture.main_user.profile_rating.bottom_menu_activity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_search_history.view.*
import picturePick.picture.main_user.profile_rating.R

/**
 * Created by main_user on 2017-10-16.
 */

class Search_hostory_Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val search_history_list_txt = itemView.search_history_list_text
    val search_history_list_clear_btn = itemView.search_history_list_clear
    companion object {
        lateinit var mListener: TagHistoryClickEvent
        fun tagClick(mListener: TagHistoryClickEvent) { this.mListener = mListener }
        lateinit var mListener2: TagHistoryClearEvent
        fun tagClear(mListener2: TagHistoryClearEvent) { this.mListener2 = mListener2 }
    }

    interface TagHistoryClickEvent {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_Tag_Clicked(tag : String)
    }
    interface TagHistoryClearEvent {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_Tag_Clear(tag_position:Int)
    }
    fun bindHolder(context:Context, history_list:ArrayList<String>,position: Int) {
        search_history_list_txt.text = history_list[position]

        //clear 버튼
        search_history_list_clear_btn.setOnClickListener({
            mListener2.interface_Tag_Clear(position)
        })
        //태그 검색 리스트
        search_history_list_txt.setOnClickListener({
            mListener.interface_Tag_Clicked(history_list[position])
        })
    }
}
class Search_hostory_adapter(val context:Context, val history_list: ArrayList<String>) : RecyclerView.Adapter<Search_hostory_Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Search_hostory_Viewholder {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.list_search_history,parent,false)
        return Search_hostory_Viewholder(mainView)
    }

    override fun onBindViewHolder(holder: Search_hostory_Viewholder?, position: Int) {
        holder?.bindHolder(context, history_list,position)
    }

    override fun getItemCount(): Int = history_list.size
}