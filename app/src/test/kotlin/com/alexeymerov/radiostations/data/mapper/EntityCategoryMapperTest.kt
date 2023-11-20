package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.data.local.db.entity.EntityItemType
import com.alexeymerov.radiostations.data.mapper.category.CategoryMapperImpl
import com.alexeymerov.radiostations.data.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.data.remote.response.CategoryBody
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.random.Random

@RunWith(JUnit4::class)
class EntityCategoryMapperTest {

    private val categoryMapper = CategoryMapperImpl()

    @Test
    fun `map empty category list`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()
        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        assert(resultList.isEmpty())
    }

    @Test
    fun `map categories with only text param`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()
        val firstBody = CategoryBody(text = "Body text")
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        assert(resultList.isEmpty())
    }

    @Test
    fun `map category body`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()

        val text = "Body text"
        val httpUrl = "http://link.com"
        val httpsUrl = "https://link.com"
        val firstBody = CategoryBody(
            text = text,
            url = httpUrl,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = null
        )
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val entity = resultList.first()
        assert(entity.type == EntityItemType.CATEGORY)
        assert(entity.position == 0)
        assert(entity.parentUrl == parentUrl)
        assert(entity.url == httpsUrl)
        assert(entity.text == text)
        assert(entity.currentTrack == String.EMPTY)
        assert(entity.image == String.EMPTY)
        assert(entity.childCount == null)
    }

    @Test
    fun `Http To Https`() = runTest {
        val parentUrl = "http://parentlink.com"
        val sourceList = mutableListOf<CategoryBody>()
        val firstBody = CategoryBody(text = "Body text", url = "htp://broken(link..3com")
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        assert(resultList.first().url.matches(NetworkDefaults.REGEX_VALID_URL).not())
        assert(resultList.first().parentUrl.matches(NetworkDefaults.REGEX_VALID_URL))
    }

    @Test
    fun `map header with child list`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()

        val text = "Body text"
        val httpUrl = "http://link.com"
        val httpsUrl = "https://link.com"

        val childrenBody = CategoryBody(
            text = text,
            url = httpUrl,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = null
        )

        val firstBody = CategoryBody(
            text = text,
            url = httpUrl,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = listOf(childrenBody, childrenBody, childrenBody)
        )
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val entity = resultList.first()
        assert(entity.type == EntityItemType.HEADER)
        assert(entity.position == 0)
        assert(entity.parentUrl == parentUrl)
        assert(entity.url == httpsUrl)
        assert(entity.text == text)
        assert(entity.currentTrack == String.EMPTY)
        assert(entity.image == String.EMPTY)
        assert(entity.childCount == 3)
    }

    @Test
    fun `map child list with empty header`() = runTest {
        val parentUrl = "SomeParentUrl"
        val sourceList = mutableListOf<CategoryBody>()

        val firstBody = CategoryBody(
            text = "Body text",
            url = String.EMPTY,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = emptyList()
        )
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        assert(resultList.isEmpty())
    }

    @Test
    fun `map subcategories body`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()

        val text = "Body text"
        val httpUrl = "http://link.com"
        val totalChildCount = 3
        val bodyChildList = mutableListOf<CategoryBody>()

        repeat(totalChildCount) {
            val child = CategoryBody(
                text = text,
                url = httpUrl,
                type = NetworkDefaults.TYPE_LINK,
                image = String.EMPTY,
                currentTrack = String.EMPTY,
                children = null
            )
            bodyChildList.add(child)
        }

        val firstBody = CategoryBody(
            text = text,
            url = httpUrl,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = bodyChildList
        )
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val childList = resultList.filter { it.type == EntityItemType.SUBCATEGORY }
        val isAllSubcategories = childList.all { it.type == EntityItemType.SUBCATEGORY }

        assert(childList.size == totalChildCount)
        assert(isAllSubcategories)
    }

    @Test
    fun `map audios body`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()

        val text = "Body text"
        val httpUrl = "http://link.com"
        val totalChildCount = 3
        val bodyChildList = mutableListOf<CategoryBody>()

        repeat(totalChildCount) {
            val child = CategoryBody(
                text = text,
                url = httpUrl,
                type = NetworkDefaults.TYPE_AUDIO,
                image = String.EMPTY,
                currentTrack = String.EMPTY,
                children = null
            )
            bodyChildList.add(child)
        }

        val firstBody = CategoryBody(
            text = text,
            url = httpUrl,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = bodyChildList
        )
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val childList = resultList.filter { it.type == EntityItemType.AUDIO }
        val isAllSubcategories = childList.all { it.type == EntityItemType.AUDIO }

        assert(childList.size == totalChildCount)
        assert(isAllSubcategories)
    }

    @Test
    fun `maps child list with broken body`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()

        val text = "Body text"
        val httpUrl = "http://link.com"
        val totalChildCount = 5
        val bodyChildList = mutableListOf<CategoryBody>()

        repeat(totalChildCount) {
            val child = CategoryBody(
                text = text,
                url = httpUrl,
                type = if (Random.nextBoolean()) NetworkDefaults.TYPE_AUDIO else NetworkDefaults.TYPE_LINK,
                image = String.EMPTY,
                currentTrack = String.EMPTY,
                children = null
            )
            bodyChildList.add(child)
        }

        val brokenSubcategory = CategoryBody(
            text = String.EMPTY, //broken part
            url = "httpUrl",
            type = NetworkDefaults.TYPE_LINK,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = null
        )
        bodyChildList.add(brokenSubcategory)

        val brokenAudio = CategoryBody(
            text = text,
            url = "httpUrl", //broken part
            type = NetworkDefaults.TYPE_AUDIO,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = null
        )
        bodyChildList.add(brokenAudio)

        val firstBody = CategoryBody(
            text = text,
            url = httpUrl,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = bodyChildList
        )
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val childList = resultList.filter { it.type == EntityItemType.AUDIO || it.type == EntityItemType.SUBCATEGORY }
        val isAllChildren = childList.all { it.type == EntityItemType.AUDIO || it.type == EntityItemType.SUBCATEGORY }

        assert(childList.size == totalChildCount)
        assert(isAllChildren)
    }

}
