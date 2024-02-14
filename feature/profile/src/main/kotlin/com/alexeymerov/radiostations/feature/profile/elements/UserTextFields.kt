package com.alexeymerov.radiostations.feature.profile.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.dto.TextFieldData
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.R
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
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        UserTextField(
            modifier = Modifier.fillMaxWidth(),
            inEdit = inEdit,
            icon = Icons.Outlined.Person,
            labelResId = R.string.name,
            data = userData.name,
            onValueChange = {
                if (it != userData.name.text) {
                    onAction.invoke(ProfileViewModel.ViewAction.NewName(it))
                }
            }
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
            onValueChange = {
                if (it != userData.email.text) {
                    onAction.invoke(ProfileViewModel.ViewAction.NewEmail(it))
                }
            }
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
            onValueChange = {
                if (it != userData.phoneNumber.text) {
                    onAction.invoke(ProfileViewModel.ViewAction.NewPhone(it))
                }
            },
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
        Icon(
            modifier = Modifier.padding(bottom = if (inEdit) 4.dp else 0.dp),
            imageVector = icon,
            contentDescription = null
        )

        var text by remember { mutableStateOf(data.text) }
        LaunchedEffect(text) {
            onValueChange.invoke(text)
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            value = text,
            onValueChange = { text = it },
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
            supportingText = if (inEdit) {
                {
                    data.errorTextResId?.let {
                        Text(text = stringResource(it))
                    }
                }
            } else null,
            trailingIcon = {
                if (inEdit && data.errorTextResId == null) {
                    Icon(Icons.Rounded.CheckCircleOutline, contentDescription = null)
                }
            },
            label = { Text(text = stringResource(labelResId)) },
            prefix = { prefix?.invoke() }
        )
    }
}