package vn.hvt.cook_master.enums;

import java.util.Arrays;

public enum AllowedFileType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif");
//    APPLICATION_PDF("application/pdf");

    private final String mimeType;

    AllowedFileType(String mimeType) {
        this.mimeType = mimeType;
    }
    public String getType() {
        return mimeType;
    }

}
