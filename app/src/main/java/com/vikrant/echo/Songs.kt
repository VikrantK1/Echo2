package com.vikrant.echo

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.Comparator

@SuppressLint("ParcelCreator")
class Songs(
    var SongId: Long, var SongTitle: String,
    var Artist: String, var songData: String,
    var DateAdded: Long
) : Parcelable {
    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }

    object statified {
        var dateComparator: Comparator<Songs> = kotlin.Comparator<Songs> { o1, o2 ->
            var song1 = o1.DateAdded.toDouble()
            var song2 = o2.DateAdded.toDouble()
            song2.compareTo(song1)
        }
        var nameComparator: Comparator<Songs> = kotlin.Comparator { o1, o2 ->
            var song1 = o1.SongTitle.toUpperCase()
            var song2 = o2.SongTitle.toUpperCase()
            song2.compareTo(song1)
        }
    }
}
