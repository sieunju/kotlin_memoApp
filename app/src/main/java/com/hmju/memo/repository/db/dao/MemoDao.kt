package com.hmju.memo.repository.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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
    fun insert(memo: Memo)

    @Query("SELECT * FROM MEMO")
    fun fetchMemoList() : Single<Memo>
}