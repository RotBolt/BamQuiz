package io.kaendagger.bamquiz.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class QuizApiClient (private val serverUrl:String){

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(serverUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val quizeService = retrofit.create(QuizService::class.java)
}