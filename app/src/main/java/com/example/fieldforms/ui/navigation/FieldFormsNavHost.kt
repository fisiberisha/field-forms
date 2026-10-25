package com.example.fieldforms.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fieldforms.presentation.FormUiState
import com.example.fieldforms.ui.form.FormScreen
import com.example.fieldforms.ui.form.ImageDetailScreen

@Composable
fun FieldFormsNavHost(
    uiState: FormUiState,
    onRetry: () -> Unit,
    onChoiceResponseToggle: (questionId: Int, responseId: Int, multipleSelection: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = FormRoutes.FORM,
        modifier = modifier,
    ) {
        composable(route = FormRoutes.FORM) {
            FormScreen(
                uiState = uiState,
                onRetry = onRetry,
                onImageClick = { imageQuestion ->
                    navController.navigateToImageDetail(
                        imageSrc = imageQuestion.src,
                        title = imageQuestion.title,
                    )
                },
                onChoiceResponseToggle = onChoiceResponseToggle,
            )
        }
        composable(
            route = FormRoutes.IMAGE_DETAIL,
            arguments = listOf(
                navArgument(FormRoutes.IMAGE_SRC_ARG) { type = NavType.StringType },
                navArgument(FormRoutes.IMAGE_TITLE_ARG) { type = NavType.StringType },
            ),
        ) {
            val imageSrc = it.arguments?.getString(FormRoutes.IMAGE_SRC_ARG).orEmpty()
            val title = it.arguments?.getString(FormRoutes.IMAGE_TITLE_ARG).orEmpty()
            ImageDetailScreen(
                imageSrc = imageSrc,
                title = title,
                onBack = navController::popBackStack,
            )
        }
    }
}
