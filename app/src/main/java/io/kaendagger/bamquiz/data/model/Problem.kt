package io.kaendagger.bamquiz.data.model

data class Problem(
    val correct:String,
    val option_a:String,
    val option_b:String,
    val option_c:String,
    val option_d:String,
    val ques:String,
    val ques_img:String?,
    val type:String
)