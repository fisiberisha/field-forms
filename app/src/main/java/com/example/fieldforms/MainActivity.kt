package com.example.fieldforms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fieldforms.presentation.FormViewModel
import com.example.fieldforms.ui.form.FormScreen
import com.example.fieldforms.ui.theme.FieldFormsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            FieldFormsTheme {
                FormScreen(
                    uiState = uiState,
                    onRetry = viewModel::refresh,
                )
            }
        }
    }
}
