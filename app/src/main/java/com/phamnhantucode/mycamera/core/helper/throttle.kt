package com.phamnhantucode.mycamera.core.helper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun throttle(
    wait: Long,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    action: () -> Unit,
): () -> Unit {
    var lastActionTime = 0L
    var job: Job? = null

    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastActionTime >= wait) {
            lastActionTime = currentTime
            job?.cancel()
            job = coroutineScope.launch {
                action()
            }
        }
    }
}