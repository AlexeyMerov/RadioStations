package com.alexeymerov.radiostations.feature.profile.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.dto.Country
import com.alexeymerov.radiostations.core.dto.TextFieldData
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel

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
            .padding(8.dp),
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
                        text = "+${userData.country.phoneCode}"
                    )
                }
            }
        )

        Text(
            modifier = Modifier
                .padding(top = 16.dp)
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
                .padding(start = 8.dp),
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
@OptIn(ExperimentalMaterial3Api::class)
internal fun CountriesBottomSheet(
    countries: List<Country>,
    onSelect: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { onDismiss.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp),
                    text = "In development",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = countries,
                key = { it.phoneCode }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable { onSelect.invoke(it) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(text = "+ ${it.phoneCode}")
                }
            }
        }
    }
}