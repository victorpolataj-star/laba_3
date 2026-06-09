package com.example.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import java.io.File;

public class ExifReader {

    public static String readExifData(File imageFile) {
        StringBuilder sb = new StringBuilder();

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

            // Размер изображения
            JpegDirectory jpegDir = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            if (jpegDir != null) {
                int width = jpegDir.getImageWidth();
                int height = jpegDir.getImageHeight();
                sb.append("Размер: ").append(width).append("x").append(height).append(" пикс.\n");
            }

            // Информация о камере
            ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0 != null) {
                String model = exifIFD0.getString(ExifIFD0Directory.TAG_MODEL);
                if (model != null) {
                    sb.append("Камера: ").append(model).append("\n");
                }

                String make = exifIFD0.getString(ExifIFD0Directory.TAG_MAKE);
                if (make != null) {
                    sb.append("Производитель: ").append(make).append("\n");
                }
            }

            // Параметры съемки
            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (subIFD != null) {
                String dateTime = subIFD.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (dateTime != null) {
                    sb.append("Дата съемки: ").append(dateTime).append("\n");
                }

                String exposure = subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
                if (exposure != null) {
                    sb.append("Выдержка: ").append(exposure).append(" сек\n");
                }

                String fNumber = subIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER);
                if (fNumber != null) {
                    sb.append("Диафрагма: f/").append(fNumber).append("\n");
                }

                int iso = subIFD.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
                if (iso > 0) {
                    sb.append("ISO: ").append(iso).append("\n");
                }
            }

            if (sb.length() == 0) {
                sb.append("EXIF данные отсутствуют");
            }

        } catch (Exception e) {
            sb.append("Ошибка чтения EXIF: ").append(e.getMessage());
        }

        return sb.toString();
    }
}
