package ex.rr.archerysession.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Bow(
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("name")
    var name: String,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("length")
    var length: Int? = null,
    @SerializedName("braceHeight")
    var braceHeight: Long? = null,
    @SerializedName("topTiller")
    var topTiller: Long? = null,
    @SerializedName("bottomTiller")
    var bottomTiller: Long? = null
) {
    fun getJSON(): String {
        return Gson().toJson(this)
    }
}