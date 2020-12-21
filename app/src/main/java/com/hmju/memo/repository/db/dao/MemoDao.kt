package com.hmju.memo.repository.db.dao

import androidx.room.*
import com.hmju.memo.repository.db.dto.Memo
import com.hmju.memo.repository.db.dto.MemoImage
import io.reactivex.Single

/**
 * Description :
 *
 * Created by juhongmin on 12/17/20
 */
@Dao
interface MemoDao {

    @Insert
    fun insertMemo(memo: Memo): Single<Long>

    @Update
    fun updateMemo(memo: Memo): Single<Int>

    @Query("DELETE FROM MEMO WHERE manageNo = :manageNo")
    fun deleteMemo(manageNo: Int): Single<Int>

    @Insert
    fun insertMemoImage(memoImage: MemoImage): Long

    @Query("DELETE FROM MEMO_IMG WHERE manageNo = :manageNo")
    fun deleteMemoImage(manageNo: Int): Single<Int>

    @Query("SELECT * FROM MEMO")
    fun fetchMemoList(): Single<List<Memo>>
}