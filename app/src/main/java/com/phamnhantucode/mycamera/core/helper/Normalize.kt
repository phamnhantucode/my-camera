package com.phamnhantucode.mycamera.core.helper

fun normalizeToRange(
    values: Float,
    inputMin: Float,
    inputMax: Float,
    outputMin: Float,
    outputMax: Float
): Float {
    return (values - inputMin) * (outputMax - outputMin) / (inputMax - inputMin) + outputMin
}