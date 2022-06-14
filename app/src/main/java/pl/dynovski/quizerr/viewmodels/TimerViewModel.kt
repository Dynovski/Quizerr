package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {

    private val _timeInMillis = MutableLiveData<Long>()
    val timeInMillis: LiveData<Long> = _timeInMillis

    fun timeChanged(newTimeInMillis: Long) {
        _timeInMillis.value = newTimeInMillis
    }
}