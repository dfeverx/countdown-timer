/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityVM @Inject constructor() : ViewModel() {
    val timmerIdle = mutableStateOf(true)

    val time: LiveData<TickTime> get() = _time
    private val _time: MutableLiveData<TickTime>
        by lazy {
        MutableLiveData<TickTime>()
    }
    private var timer: CountDownTimer? = null

    fun startCountDown(h: Int, m: Int, s: Int) {
        timmerIdle.value = false

        val inMilli = (s * 1000 + m * 60 * 1000 + h * 60 * 60 * 1000).toLong()
        timer = object : CountDownTimer(inMilli, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val inSec = millisUntilFinished / 1000
                val split = splitToComponentTimes(inSec)

                _time.postValue(
                    TickTime(
                        split[0],
                        split[1],
                        split[2]
                    )
                )
            }

            override fun onFinish() {
                _time.postValue(TickTime(0, 0, 0))
                timmerIdle.value = true
            }
        }
        timer?.start()
    }

    fun stopCountDown() {
        timer?.cancel()
        timmerIdle.value = true
        _time.postValue(TickTime(0, 0, 0))
    }

    fun pauseCountDown() {
        timer?.cancel()
        timmerIdle.value = true
    }
}

fun splitToComponentTimes(biggy: Long): IntArray {
    val longVal: Long = biggy
    val hours = longVal.toInt() / 3600
    var remainder = longVal.toInt() - hours * 3600
    val mins = remainder / 60
    remainder -= mins * 60
    val secs = remainder
    return intArrayOf(hours, mins, secs)
}

class TickTime(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
)
