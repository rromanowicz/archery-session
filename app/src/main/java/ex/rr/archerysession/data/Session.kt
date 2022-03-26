package ex.rr.archerysession.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.*

data class Session(
    @SerializedName("startDate")
    var startDate: Date = Date(),
    @SerializedName("endDate")
    var endDate: Date? = null,
    @SerializedName("numberOfEnds")
    var ends: Int = 0,
    @SerializedName("numberOfArrows")
    var arrows: Int = 0,
    @SerializedName("endScores")
    var scores: MutableList<MutableList<Int>> = mutableListOf(),
    @SerializedName("bow")
    var bow: Bow? = null
) {

    fun addEndScores(scores: MutableList<Int>) {
        if (scores.isNotEmpty()) {
            this.ends++
            this.arrows += scores.size
            this.scores.add(scores)
        }
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

    fun clear(){
        this.startDate = Date()
        this.endDate = null
        this.ends = 0
        this.arrows = 0
        this.scores = mutableListOf()
    }

}
