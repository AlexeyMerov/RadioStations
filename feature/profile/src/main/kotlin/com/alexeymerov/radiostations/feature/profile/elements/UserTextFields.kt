package com.alexeymerov.radiostations.feature.profile.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.dto.CountryDto
import com.alexeymerov.radiostations.core.dto.TextFieldData
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.remembers.rememberTextPainter
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
internal fun UserTextFields(
    inEdit: Boolean,
    userData: UserDto,
    onAction: (ProfileViewModel.ViewAction) -> Unit,
    onCountryAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        UserTextField(
            modifier = Modifier.fillMaxWidth(),
            inEdit = inEdit,
            icon = Icons.Outlined.Person,
            labelResId = R.string.name,
            data = userData.name,
            onValueChange = { onAction.invoke(ProfileViewModel.ViewAction.NewName(it)) }
        )

        UserTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            inEdit = inEdit,
            icon = Icons.Rounded.MailOutline,
            labelResId = R.string.email,
            data = userData.email,
            keyboardType = KeyboardType.Email,
            onValueChange = { onAction.invoke(ProfileViewModel.ViewAction.NewEmail(it)) }
        )

        UserTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            inEdit = inEdit,
            icon = Icons.Outlined.Phone,
            labelResId = R.string.phone,
            data = userData.phoneNumber,
            keyboardType = KeyboardType.Phone,
            onValueChange = { onAction.invoke(ProfileViewModel.ViewAction.NewPhone(it)) },
            prefix = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .run {
                            if (inEdit) {
                                background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .clickable { onCountryAction.invoke() }
                            } else this
                        }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
                        text = "+${userData.countryCode}"
                    )
                }
            }
        )

        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(0.5f),
            text = "Local only"
        )
    }
}

@Composable
private fun UserTextField(
    modifier: Modifier = Modifier,
    inEdit: Boolean,
    icon: ImageVector,
    labelResId: Int,
    data: TextFieldData,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    prefix: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = String.EMPTY)

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            value = TextFieldValue(
                text = data.text,
                selection = TextRange(data.text.length)
            ),
            onValueChange = { onValueChange.invoke(it.text) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledPrefixColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = inEdit,
            readOnly = !inEdit,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            isError = data.errorTextResId != null,
            supportingText = {
                data.errorTextResId?.let {
                    Text(text = stringResource(it))
                }
            },
            trailingIcon = {
                if (inEdit && data.errorTextResId == null) {
                    Icon(Icons.Rounded.CheckCircleOutline, contentDescription = String.EMPTY)
                }
            },
            label = { Text(text = stringResource(labelResId)) },
            prefix = { prefix?.invoke() }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
internal fun CountriesBottomSheet(
    countries: LazyPagingItems<CountryDto>,
    onSelect: (CountryDto) -> Unit,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val svgFactory = remember { SvgDecoder.Factory() }
    val surfaceColorAtElevation = MaterialTheme.colorScheme.surfaceColorAtElevation(BottomSheetDefaults.Elevation)
    val flagGradientColors = remember {
        listOf(
            Color.Transparent,
            surfaceColorAtElevation.copy(alpha = 0.60f),
            surfaceColorAtElevation.copy(alpha = 0.80f),
            surfaceColorAtElevation.copy(alpha = 0.95f),
            surfaceColorAtElevation,
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss.invoke() }
    ) {
        var searchText by remember { mutableStateOf(String.EMPTY) }
        var isSearchActive by remember { mutableStateOf(false) }

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            query = searchText,
            onQueryChange = {
                searchText = it
                onSearch.invoke(it)
            },
            placeholder = {
                AnimatedVisibility(
                    visible = !isSearchActive,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    Text(text = "Search")
                }
            },
            leadingIcon = {
                AnimatedVisibility(
                    visible = !isSearchActive,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = isSearchActive && searchText.isNotEmpty(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    IconButton(onClick = {
                        searchText = String.EMPTY
                        onSearch.invoke(String.EMPTY)
                    }) {
                        Icon(Icons.Rounded.Clear, contentDescription = null)
                    }
                }
            },
            shape = CircleShape,
            onSearch = { focusManager.clearFocus(true) },
            active = false, // should be false
            onActiveChange = {
                isSearchActive = it
                if (it) {
                    coroutineScope.launch {
                        sheetState.expand()
                    }
                }
            },
            content = {}
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
        ) {
            // https://developer.android.com/reference/kotlin/androidx/paging/compose/package-summary
            items(
                count = countries.itemCount,
                key = countries.itemKey { it.tag }
            ) { index ->
                val country = countries[index]
                if (country != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable { onSelect.invoke(country) }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .padding(end = 8.dp)
                                .weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .width(64.dp)
                                    .alpha(0.75f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.horizontalGradient(
                                                endX = 56.dp.toPx(),
                                                colors = flagGradientColors
                                            )
                                        )
                                    },
                                model = ImageRequest.Builder(context)
                                    .data(country.flagUrl)
                                    .decoderFactory(svgFactory)
                                    .crossfade(200)
                                    .build(),
                                contentScale = ContentScale.FillHeight,
                                alignment = Alignment.CenterStart,
                                contentDescription = null,
                                error = rememberTextPainter(
                                    text = "Flag",
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            Column(
                                modifier = Modifier.padding(start = 16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.basicMarquee(),
                                    text = buildAnnotatedString {
                                        append(country.englishName)
                                        country.englishNameHighliths?.forEach { range ->
                                            addStyle(
                                                SpanStyle(color = MaterialTheme.colorScheme.error),
                                                start = range.first,
                                                end = range.last
                                            )
                                        }
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1
                                )

                                if (country.nativeName != null) {
                                    Text(
                                        modifier = Modifier.basicMarquee(),
                                        text = buildAnnotatedString {
                                            append(country.nativeName)
                                            country.nativeNameHighliths?.forEach { range ->
                                                addStyle(
                                                    SpanStyle(color = MaterialTheme.colorScheme.error),
                                                    start = range.first,
                                                    end = range.last
                                                )
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        BasicText(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 6.dp),
                            text = "+${country.phoneCode}",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

            }
        }
    }
}