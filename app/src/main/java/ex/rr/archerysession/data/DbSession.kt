package ex.rr.archerysession.data

import com.google.gson.annotations.SerializedName

data class DbSession(
    @SerializedName("sessionId")
    val id: Long,
    @SerializedName("sessionDetails")
    val session: Session
)
