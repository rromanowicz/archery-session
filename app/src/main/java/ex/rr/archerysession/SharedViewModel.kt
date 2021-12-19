package ex.rr.archerysession

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var scores = MutableLiveData<MutableList<Int>?>()

    fun sendScores(values: MutableList<Int>) {
        Log.e("ViewModel", values.toString()) //TODO: remove
        scores.value = values
    }

    fun clear() {
        scores.value?.clear()
    }
}