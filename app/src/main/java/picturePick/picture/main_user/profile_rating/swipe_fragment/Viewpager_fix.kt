package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by main_user on 2017-10-28.
 */
class Viewpager_fix : android.support.v4.view.ViewPager {
    constructor(context : Context):super(context)
    constructor(context : Context, attributeSet: AttributeSet):super(context,attributeSet)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        try{
            return super.onTouchEvent(ev)
        }catch (e:IllegalArgumentException){}
        return  false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        }catch (e:IllegalArgumentException){}
        return  false
    }
}