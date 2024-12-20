package vn.hvt.cook_master.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.dto.response.ApiResponse;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.service.ImageService;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ImageController {

    ImageService imageService;

    // sau này sẽ di chuyển các api này vào các controller tương ứng
    @PostMapping("/recipes/{recipeId}")
    public ApiResponse<String> uploadRecipeImage(
            @PathVariable Long recipeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        String imageUrl = imageService.uploadImage(file, "recipe", recipeId, null, userId);
        return ApiResponse.<String>builder()
                .result(imageUrl)
                .build();
    }

    @PostMapping("/recipes/{recipeId}/steps/{stepId}")
    public ApiResponse<String> uploadStepImage(
            @PathVariable Long recipeId,
            @PathVariable Long stepId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        String imageUrl = imageService.uploadImage(file, "step", recipeId, stepId, userId);
        return ApiResponse.<String>builder()
                .result(imageUrl)
                .build();
    }

    @PostMapping("/users")
    public ApiResponse<String> uploadUserProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        String imageUrl = imageService.uploadImage(file, "user", null, null, userId);
        return ApiResponse.<String>builder()
                .result(imageUrl)
                .build();
    }

//    @PostMapping
//    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
//
//    }

    @DeleteMapping("/{publicId}")
    public ApiResponse<?> deleteImage(
            @PathVariable String publicId,
            @RequestParam("userId") Long userId) {

        try {
            imageService.deleteImage(publicId, userId);
            return  ApiResponse.<Void>builder()
                    .build();

        } catch (IOException e) {
            throw new AppException(ErrorCode.DELETE_IMAGE_FAILED);
        }
    }
}