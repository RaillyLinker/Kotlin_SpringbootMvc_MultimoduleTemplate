package com.raillylinker.module_mongodb.mongodb_beans.mdb1_main.documents

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "test")
data class Mdb1_Test(
    @Field("content")
    var content: String,
    @Field("random_num")
    var randomNum: Int,
    @Field("nullable_value")
    var nullableValue: String?,
    @Field("row_activate")
    var rowActivate: Boolean
) {
    @Id
    var uid: String? = null

    @CreatedDate
    @Field("row_create_date")
    var rowCreateDate: LocalDateTime? = null

    @LastModifiedDate
    @Field("row_update_date")
    var rowUpdateDate: LocalDateTime? = null
}