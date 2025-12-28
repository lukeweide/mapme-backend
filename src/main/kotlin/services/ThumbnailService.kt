package com.mapme.data.services

import com.mapme.config.AppConfig
import net.coobird.thumbnailator.Thumbnails
import java.io.File

class ThumbnailService {

    /**
     * Generates a thumbnail for an image file
     * Returns the path to the generated thumbnail
     */
    fun generateThumbnail(
        sourceFile: File,
        size: Int = AppConfig.Photos.THUMBNAIL_SIZE
    ): File {
        val thumbnailDir = File("data/thumbnails")
        thumbnailDir.mkdirs()

        val thumbnailFileName = "${sourceFile.nameWithoutExtension}_${size}.jpg"
        val thumbnailFile = File(thumbnailDir, thumbnailFileName)

        // Skip if already exists
        if (thumbnailFile.exists()) {
            return thumbnailFile
        }

        // Generate thumbnail with Thumbnailator
        Thumbnails.of(sourceFile)
            .size(size, size)
            .outputFormat("jpg")
            .outputQuality(0.85)
            .toFile(thumbnailFile)

        return thumbnailFile
    }

    /**
     * Gets thumbnail (generates if not exists)
     */
    fun getThumbnail(photoFile: File, size: Int = AppConfig.Photos.THUMBNAIL_SIZE): File? {
        return try {
            generateThumbnail(photoFile, size)
        } catch (e: Exception) {
            null
        }
    }
}