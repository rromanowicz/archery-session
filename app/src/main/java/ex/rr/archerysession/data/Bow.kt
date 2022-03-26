package ex.rr.archerysession.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Bow(
    @SerializedName("id")
    var id: Long ,
    @SerializedName("name")
    var name: String,
    @SerializedName("description")
    var description: String
) {
    fun getJSON(): String {
        return Gson().toJson(this)
    }
}