package ex.rr.archerysession.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Settings(
    @SerializedName("bows")
    var bows: MutableList<Bow>
) {
    fun getJSON(): String {
        return Gson().toJson(this)
    }
}
