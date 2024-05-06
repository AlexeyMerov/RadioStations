package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.CountriesQuery
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.alexeymerov.radiostations.core.remote.response.MainBody
import com.apollographql.apollo3.api.ApolloResponse
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class ResponseMapperImpl @Inject constructor() : ResponseMapper {

    //no error handling at the moment since uncertainty of server errors and response format
    override fun <T> mapRadioResponseBody(body: Response<MainBody<T>>): List<T> {
        var errorText: String? = null
        var resultList = emptyList<T>()
        val mainBody = body.body()
        when {
            !body.isSuccessful -> errorText = body.message()
            mainBody == null -> errorText = "Response body is null"
            mainBody.head.status != STATUS_OK -> errorText = mainBody.head.title ?: "Response status: ${mainBody.head.status}"
            else -> resultList = mainBody.body
        }

        if (errorText != null) Timber.d("mapRadioResponseBody $errorText")

        return resultList
    }

    override fun mapCountriesResponseBody(response: ApolloResponse<CountriesQuery.Data>): List<CountryBody> {
        var resultList: List<CountryBody> = emptyList()

        val countries = response.data?.countries
        when {
            response.hasErrors() -> Timber.e(response.errors.toString())
            countries == null -> Timber.e("Response data is null")
            else -> resultList = countries.map {
                CountryBody(
                    countryCode = it.code,
                    nameEnglish = it.name,
                    nameNative = it.native,
                    phoneCode = it.phone
                )
            }
        }

        return resultList
    }

    companion object {
        const val STATUS_OK = "200"
    }
}