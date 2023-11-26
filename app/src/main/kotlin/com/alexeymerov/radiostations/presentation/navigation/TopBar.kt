package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.presentation.common.view.DropDownRow

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(barState: TopBarState, navController: NavController) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent), //with color it has some delay for color animation
        title = { TopBarTitle(barState.title, barState.subTitle) },
        navigationIcon = {
            if (barState.selectedItems == 0) {
                NavigationIcon(barState.displayBackButton, navController)
            }
        },
        actions = { TopBarActions(barState.rightIcon) }
    )
}

@Composable
private fun TopBarTitle(title: String, subTitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = title,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            label = String.EMPTY
        ) { targetText ->
            Text(text = targetText, fontWeight = FontWeight.Bold)
        }
        if (subTitle.isNotEmpty()) {
            AnimatedContent(
                targetState = subTitle,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                label = String.EMPTY
            ) { targetText ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .alpha(0.7f)
                            .size(12.dp),
                        imageVector = Icons.Outlined.LocationCity,
                        contentDescription = String.EMPTY
                    )

                    Text(
                        modifier = Modifier
                            .alpha(0.7f)
                            .padding(start = 4.dp),
                        text = targetText,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationIcon(displayBackButton: Boolean, navController: NavController) {
    AnimatedVisibility(
        visible = displayBackButton,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
    }
}

@Composable
private fun RowScope.TopBarActions(
    rightIcon: RightIconItem?
) {
    AnimatedVisibility(
        visible = rightIcon != null,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        var needShowMenu by rememberSaveable { mutableStateOf(false) }
        rightIcon?.let {
            IconButton(
                onClick = {
                    if (it.dropDownMenu != null) {
                        needShowMenu = !needShowMenu
                    }
                    it.action.invoke()
                }
            ) {
                it.icon.iconResId?.let { iconRes ->
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = String.EMPTY
                    )
                } ?: Icon(
                    imageVector = it.icon.iconVector,
                    contentDescription = String.EMPTY
                )
            }

            DropdownMenu(
                expanded = needShowMenu,
                onDismissRequest = { needShowMenu = false },
                modifier = Modifier.defaultMinSize(minWidth = 125.dp),
                offset = DpOffset(x = 12.dp, y = 0.dp),
            ) {
                it.dropDownMenu?.forEach {
                    DropDownRow(
                        iconId = it.iconId,
                        stringId = it.stringId,
                        action = it.action
                    )
                }
            }
        }
    }
}