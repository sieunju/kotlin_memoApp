package com.hmju.memo

import com.hmju.memo.repository.preferences.LocationPref
import com.hmju.memo.repository.preferences.getLatitude
import org.junit.Assert
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Description :
 *
 * Created by hmju on 2020-06-18
 */
class ValidTest {

    @Test
    fun stringToDouble(){

        Assert.assertEquals(0.0, getLatitude("0.0"),0.001)
    }
}