package com.example.base.presentation.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.base.R
import com.example.base.ui.theme.Spacing
import com.example.base.ui.theme.ThemeMode
import com.example.base.ui.theme.supportsDynamicColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(viewModel: SettingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.setting_title)) },
                navigationIcon = {
                    IconButton(onClick = viewModel::goBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionHeader(stringResource(R.string.setting_appearance))
            Column(Modifier.selectableGroup()) {
                ThemeMode.entries.forEach { mode ->
                    val selected = mode == state.themeMode
                    ListItem(
                        modifier = Modifier.selectable(
                            selected = selected,
                            role = Role.RadioButton,
                            onClick = { viewModel.setThemeMode(mode) },
                        ),
                        headlineContent = { Text(stringResource(mode.labelRes)) },
                        leadingContent = { RadioButton(selected = selected, onClick = null) },
                    )
                }
            }
            if (supportsDynamicColor) {
                ListItem(
                    modifier = Modifier.selectable(
                        selected = state.dynamicColor,
                        role = Role.Switch,
                        onClick = { viewModel.setDynamicColor(!state.dynamicColor) },
                    ),
                    headlineContent = { Text(stringResource(R.string.setting_dynamic_color)) },
                    supportingContent = { Text(stringResource(R.string.setting_dynamic_color_desc)) },
                    trailingContent = { Switch(checked = state.dynamicColor, onCheckedChange = null) },
                )
            }

            SectionHeader(stringResource(R.string.setting_about))
            ListItem(
                leadingContent = { Icon(Icons.Filled.Info, contentDescription = null) },
                headlineContent = { Text(stringResource(R.string.setting_version)) },
                supportingContent = { Text(state.versionName) },
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = Spacing.md, end = Spacing.md, top = Spacing.lg, bottom = Spacing.sm),
    )
}

private val ThemeMode.labelRes: Int
    get() = when (this) {
        ThemeMode.SYSTEM -> R.string.setting_theme_system
        ThemeMode.LIGHT -> R.string.setting_theme_light
        ThemeMode.DARK -> R.string.setting_theme_dark
    }
