package com.alexeymerov.radiostations.core.data.mapper.country

import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.alexeymerov.radiostations.core.remote.response.CountryIdd
import com.alexeymerov.radiostations.core.remote.response.CountryName
import com.alexeymerov.radiostations.core.remote.response.CountryNativeName
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CountryMapperTest {

    private lateinit var countryMapper: CountryMapper

    private val correctNativeName = CountryNativeName(
        official = "United Kingdom of Great Britain and Northern Ireland",
        common = "United Kingdom"
    )

    private val correctName = CountryName(
        common = "United Kingdom",
        official = "United Kingdom of Great Britain and Northern Ireland",
        nativeName = mapOf("eng" to correctNativeName)
    )

    private val correctIdd = CountryIdd(
        root = "+4",
        suffixes = listOf("4")
    )

    private val correctCca2 = "GB"

    @Before
    fun setup() {
        countryMapper = CountryMapperImpl()
    }

    @Test
    fun `map empty list return empty list`() = runTest {
        val sourceList = emptyList<CountryBody>()
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).isEmpty()
    }

    @Test
    fun `map list with correct params return valid list`() = runTest {
        val countryBody = CountryBody(
            name = correctName,
            idd = correctIdd,
            cca2 = correctCca2
        )

        val sourceList = listOf(countryBody)
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(1)
    }

    @Test
    fun `map list with empty or incorrect cca2 return empty list`() = runTest {
        val sourceList = listOf(
            CountryBody(
                name = correctName,
                idd = correctIdd,
                cca2 = ""
            ),
            CountryBody(
                name = correctName,
                idd = correctIdd,
                cca2 = "12"
            ),
            CountryBody(
                name = correctName,
                idd = correctIdd,
                cca2 = "  "
            ),
            CountryBody(
                name = correctName,
                idd = correctIdd,
                cca2 = "GBGB"
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(0)
    }

    @Test
    fun `map list with empty or incorrect idd root return empty list`() = runTest {
        val sourceList = listOf(
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = "",
                    suffixes = correctIdd.suffixes
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = " ",
                    suffixes = correctIdd.suffixes
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = "4",
                    suffixes = correctIdd.suffixes
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = "+a",
                    suffixes = correctIdd.suffixes
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = "4+",
                    suffixes = correctIdd.suffixes
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = "++4",
                    suffixes = correctIdd.suffixes
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = "+",
                    suffixes = correctIdd.suffixes
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = "4+4",
                    suffixes = correctIdd.suffixes
                )
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(0)
    }

    @Test
    fun `map list with empty or incorrect suffixes return only root param`() = runTest {
        val sourceList = listOf(
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = correctIdd.root,
                    suffixes = emptyList()
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = correctIdd.root,
                    suffixes = listOf("")
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = correctIdd.root,
                    suffixes = listOf(" ")
                )
            ),
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = correctIdd.root,
                    suffixes = listOf("f")
                )
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(4)
        resultList.forEach {
            assertThat(it.phoneCode).isEqualTo(correctIdd.root)
        }
    }

    @Test
    fun `map list with single suffix return only root + suffix`() = runTest {
        val sourceList = listOf(
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = correctIdd.root,
                    suffixes = listOf("4")
                )
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(1)
        assertThat(resultList.first().phoneCode).isEqualTo(correctIdd.root + "4")
    }

    @Test
    fun `map list with multiple suffixes return only root param`() = runTest {
        val sourceList = listOf(
            CountryBody(
                name = correctName,
                cca2 = correctCca2,
                idd = CountryIdd(
                    root = correctIdd.root,
                    suffixes = listOf("1", "2", "3")
                )
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(1)
        assertThat(resultList.first().phoneCode).isEqualTo(correctIdd.root)
    }

    @Test
    fun `map list with empty native name return empty native string`() = runTest {
        val name = CountryName(
            common = correctName.common,
            official = correctName.official,
            nativeName = emptyMap()
        )

        val sourceList = listOf(
            CountryBody(
                name = name,
                cca2 = correctCca2,
                idd = correctIdd
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(1)
        assertThat(resultList.first().nameNative).isEmpty()
    }

    @Test
    fun `map list with empty common native name return official native name`() = runTest {
        val nativeName = CountryNativeName(
            official = correctNativeName.official,
            common = ""
        )

        val name = CountryName(
            common = correctName.common,
            official = correctName.official,
            nativeName = mapOf("eng" to nativeName)
        )

        val sourceList = listOf(
            CountryBody(
                name = name,
                cca2 = correctCca2,
                idd = correctIdd
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(1)
        assertThat(resultList.first().nameNative).isEqualTo(correctNativeName.official)
    }

    @Test
    fun `map list with empty official native name return empty string`() = runTest {
        val nativeName = CountryNativeName(
            official = "",
            common = correctNativeName.common
        )

        val name = CountryName(
            common = correctName.common,
            official = correctName.official,
            nativeName = mapOf("eng" to nativeName)
        )

        val sourceList = listOf(
            CountryBody(
                name = name,
                cca2 = correctCca2,
                idd = correctIdd
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(1)
        assertThat(resultList.first().nameNative).isEmpty()
    }

    @Test
    fun `map list with empty common name return official name`() = runTest {
        val nativeName = CountryNativeName(
            official = correctNativeName.official,
            common = correctNativeName.common
        )

        val name = CountryName(
            common = "",
            official = correctName.official,
            nativeName = mapOf("eng" to nativeName)
        )

        val sourceList = listOf(
            CountryBody(
                name = name,
                cca2 = correctCca2,
                idd = correctIdd
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(1)
        assertThat(resultList.first().nameEnglish).isEqualTo(correctName.official)
    }

    @Test
    fun `map list with empty official name return empty list`() = runTest {
        val nativeName = CountryNativeName(
            official = correctNativeName.official,
            common = correctNativeName.common
        )

        val name = CountryName(
            common = correctName.common,
            official = "",
            nativeName = mapOf("eng" to nativeName)
        )

        val sourceList = listOf(
            CountryBody(
                name = name,
                cca2 = correctCca2,
                idd = correctIdd
            )
        )
        val resultList = countryMapper.mapCountries(sourceList)

        assertThat(resultList).hasSize(0)
    }

}