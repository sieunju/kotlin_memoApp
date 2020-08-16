package com.hmju.memo

import com.hmju.memo.model.memo.bindingTest
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
        assertEquals(4, 2 + 2)
    }

    @Test
    fun memoImagesBinding(){
        bindingTest("[\"IMG_1595897676051afk1j1b40cc.jpeg\",\"IMG_15946450230737kkbsc1gw6f.jpeg\",\"IMG_1594645115336nywr85bdph.jpeg\"]")
    }
}
