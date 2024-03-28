package com.alexeymerov.radiostations.core.remote.client.radio


import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.ProjectConst
import com.alexeymerov.radiostations.core.remote.client.bodyOrNull
import com.alexeymerov.radiostations.core.remote.di.Backend
import com.alexeymerov.radiostations.core.remote.di.Server
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.alexeymerov.radiostations.core.remote.response.MediaBody
import com.alexeymerov.radiostations.core.remote.response.RadioMainBody
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import timber.log.Timber
import javax.inject.Inject

class RadioClientImpl @Inject constructor(
    @Backend(Server.Radio) private val httpClient: HttpClient,
    private val responseMapper: ResponseMapper,
) : RadioClient {

    /**
     *  There is an inconsistency with IDs and params and the reliable way is to use links from responses
     *  But since server operates with links, we have to remove BASE_URL part before requests.
     *  Example of the issue: 3 different items from responses below
     *  ?c=music (AND) "key": "music"
     *  ?id=r0 (AND) "key": "location"
     *  ?id=c57943 (AND) "guide_id": "c57943"
     *
     *     {
     *       "element": "outline",
     *       "type": "link",
     *       "text": "Music",
     *       "URL": "http://opml.radiotime.com/Browse.ashx?c=music",
     *       "key": "music"
     *     },
     *     {
     *       "element": "outline",
     *       "type": "link",
     *       "text": "By Location",
     *       "URL": "http://opml.radiotime.com/Browse.ashx?id=r0",
     *       "key": "location"
     *     },
     *     {
     *       "element": "outline",
     *       "type": "link",
     *       "text": "Top 40 & Pop Music",
     *       "URL": "http://opml.radiotime.com/Browse.ashx?id=c57943",
     *       "guide_id": "c57943"
     *     }
     *
     * */
    override suspend fun requestCategoriesByUrl(url: String): List<CategoryBody> {
        return try {
            val finalUrl = url.replace(ProjectConst.BASE_URL, String.EMPTY)
            val response = httpClient.get(finalUrl)
            val body = response.bodyOrNull<RadioMainBody<CategoryBody>?>()
            responseMapper.mapRadioResponseBody(response, body)
        } catch (e: Exception) {
            Timber.e(e)
            emptyList()
        }
    }

    override suspend fun requestAudioById(tuneId: String): MediaBody? {
        return try {
            val response = httpClient.get(URL_PATH_TUNE) { parameter("id", tuneId) }
            val body = response.bodyOrNull<RadioMainBody<MediaBody>?>()
            responseMapper.mapRadioResponseBody(response, body).getOrNull(0)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    private companion object {
        const val URL_PATH_TUNE = "/Tune.ashx"
    }
}