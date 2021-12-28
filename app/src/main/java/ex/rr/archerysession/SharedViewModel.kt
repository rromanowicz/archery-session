package ex.rr.archerysession

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ex.rr.archerysession.data.Session

class SharedViewModel : ViewModel() {
    var scores = MutableLiveData<MutableList<Int>?>()
    var session = MutableLiveData<Session?>()
    var sessionId = MutableLiveData<String?>()
    var reloadHistory = MutableLiveData<Boolean?>()

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

    fun clear() {
        scores.value?.clear()
        session.value = null
        sessionId.value = null
        reloadHistory.value = null
    }
}