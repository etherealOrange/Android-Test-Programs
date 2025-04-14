package com.example.filetest

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        flow {emit("a")
            delay(100)
            emit("b") }.flatMapLatest {
            value ->
                flow {
                    emit(value)
                    delay(200)
                    emit(value + "_last")
                }
            }
    }
}