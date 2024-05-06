package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.CountriesQuery
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.alexeymerov.radiostations.core.remote.response.MainBody
import com.apollographql.apollo3.api.ApolloResponse
import retrofit2.Response

interface ResponseMapper {

    fun <T> mapRadioResponseBody(body: Response<MainBody<T>>): List<T>

    fun mapCountriesResponseBody(response: ApolloResponse<CountriesQuery.Data>): List<CountryBody>

}