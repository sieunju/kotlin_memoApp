package com.hmju.memo

import com.hmju.memo.model.memo.bindingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

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
    fun memoImagesBinding() {
        bindingTest("[\"IMG_1595897676051afk1j1b40cc.jpeg\",\"IMG_15946450230737kkbsc1gw6f.jpeg\",\"IMG_1594645115336nywr85bdph.jpeg\"]")
    }

    @Test
    fun listTest() {
        val arrayList = ArrayList<Int>()
        var time = System.currentTimeMillis()
        for (i in 1..3000000) {
            arrayList.add(i)
        }
        println("ArrayList Time\t${System.currentTimeMillis() - time}")

        time = System.currentTimeMillis()
        arrayList.add(1000000, 1000000)
        println("ArrayList Index Time\t${System.currentTimeMillis() - time}")

        time = System.currentTimeMillis()
        arrayList.get(100000)
        println("ArrayList Find Time\t${System.currentTimeMillis() - time}")

        time = System.currentTimeMillis()
        arrayList.add(1)
        println("ArrayList Normal Add Time\t${System.currentTimeMillis() - time}")

        val linkedList = LinkedList<Int>()
        time = System.currentTimeMillis()
        for (i in 1..3000000) {
            linkedList.add(i)
        }
        println("LinkedList Time\t${System.currentTimeMillis() - time}")

        time = System.currentTimeMillis()
        linkedList.add(1000000, 1000000)
        println("LinkedList Index Time\t${System.currentTimeMillis() - time}")

        time = System.currentTimeMillis()
        linkedList.get(100000)
        println("LinkedList Find Time\t${System.currentTimeMillis() - time}")

        time = System.currentTimeMillis()
        linkedList.add(1)
        println("LinkedList Normal Add Time\t${System.currentTimeMillis() - time}")
    }
}
