package vn.hvt.cook_master.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    void deleteImage(String publicId, Long userId) throws IOException;

    String uploadImage(MultipartFile file, String type, Long recipeId, Long stepId, Long userId);
}
