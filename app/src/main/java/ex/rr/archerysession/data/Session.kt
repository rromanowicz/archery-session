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
    @SerializedName("scoreMap")
    var scoreMap: MutableMap<Int, MutableList<Int>> = mutableMapOf(),
    @SerializedName("endScores")
    var scores: MutableList<MutableList<Int>> = mutableListOf(),
    @SerializedName("bow")
    var bow: Bow? = null,
    @SerializedName("target")
    var target: String? = null,
    @SerializedName("distance")
    var distance: String? = null

) {

    fun mapScoresToScoreMap() {
        if (scores.isNotEmpty() && scoreMap.isEmpty()) {
            scores.forEach {
                var id: Int = scoreMap.size
                scoreMap[++id] = it
            }
        }
    }

    fun addEndScores(endScores: EndScores) {
        if (endScores.endScores!!.isNotEmpty()) {
            this.scoreMap[endScores.id!!] = endScores.endScores!!
            this.ends = getTotalEnds()
            this.arrows = getTotalArrows()
        }
    }

    fun endSession() {
        endDate = Date()
    }

    fun getTotalScore(): Int {
        var result: Int = 0
        scoreMap.forEach{ entry -> result += entry.value.sumOf { it }}
        return result
    }

    fun getTotalArrows(): Int {
        var result: Int = 0
        scoreMap.forEach{ entry -> result += entry.value.size}
        return result
    }

    fun getTotalEnds(): Int {
        return scoreMap.size
    }

    fun getJSON(): String {
        return Gson().toJson(this)
    }

    fun clear() {
        this.startDate = Date()
        this.endDate = null
        this.ends = 0
        this.arrows = 0
        this.scoreMap = mutableMapOf()
        this.bow = null
        this.target = null
        this.distance = null
    }

}
