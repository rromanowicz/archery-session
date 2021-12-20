package ex.rr.archerysession

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import ex.rr.archerysession.data.DbSession
import ex.rr.archerysession.data.Session

class SharedViewModel : ViewModel() {
    var scores = MutableLiveData<MutableList<Int>?>()
    var session = MutableLiveData<Session?>()
//    var dbSession = MutableLiveData<DbSession?>()
    var sessionId = MutableLiveData<String?>()

    fun sendScores(values: MutableList<Int>) {
        Log.e("ViewModel", values.toString()) //TODO: remove
        scores.value = values
    }

    fun sendSession(value: Session) {
        session.value = value
    }

//    fun sendDbSession(value: DbSession) {
//        dbSession.value = value
//    }

    fun sendSessionId(value: String) {
        sessionId.value = value
    }

    fun clear() {
        scores.value?.clear()
        session.value = null
        sessionId.value = null
    }
}