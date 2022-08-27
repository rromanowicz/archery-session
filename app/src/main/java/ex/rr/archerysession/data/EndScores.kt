package ex.rr.archerysession.data

class EndScores {

    var id: Int? = null
    var endScores: MutableList<Int>? = null

    fun arrows(): Int {
        return this.endScores?.size ?: 0
    }

    fun score(): Int {
        if (!endScores.isNullOrEmpty()) {
            return endScores!!.sumOf { it }
        }
        return 0
    }

    fun clear(){
        this.id = null
        this.endScores = null
    }
}