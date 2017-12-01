package picturePick.picture.main_user.profile_rating.swipe_fragment.DemoGrid

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem



/**
 * Created by main_user on 2017-11-26.
 */

class DemoItem : AsymmetricItem {
    private var columnSpan: Int = 0
    private var rowSpan: Int = 0
    var position: Int = 0
        private set

    @JvmOverloads constructor(columnSpan: Int = 1, rowSpan: Int = 1, position: Int = 0) {
        this.columnSpan = columnSpan
        this.rowSpan = rowSpan
        this.position = position
    }

    constructor(`in`: Parcel) {
        readFromParcel(`in`)
    }

    override fun getColumnSpan(): Int = columnSpan

    override fun getRowSpan(): Int = rowSpan

    override fun toString(): String = String.format("%s: %sx%s", position, rowSpan, columnSpan)

    override fun describeContents(): Int = 0

    private fun readFromParcel(`in`: Parcel) {
        columnSpan = `in`.readInt()
        rowSpan = `in`.readInt()
        position = `in`.readInt()
    }

    override fun writeToParcel(@NonNull dest: Parcel, flags: Int) {
        dest.writeInt(columnSpan)
        dest.writeInt(rowSpan)
        dest.writeInt(position)
    }

    companion object {

        /* Parcelable interface implementation */
        val CREATOR: Parcelable.Creator<DemoItem> = object : Parcelable.Creator<DemoItem> {
            override fun createFromParcel(@NonNull `in`: Parcel): DemoItem = DemoItem(`in`)

            @NonNull override fun newArray(size: Int): Array<DemoItem?> = arrayOfNulls(size)
        }
    }
}