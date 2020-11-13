package com.hmju.memo

import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.DecimalFormat
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

    fun bindingTest(images: String?) {
        images?.let { imgs ->
            try {
                val array = GsonBuilder().create().fromJson(imgs, ArrayList<String>()::class.java)
                println("Array ${array[0]}")
            } catch (e: Exception) {
                println(e.message)
            }

        }
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

    @Test
    fun userAgentMatchesTest() {
        val userAgent =
            "Mozilla/5.0 (Linux; Android 10; SM-G975N Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/80.0.3987.99 Mobile Safari/537.36/APTRACK_ANDROID"
        val pattern = "^[a-zA-Z]*\$"
        if (userAgent.contains("Android")) {
            println("Android 입니다.")
        } else {
            println("아닙니다.")
        }
    }

    @Test
    fun setRegDtm() {
        val regDtm = "2020-08-09 23:26:29"

    }

    data class Struct(
        var id: String,
        var age: Int
    )

    @Test
    fun testLet() {
        // let 블럭안에서는 Non-Null만 들어올수 있어서 non-null 체크시 유용하게 사용할수 있고,
        // 만약 객체를 선언하는 상황인 경우 엘비스 오퍼레이터를 사용해서 기본값을 지정할수 있습니다.
        // 필요에 따라서 리턴을 지정할수 있고 객체 상태를 변경할수 있습니다. ex.) Data Class를 let사용해서
        // 안에 받으면 리턴값을 String 으로 변경할수 있습니다.
        // 하지만 무분별하게 let 을사용하면 ByteCode로 Decompile 할때 기존 자바보다 길어질수 있으니
        // 유의해서 사용해야 한다...
        var temp : Struct? = Struct("hi", 10)

        val resultFirst = temp?.let {
            it.id = "Hello"
            it.age = 10
            it
        }

        val resultStr = temp?.let {
            it.id = "Result Str"
            it.age = 12
            "${it.id} is ${it.age}"
        }

        val resultUnit = temp?.let {
            it.id = "Result Unit"
            it.age = 13
        }

        temp = null

        val idStr = temp?.let { it.id } ?: "Nullaa"

        println(resultFirst)
        println(resultStr)
        println(resultUnit)
        println(idStr)

        val temp1 : Struct? = Struct("qq",11)

    }

    @Test
    fun testWith() {
        val temp = Struct("hi", 10)
        // With 의 경우 non-null 객체를 사용하고 블럭의 리턴값이 필요하지 않을때 사용된다.
        // 그래서 주로 객체의 함수를 여러 개 호출할때 그룹화 하는 용도로 활용된다..
        with(temp){
            println(id)
            println(age)
        }
    }

    @Test
    fun testRun(){
        val temp : Struct? = Struct("hi", 10)
        // Run은 With 와 비슷한 구조이지만 확장 함수이기때문에 safe call (.?) 를 붙여서
        // non-null 형태로 실행할수 있습니다. 어떤 값을 계산할 필요가 있을때 사용됩니다.
        // 확장함수가 아닌 형태의 run 을 구성할수 있는데 이때는 객체를 변경해서 리턴할수 없는 형태이기때문에
        // 그러한 용도는 아니고, 어떤 객체를 생성하기 위해 명령문을 블럭 안에 묶음으로써 가독성을 높이는 역활을 합니다.
        val result = temp?.run {
            ++age
            val ran = Random().nextInt(10)
            println("Ran $ran")
            age += ran
            "$id and $age"
        }

        val result1 = run {
            val id = "aaa"
            val age = 10
            Struct(id,age)
        }


        println(result)
        println(result1)
    }

    @Test
    fun testApply(){
        val temp = Struct("",0)
        // 확장함수 이고, 블록 함수의 입력을 람다 리시버로 받았기 때문에 블럭안에서 객체 프로퍼티를 호출할떄
        // it 이나 this 를 사용할 필요가 업습니다. 그래서 run 과 비슷해 보이기는 하나 블럭에서 리턴 값을 받지
        // 않고 자기 자신 객체 형태를 반환한다는 점이 다릅니다.
        val result = temp.apply {
            id = "hello"
            age = 45
        }
    }

    @Test
    fun testAlso(){
        val temp = Struct("",0)
        // 확장함수이고 블럭함수의 입력으로 람다 리시버를 받지 않고 this 를 받습니다. apply 와 비슷하게
        // 자기 자신을 반환 한다는 점이 같습니다. 하지만 사용 용도는 객체의 데이터 유효성을 확인 하거나
        // 자기 자신을 리턴한다는 점에 Builder 패턴과 동일한 용도로 사용되기도 합니다.
        // 디버그, 로깅등의 부가적인 목적으로 사용할떄가 많습니다.
        val result = temp.also {
            it.id = "Hello"
            it.age = 45
        }

        val numbers = arrayListOf("one","two","three")
        numbers.apply {  }.add("four")

        numbers.also { println("add 하기전에 Print $it")
            it[1] = "qqq"
        }.add("four")

        println(numbers)

        println("$result")
    }

    @Test
    fun testComma() {
        val num = 13232
        val format = DecimalFormat("###,###")
        println("Comma ${format.format(num)}")
    }
}
