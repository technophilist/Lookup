package com.example.lookup.data.utils

import retrofit2.Response

/**
 * A convenience function that returns [T] if the [Response.body] is not null. If it is null.
 * an [Exception] would be thrown with the specified [exceptionMessage].
 */
// todo: make sure that this method is being utilized in all places in the data layer.
fun <T> Response<T>.getBodyOrThrowException(
    exceptionMessage: String = "An error occurred when fetching data."
): T = body() ?: throw Exception(exceptionMessage)

