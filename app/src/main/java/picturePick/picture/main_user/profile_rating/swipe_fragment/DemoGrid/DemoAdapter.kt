package picturePick.picture.main_user.profile_rating.swipe_fragment.DemoGrid

import android.widget.ListAdapter

/**
 * Created by main_user on 2017-11-26.
 */
interface DemoAdapter : ListAdapter {

    fun appendItems(newItems: List<DemoItem>)

    fun setItems(moreItems: List<DemoItem>)
}