package com.hmju.memo.model.gallery

/**
 * Description : 갤럴리 필터 데이터 모델 클래스.
 * @param bucketId 앨범 아이디값
 * @param bucketName 앨범 이름
 * @param photoUri 첫번쨰 사진값
 * @param count 해당 앨범 총 개수
 *
 * Created by juhongmin on 2020/09/24
 */
data class GalleryFilterItem(
    val bucketId: String,
    val bucketName: String,
    val photoUri: String,
    val count: Int
) {
    override fun equals(other: Any?): Boolean {
        return if (other is GalleryFilterItem) {
            other.bucketId == bucketId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = bucketId.hashCode()
        result = 31 * result + bucketName.hashCode()
        result = 31 * result + count.hashCode()
        result = 31 * result + (photoUri.hashCode() ?: 0)
        return result
    }
}