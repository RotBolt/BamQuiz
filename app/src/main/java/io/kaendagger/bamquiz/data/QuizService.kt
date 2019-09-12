package io.kaendagger.bamquiz.data

import io.kaendagger.bamquiz.data.model.Problem
import retrofit2.Response
import retrofit2.http.GET

interface QuizService {

    @GET("/")
    suspend fun getQuestions():Response<List<Problem>>
}