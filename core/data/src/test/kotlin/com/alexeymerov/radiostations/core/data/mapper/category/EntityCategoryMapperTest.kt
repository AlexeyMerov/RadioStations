package com.alexeymerov.radiostations.core.data.mapper.category

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.random.Random

@RunWith(JUnit4::class)
class EntityCategoryMapperTest {

    private lateinit var categoryMapper: CategoryMapper

    @Before
    fun setup() {
        categoryMapper = CategoryMapperImpl()
    }

    @Test
    fun `map empty list return empty list`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = mutableListOf<CategoryBody>()
        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)

        assertThat(resultList).isEmpty()
    }

    @Test
    fun `map valid data return valid list`() = runTest {
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

        assertThat(entity.type).isEqualTo(EntityItemType.CATEGORY)
        assertThat(entity.position).isEqualTo(0)
        assertThat(entity.parentUrl).isEqualTo(parentUrl)
        assertThat(entity.url).isEqualTo(httpsUrl)
        assertThat(entity.text).isEqualTo(text)
        assertThat(entity.currentTrack).isEmpty()
        assertThat(entity.image).isEmpty()
        assertThat(entity.childCount).isNull()
    }

    @Test
    fun `map with invalid url will fail`() = runTest {
        val parentUrl = "http://parentlink.com"
        val sourceList = listOf(CategoryBody(text = "Body text", url = "htp://broken(link..3com"))

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val entity = resultList.first()

        assertThat(entity.url).doesNotMatch(NetworkDefaults.REGEX_VALID_URL.pattern)
    }

    @Test
    fun `map with child list return entity with children and header type`() = runTest {
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

        val childrenList = listOf(childrenBody, childrenBody, childrenBody)
        val firstBody = CategoryBody(
            text = text,
            url = httpUrl,
            type = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            children = childrenList
        )
        sourceList.add(firstBody)

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val entity = resultList.first()

        assertThat(entity.type).isEqualTo(EntityItemType.HEADER)
        assertThat(entity.position).isEqualTo(0)
        assertThat(entity.parentUrl).isEqualTo(parentUrl)
        assertThat(entity.url).isEqualTo(httpsUrl)
        assertThat(entity.text).isEqualTo(text)
        assertThat(entity.currentTrack).isEmpty()
        assertThat(entity.image).isEmpty()
        assertThat(entity.childCount).isEqualTo(childrenList.size)
    }

    @Test
    fun `map with empty child list return empty list`() = runTest {
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

        assertThat(resultList).isEmpty()
    }

    @Test
    fun `map child list with type link return valid subcategories list`() = runTest {
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

        assertThat(childList).hasSize(totalChildCount)
        assertThat(isAllSubcategories).isTrue()
    }

    @Test
    fun `map child list with type audio return valid audio list`() = runTest {
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

        assertThat(childList).hasSize(totalChildCount)
        assertThat(isAllSubcategories).isTrue()
    }

    @Test
    fun `maps child list with broken body return only valid items`() = runTest {
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

        assertThat(childList).hasSize(totalChildCount)
        assertThat(isAllChildren).isTrue()
    }

    @Test
    fun `map audio item with subtitle in brackets return entity with title and subtitle`() = runTest {
        val parentUrl = "http://parenturl.com"
        val title = "Title"
        val subtitle = "Subtitle"
        val text = "$title ($subtitle)"
        val httpUrl = "http://link.com"

        val sourceList = listOf(
            CategoryBody(
                text = text,
                url = httpUrl,
                type = NetworkDefaults.TYPE_AUDIO,
                image = String.EMPTY,
                currentTrack = String.EMPTY,
            )
        )

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val entity = resultList.first()

        assertThat(entity.text).isEqualTo(title)
        assertThat(entity.subTitle).isEqualTo(subtitle)
    }

    @Test
    fun `map item with child size in brackets return entity with clean title and child count param`() = runTest {
        val parentUrl = "http://parenturl.com"
        val title = "Title"
        val text = "$title (4)"
        val httpUrl = "http://link.com"

        val children = listOf(
            CategoryBody(text = "1", url = "http://link.com"),
            CategoryBody(text = "2", url = "http://link.com"),
            CategoryBody(text = "3", url = "http://link.com"),
        )
        val sourceList = listOf(
            CategoryBody(
                text = text,
                url = httpUrl,
                type = NetworkDefaults.TYPE_AUDIO,
                image = String.EMPTY,
                currentTrack = String.EMPTY,
                children = children
            )
        )

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)
        val entity = resultList.first()

        assertThat(entity.text).isEqualTo(title)
        assertThat(entity.childCount).isEqualTo(children.size)
    }

    @Test
    fun `map with invalid url and children will fail`() = runTest {
        val parentUrl = "http://parenturl.com"

        val sourceList = listOf(
            CategoryBody(
                text = "text",
                url = null,
                children = null
            ),
            CategoryBody(
                text = "text",
                url = "",
                children = emptyList()
            )
        )

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)

        assertThat(resultList).hasSize(0)
    }

    @Test
    fun `map children with invalid url and children will fail`() = runTest {
        val parentUrl = "http://parenturl.com"

        val children = listOf(
            CategoryBody(text = "1", url = null),
            CategoryBody(text = "2", url = "parenturl.com"),
        )

        val sourceList = listOf(
            CategoryBody(
                text = "text",
                url = parentUrl,
                children = children
            )
        )

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)

        assertThat(resultList.first().childCount).isEqualTo(0)
    }

    @Test
    fun `map with invalid parentUrl return empty list`() = runTest {
        val parentUrl = "parenturl.com"
        val sourceList = listOf(CategoryBody(text = "Some text"))

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)

        assertThat(resultList).isEmpty()
    }

    @Test
    fun `map with empty text return empty list`() = runTest {
        val parentUrl = "http://parenturl.com"
        val sourceList = listOf(CategoryBody(text = ""))

        val resultList = categoryMapper.mapCategoryResponseToEntity(sourceList, parentUrl)

        assertThat(resultList).isEmpty()
    }
}
