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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mainViewModel: ActivityVM by viewModels()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val time = mainViewModel.time.asFlow().collectAsState(initial = null)
            MyTheme {
                MyApp(mainViewModel, time)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun MyApp(mainViewModel: ActivityVM, time: State<TickTime?>) {
    val scope = rememberCoroutineScope()
    val hourState: LazyListState = rememberLazyListState()
    val minuteState: LazyListState = rememberLazyListState()
    val secState: LazyListState = rememberLazyListState()

    time.value?.let {
        scope.launch { hourState.scrollToItem(it.hours) }
        scope.launch { minuteState.scrollToItem(it.minutes) }
        scope.launch { secState.scrollToItem(it.seconds) }
    }

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.background)
                .padding(top = 16.dp)
        ) {
            Column(Modifier.weight(1f)) {

                TimeLine(13, stringResource(R.string.hours), hourState)
                TimeLine(60, stringResource(R.string.minutes), minuteState)
                TimeLine(60, stringResource(R.string.seconds), secState)
            }
            /*Fixed Layout*/
            FixedBottom(
                mainViewModel.timmerIdle.value,
                {
                    val h = hourState.firstVisibleItemIndex
                    val m = minuteState.firstVisibleItemIndex
                    val s = secState.firstVisibleItemIndex
                    mainViewModel.startCountDown(h, m, s)
                },
                {
                    mainViewModel.pauseCountDown()
                },
                {
                    mainViewModel.stopCountDown()
                }
            )
        }
    }
}

@Composable
fun FixedBottom(idle: Boolean = true, start: () -> Unit, pause: () -> Unit, stop: () -> Unit) {
    Row(
        modifier = Modifier

    ) {
        Box(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .background(
                    Color.Green,
                    RoundedCornerShape(
                        topStart = 150.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                )
                .size(200.dp)
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!idle) {
                Icon(
                    imageVector = Icons.Filled.StopCircle,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(50.dp)
                        .clickable { stop() }
                )
            }

            if (idle) {
                Icon(
                    imageVector = Icons.Filled.PlayCircleOutline,
                    contentDescription = null,
                    tint = /*MaterialTheme.colors.background*/Color.Black,
                    modifier = Modifier
                        .clickable { start() }
                        .size(150.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.PauseCircleOutline,
                    contentDescription = null,
                    tint = /*MaterialTheme.colors.background*/Color.Black,
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { pause() }
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimeLine(count: Int, label: String, state: LazyListState, isEnabled: Boolean = true) {

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 16.dp)
        )

        LazyRow(
            state = state,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 0.dp, end = 472.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            items(count) { index ->

                when (index) {
                    state.firstVisibleItemIndex -> {
                        Text(
                            text = "${if (index < 10) "0$index" else index}",
                            Modifier
                                .padding(vertical = 0.dp)
                                .padding(start = 32.dp),
                            style = MaterialTheme.typography.h1,

                        )
                    }
                    state.firstVisibleItemIndex + 1 -> {
                        Text(
                            text = "${if (index < 10) "0$index" else index}",
                            Modifier
                                .padding(vertical = 0.dp)
                                .padding(start = 16.dp),
                            style = MaterialTheme.typography.h2,
                            color = Color.LightGray
                        )
                    }
                    state.firstVisibleItemIndex + 2 -> {
                        Text(
                            text = "${if (index < 10) "0$index" else index}",
                            Modifier
                                .padding(vertical = 0.dp)
                                .padding(start = 16.dp),
                            style = MaterialTheme.typography.h3,
                            color = Color.LightGray
                        )
                    }
                    else -> {
                        Text(
                            text = "${if (index < 10) "0$index" else index}",
                            Modifier
                                .padding(vertical = 0.dp)
                                .padding(start = 16.dp),
                            style = MaterialTheme.typography.h4,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}
