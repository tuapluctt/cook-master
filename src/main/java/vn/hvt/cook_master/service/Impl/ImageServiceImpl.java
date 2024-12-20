package vn.hvt.cook_master.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.entity.Image;
import vn.hvt.cook_master.entity.User;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.repository.ImageRepository;
import vn.hvt.cook_master.repository.UserRepository;
import vn.hvt.cook_master.service.ImageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ImageServiceImpl implements ImageService {
    Cloudinary cloudinary;
    UserRepository userRepository;
    ImageRepository imageRepository;
    @Override
    public String uploadImage(MultipartFile file, String type, Long recipeId, Long stepId, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            String folderPath = generateFolderPath(type, userId, recipeId, stepId);

            // Cấu hình upload lên Cloudinary
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("folder", folderPath);
            uploadOptions.put("public_id", generateUniqueImageName(file.getOriginalFilename()));
            uploadOptions.put("resource_type", "auto");

            // Upload ảnh lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            // Lấy publicId của ảnh vừa upload
            String publicId = (String) uploadResult.get("public_id");

            // Lưu thông tin ảnh vào database (Image entity)
            Image image = new Image();
            image.setPublicId(publicId);
            image.setImageUrl((String) uploadResult.get("secure_url"));
            image.setUserId(userId);

            if ("recipe".equals(type)) {
                image.setRecipeId(recipeId);
            } else if ("step".equals(type)) {
                image.setStepId(stepId);
            }

            image.setType(type);
            imageRepository.save(image);

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteImage(String publicId, Long userId) {
        try {
            // Xóa ảnh trên Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            // Xóa thông tin ảnh trong database
            var image = imageRepository.findById(Integer.valueOf(publicId))
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));
            imageRepository.delete(image);
        } catch (IOException e) {
            throw new AppException(ErrorCode.DELETE_IMAGE_FAILED);
        }
    }

    private String generateFolderPath(String type, Long userId, Long recipeId, Long stepId) {
        switch (type) {
            case "recipe":
                return "recipes/" + recipeId + "/images";
            case "step":
                return "recipes/" + recipeId + "/steps/" + stepId + "/images";
            case "user":
                return "users/" + userId + "/profile-pictures";
            default:
                throw new IllegalArgumentException("Invalid image type");
        }
    }

    private String generateUniqueImageName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

}
