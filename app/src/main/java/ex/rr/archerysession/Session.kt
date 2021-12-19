package ex.rr.archerysession

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.*

data class Session(
    @SerializedName("startDate")
    val startDate: Date = Date(),
    @SerializedName("endDate")
    var endDate: Date? = null,
    @SerializedName("nomberOfEnds")
    var ends: Int = 0,
    @SerializedName("numberOfArrows")
    var arrows: Int = 0,
    @SerializedName("endScores")
    var scores: MutableList<MutableList<Int>> = mutableListOf()
) {

    fun addEndScores(scores: MutableList<Int>) {
        this.ends++
        this.arrows += scores.size
        this.scores.add(scores)
    }

    fun endSession() {
        endDate = Date()
    }

    fun getTotalScore(): Int {
        return scores.sumOf { it -> it.sumOf { it } }
    }

    fun getJSON(): String {
        return Gson().toJson(this)
    }

}
