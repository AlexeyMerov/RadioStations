package com.alexeymerov.radiostations.core.data.mapper.media

import com.alexeymerov.radiostations.core.common.httpsEverywhere
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import com.alexeymerov.radiostations.core.remote.response.MediaBody
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MediaMapperTest {

    private lateinit var mediaMapper: MediaMapper

    @Before
    fun setup() {
        mediaMapper = MediaMapperImp()
    }

    @Test
    fun `map media return entity with id = 0`() = runTest {
        val categoryEntity = CategoryEntity(
            type = EntityItemType.AUDIO,
            id = "id",
            position = 0,
            url = "",
            parentUrl = "",
            text = ""
        )

        val mediaBody = MediaBody(url = "")

        val resultEntity = mediaMapper.mapToEntity(categoryEntity, mediaBody)

        assertThat(resultEntity.id).isEqualTo(0)
    }

    @Test
    fun `map valid media return valid entity`() = runTest {
        val stationUrl = "https://somestationurl.com"
        val directMediaUrl = "http://someurl.com"
        val imageUrl = "https://someimageurl.com/image.jpg"
        val stationName = "Some Name"
        val subTitle = "Some Subtitle"

        val categoryEntity = CategoryEntity(
            position = 0,
            id = "id",
            parentUrl = "",
            type = EntityItemType.AUDIO,

            url = stationUrl,
            text = stationName,
            image = imageUrl,
            subTitle = subTitle
        )

        val mediaBody = MediaBody(url = directMediaUrl)

        val resultEntity = mediaMapper.mapToEntity(categoryEntity, mediaBody)

        assertThat(resultEntity.url).isEqualTo(stationUrl)
        assertThat(resultEntity.directMediaUrl).isEqualTo(directMediaUrl.httpsEverywhere())
        assertThat(resultEntity.imageUrl).isEqualTo(imageUrl)
        assertThat(resultEntity.title).isEqualTo(stationName)
        assertThat(resultEntity.subtitle).isEqualTo(subTitle)
    }

    @Test
    fun `map empty or invalid subtitle media return empty string`() = runTest {
        val categoryEntity1 = CategoryEntity(
            position = 0,
            id = "id",
            parentUrl = "",
            type = EntityItemType.AUDIO,
            url = "",
            text = "",
        )

        val categoryEntity2 = CategoryEntity(
            position = 0,
            id = "id",
            parentUrl = "",
            type = EntityItemType.AUDIO,
            url = "",
            text = "",

            subTitle = ""
        )

        val mediaBody = MediaBody(url = "")

        val resultEntity1 = mediaMapper.mapToEntity(categoryEntity1, mediaBody)
        val resultEntity2 = mediaMapper.mapToEntity(categoryEntity2, mediaBody)

        assertThat(resultEntity1.subtitle).isEmpty()
        assertThat(resultEntity2.subtitle).isEmpty()
    }

}