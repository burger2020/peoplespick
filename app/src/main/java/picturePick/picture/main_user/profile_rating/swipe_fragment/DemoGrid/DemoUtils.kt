package picturePick.picture.main_user.profile_rating.swipe_fragment.DemoGrid

import android.util.Log

/**
 * Created by main_user on 2017-11-26.
 */

internal class DemoUtils {
    var currentOffset: Int = 0
    private val select : Int
    constructor(select : Int){
        this.select = select
    }
    constructor(){
        this.select = 0
    }
    fun moarItems(qty: Int): List<DemoItem> {
        val items = arrayListOf<DemoItem>()

        when(select){
            //평가페이지에서 넘어왔을떄
            0-> {
                when (qty) {
                    2 -> {
                        //                          가로         세로       번호
                        items.add(DemoItem(160, 80, 0))
                        items.add(DemoItem(160, 80, 1))
                    }
                    3 -> {

                        items.add(DemoItem(2, 3, 0))
                        items.add(DemoItem(2, 3, 1))
                        items.add(DemoItem(4, 3, 2))
                    }
                    4 -> {
                        (0..4).mapTo(items) { DemoItem(2, 3, it) }
                    }
                    5 -> {

                        items.add(DemoItem(2, 2, 0))
                        items.add(DemoItem(2, 2, 1))
                        items.add(DemoItem(4, 2, 2))
                        items.add(DemoItem(2, 2, 3))
                        items.add(DemoItem(2, 2, 4))
                    }
                    6 -> {
                        (0..6).mapTo(items) { DemoItem(2, 2, it) }
                    }
                }
            }
            //홈화면에서 넘어왔을때
            1->{
                when(qty){
                    2->{
                        //           가로         세로       인덱스
                        (0 .. 2).mapTo(items) { DemoItem(30, 40, it) }
                    }
                    3->{//2 1
                        (0 .. 3).mapTo(items) { DemoItem(20, 40, it) }
//                        items.add(DemoItem(125, 160, 0))
//                        items.add(DemoItem(125, 160, 1))
//                        items.add(DemoItem(250, 120, 2))
                    }
                    4->{
                        (0 .. 4).mapTo(items) { DemoItem(30, 20, it) }
                    }
                    5->{//3 2
                        items.add(DemoItem(20, 20, 0))
                        items.add(DemoItem(20, 20, 1))
                        items.add(DemoItem(20, 20, 2))
                        items.add(DemoItem(30, 20, 3))
                        items.add(DemoItem(30, 20, 4))
                    }
                    6->{
                        (0 .. 6).mapTo(items) { DemoItem(20, 20, it) }
                    }
                }
            }
        }
//        for (i in 0 until qty) {
//            val colSpan = if (Math.random() < 0.2f) 2 else 1
//            // Swap the next 2 lines to have items with variable
//            // column/row span.
//            // int rowSpan = Math.random() < 0.2f ? 2 : 1;
//            val item = DemoItem(colSpan, colSpan, currentOffset + i)
//            items.add(item)
//        }

        currentOffset += qty

        Log.d("photo_list_num",""+items)
        return items
    }
}