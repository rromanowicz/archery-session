package ex.rr.archerysession

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val scores = MutableLiveData<MutableList<Int>>()

    fun sendScores(values: MutableList<Int>){
        Log.e("ViewModel", values.toString())
        scores.value = values
    }
}