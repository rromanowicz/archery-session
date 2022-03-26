package ex.rr.archerysession

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ex.rr.archerysession.data.Session

class SharedViewModel : ViewModel() {
    var scores = MutableLiveData<MutableList<Int>?>()
    var session = MutableLiveData<Session?>()
    var sessionId = MutableLiveData<String?>()
    var reloadHistory = MutableLiveData<Boolean?>()
    var endSession = MutableLiveData<Boolean?>()
    var newBow = MutableLiveData<Int?>()

    fun sendScores(values: MutableList<Int>) {
        scores.value = values
    }

    fun sendSession(value: Session) {
        session.value = value
    }

    fun sendSessionId(value: String) {
        sessionId.value = value
    }

    fun setReloadHistory() {
        reloadHistory.value = reloadHistory.value == null || reloadHistory.value == false
    }

    fun sendEndSession() {
        endSession.value = endSession.value == null || endSession.value == false
    }

    fun sendNewBow(value: Int){
        newBow.value = value
    }

    fun clear() {
        scores.value?.clear()
        session.value?.clear()
        sessionId.value = null
        reloadHistory.value = null
        endSession.value = null
        newBow.value = null
    }
}