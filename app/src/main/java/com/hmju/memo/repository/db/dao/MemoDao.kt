package com.hmju.memo.repository.db.dao

import androidx.room.*
import com.hmju.memo.repository.db.dto.Memo
import io.reactivex.Single

/**
 * Description :
 *
 * Created by juhongmin on 12/17/20
 */
@Dao
interface MemoDao {

    @Insert
    fun insertMemo(memo: Memo) : Long

    @Update
    fun updateMemo(memo: Memo) : Int

    @Query("SELECT * FROM MEMO")
    fun fetchMemoList(): Single<List<Memo>>

    @Query("DELETE FROM MEMO WHERE manageNo = :manageNo")
    fun deleteMemo(manageNo: Int) : Int
}