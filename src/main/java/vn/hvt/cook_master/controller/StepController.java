package vn.hvt.cook_master.controller;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.dto.response.ApiResponse;
import vn.hvt.cook_master.dto.response.StepImageResponse;
import vn.hvt.cook_master.dto.response.StepResponse;
import vn.hvt.cook_master.service.RecipeService;

@RestController
@RequestMapping("/api/step")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StepController {
    RecipeService recipeService;

    // Add image to step or update image if stepImageId is provided
    @PostMapping("/{stepId}/images")
    public ApiResponse<StepImageResponse> uploadImageToStep(
            @PathVariable String stepId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "stepImageId", required = false) String stepImageId){
        return ApiResponse.<StepImageResponse>builder()
                .result(recipeService.uploadImageToStep( stepId, file, stepImageId))
                .build();
    }

    // get image by stepId
    @GetMapping("/{stepId}/images")
    public ApiResponse<StepResponse> getImageByStepId(
            @PathVariable String stepId){
        return ApiResponse.<StepResponse>builder()
                .result(recipeService.getImageByStepId(stepId))
                .build();
    }

}
