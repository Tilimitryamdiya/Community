package ru.netology.community.model

import android.net.Uri
import ru.netology.community.enumeration.AttachmentType
import java.io.File

data class MediaModel(
    val uri: Uri,
    val file: File,
    val type: AttachmentType
)
