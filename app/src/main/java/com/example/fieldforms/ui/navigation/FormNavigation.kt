package com.example.fieldforms.ui.navigation

import android.net.Uri
import androidx.navigation.NavController

object FormRoutes {
    const val FORM = "form"
    const val IMAGE_DETAIL = "image_detail?src={src}&title={title}"

    const val IMAGE_SRC_ARG = "src"
    const val IMAGE_TITLE_ARG = "title"

    fun imageDetailRoute(imageSrc: String, title: String): String =
        "image_detail?src=${Uri.encode(imageSrc)}&title=${Uri.encode(title)}"
}

fun NavController.navigateToImageDetail(imageSrc: String, title: String) {
    navigate(FormRoutes.imageDetailRoute(imageSrc, title))
}
