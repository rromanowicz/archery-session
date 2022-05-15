package ex.rr.archerysession.data

enum class Target(val description: String, val pointRange: IntRange) {
    CM40("40cm", 6..10),
    CM60("60cm", 6..10),
    CM80("80cm", 5..10),
    CM122("122cm", 1..10),
    CUSTOM("Custom", 1..10),
    HIT_MISS("0/1", 0..1);

    companion object {
        private val map = values().associateBy(Target::description)
        fun fromDesc(description: String) = map[description]
    }
}
