package com.alexeymerov.radiostations.core.common

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExtensionsKtTest {

    @Test
    fun `title with (subtitle) extracted as pair of string`() {
        val title = "title"
        val subtitle = "subtitle"
        val (extractedTitle, extractedSubtitle) = "$title ($subtitle)".extractTextFromRoundBrackets()

        assertThat(title).isEqualTo(extractedTitle)
        assertThat(subtitle).isEqualTo(extractedSubtitle)
    }

    @Test
    fun `title with empty parentheses () returns text without parentheses`() {
        val title = "title"
        val subtitle = ""
        val (extractedTitle, extractedSubtitle) = "$title ($subtitle)".extractTextFromRoundBrackets()

        assertThat(title).isEqualTo(extractedTitle)
        assertThat(extractedSubtitle).isNull()
    }

    @Test
    fun `title with blank parentheses ( ) returns text without parentheses`() {
        val title = "title"
        val subtitle = " "
        val (extractedTitle, extractedSubtitle) = "$title ($subtitle)".extractTextFromRoundBrackets()

        assertThat(title).isEqualTo(extractedTitle)
        assertThat(extractedSubtitle).isNull()
    }

    @Test
    fun `title without parentheses returns original text`() {
        val title = "title"
        val (extractedTitle, extractedSubtitle) = title.extractTextFromRoundBrackets()

        assertThat(title).isEqualTo(extractedTitle)
        assertThat(extractedSubtitle).isNull()
    }

    @Test
    fun `empty string returns itself`() {
        val title = ""
        val (extractedTitle, extractedSubtitle) = title.extractTextFromRoundBrackets()

        assertThat(title).isEqualTo(extractedTitle)
        assertThat(extractedSubtitle).isNull()
    }

    @Test
    fun `boolean toInt true = 1`() {
        assertThat(true.toInt()).isEqualTo(1)
    }

    @Test
    fun `boolean false = 0`() {
        assertThat(false.toInt()).isEqualTo(0)
    }

    @Test
    fun `string SPACE equals actual space char`() {
        assertThat(String.SPACE).isEqualTo(" ")
    }

    @Test
    fun `string EMPTY equals actual empty string`() {
        assertThat(String.EMPTY).isEqualTo("")
    }

    @Test
    fun `string with https returns itself`() {
        val link = "https://google.com"
        val result = link.httpsEverywhere()
        assertThat(result).contains("https://")
    }

    @Test
    fun `string with http returns https`() {
        val link = "http://google.com"
        val result = link.httpsEverywhere()
        assertThat(result).contains("https://")
    }

    @Test
    fun `string without http(s) returns itself`() {
        val link = "google.com"
        val result = link.httpsEverywhere()
        assertThat(result).doesNotContain("http")
    }

}