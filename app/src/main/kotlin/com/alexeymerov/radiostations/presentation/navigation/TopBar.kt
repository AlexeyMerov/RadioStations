package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.rememberAsyncImagePainter
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.common.LocalConnectionStatus
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.navigation.RightIconItem
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.core.ui.view.DropDownRow

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(
    barState: TopBarState,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val navController = LocalNavController.current
    CenterAlignedTopAppBar(
        //with color it has some delay for color animation
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        title = { TopBarTitle(barState.title, barState.subTitle) },
        navigationIcon = {
            if (barState.selectedItems == 0) {
                NavigationIcon(
                    displayBackButton = barState.displayBackButton,
                    onClick = {
                        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        actions = { TopBarActions(barState.rightIcon) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBarTitle(title: String, subTitle: String?) {
    val config = LocalConfiguration.current
    val isNetworkAvailable = LocalConnectionStatus.current

    Column(
        modifier = Modifier.run { if (config.isLandscape()) padding(start = 80.dp) else this },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = title,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            label = "TopBarTitle"
        ) { targetText ->
            Text(
                modifier = Modifier.basicMarquee(
                    iterations = 10,
                    velocity = 20.dp
                ),
                text = targetText,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AnimatedVisibility(
            visible = !isNetworkAvailable,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            label = "isNetwork not Available"
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp),
                    text = "Offline",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }

        if (!subTitle.isNullOrEmpty() && isNetworkAvailable) {
            AnimatedContent(
                targetState = subTitle,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                label = "TopBar SubTitle"
            ) { targetText ->
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        modifier = Modifier
                            .alpha(0.7f)
                            .size(12.dp),
                        imageVector = Icons.Outlined.LocationCity,
                        contentDescription = null
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
private fun NavigationIcon(displayBackButton: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = displayBackButton,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        val config = LocalConfiguration.current
        IconButton(
            modifier = Modifier.padding(start = if (config.isLandscape()) 16.dp else 0.dp),
            onClick = { onClick.invoke() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
        rightIcon?.let { iconItem ->
            IconButton(
                onClick = {
                    if (iconItem.dropDownMenu != null) {
                        needShowMenu = !needShowMenu
                    }
                    iconItem.action.invoke()
                }
            ) {
                iconItem.icon.iconResId?.let { iconRes ->
                    Icon(
                        painter = rememberAsyncImagePainter(iconRes),
                        contentDescription = null
                    )
                } ?: Icon(
                    imageVector = iconItem.icon.iconVector,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = needShowMenu,
                onDismissRequest = { needShowMenu = false },
                modifier = Modifier.defaultMinSize(minWidth = 125.dp),
                offset = DpOffset(x = 12.dp, y = 0.dp),
            ) {
                iconItem.dropDownMenu?.forEach {
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