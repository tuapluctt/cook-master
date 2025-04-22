package vn.hvt.cook_master.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.constant.S3KeyPrefix;
import vn.hvt.cook_master.dto.ImageDTO;
import vn.hvt.cook_master.dto.request.IngredientRequest;
import vn.hvt.cook_master.dto.request.PresignedUrlRequest;
import vn.hvt.cook_master.dto.request.RecipeRequest;
import vn.hvt.cook_master.dto.request.StepRequest;
import vn.hvt.cook_master.dto.response.*;
import vn.hvt.cook_master.service.RecipeService;
import vn.hvt.cook_master.service.StorageImageService;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecipeController {
    RecipeService recipeService;
    StorageImageService storageImageService;

    // initiate recipe
    @PostMapping("/initiate")
    public ApiResponse<CreateRecipeResponse> createRecipe(){
        CreateRecipeResponse createRecipeResponse = recipeService.createRecipe();
        log.info("Recipe initiated with ID: {}", createRecipeResponse.getRecipeId());

        return ApiResponse.<CreateRecipeResponse>builder()
                .result(createRecipeResponse)
                .build();
    }

    // get recipe by id
    @GetMapping("/{id}")
    public ApiResponse<RecipeResponse> getRecipe(@PathVariable String id){
        return ApiResponse.<RecipeResponse>builder()
                .result(recipeService.getRecipe(id))
                .build();
    }

    // update recipe (partial : title, description, cookTime, status)
    @PutMapping("/{id}")
    public ApiResponse<RecipeResponse> updateRecipe(
            @PathVariable String id,
            @RequestBody @Valid RecipeRequest request,
            @RequestParam("partial") String field){

        log.info("Updating recipe with ID: {}. Fields to update: {}", id, field);
        log.info("Request body: {}", request);

        return ApiResponse.<RecipeResponse>builder()
                .result(recipeService.updateRecipe(id, request, field))
                .build();
    }

    // upload image form server to s3
    @PostMapping("/{recipeId}/image")
    public ApiResponse<String> uploadImage(
            @PathVariable String recipeId,
            @RequestParam("file") MultipartFile file){

        log.info("Uploading image for recipe with ID: {}", recipeId);

        return ApiResponse.<String>builder()
                .result(recipeService.uploadImageRecipe(recipeId, file))
                .build();
    }

    // generate presigned url for uploading image
    @PostMapping("/{recipeId}/image/generate-upload-url")
    public ApiResponse<ImageDTO> generatePresignedUrl(
            @PathVariable String recipeId,
            @RequestBody @Valid PresignedUrlRequest request){

        log.info("Generating presigned URL for recipe with ID: {}, Request body: {}", recipeId, request);

        return ApiResponse.<ImageDTO>builder()
                .result(storageImageService.generatePresignedUploadUrl(request, S3KeyPrefix.RECIPE_IMAGE_PREFIX))
                .build();
    }





    // add ingredient to recipe
    @PostMapping("/{recipeId}/ingredients")
    public ApiResponse<IngredientResponse> addIngredient(
            @PathVariable String recipeId,
            @RequestBody IngredientRequest request){
        return ApiResponse.<IngredientResponse>builder()
                .result(recipeService.addIngredient(recipeId, request))
                .build();
    }

    // update ingredient of recipe
    @PostMapping("/{recipeId}/ingredients/{recipeIngredientId}")
    public ApiResponse<IngredientResponse> updateIngredient(
            @PathVariable String recipeId,
            @RequestBody IngredientRequest request,
            @PathVariable String recipeIngredientId){
        return ApiResponse.<IngredientResponse>builder()
                .result(recipeService.updateIngredientOfRecipe(recipeId, request , recipeIngredientId))
                .build();
    }

    // delete ingredient of recipe
    @DeleteMapping("/{recipeId}/ingredients/{recipeIngredientId}")
    public ApiResponse<?> deleteIngredient(
            @PathVariable String recipeId,
            @PathVariable String recipeIngredientId){

        recipeService.deleteIngredientOfRecipe(recipeId, recipeIngredientId);
        return ApiResponse.builder()
                .build();
    }

    // get list of ingredients of recipe
    @GetMapping("/{recipeId}/ingredients")
    public ApiResponse<List<IngredientResponse>> getListOfIngredients(
            @PathVariable String recipeId){
        return ApiResponse.<List<IngredientResponse>>builder()
                .result(recipeService.getListOfIngredients(recipeId))
                .build();
    }



    // add step to recipe or update step if stepId is provided
    @PostMapping("/{recipeId}/steps")
    public ApiResponse<StepResponse> addOrUpdateStep(
            @PathVariable String recipeId,
            @RequestBody @Valid StepRequest request,
            @RequestParam(value = "stepId", required = false) String stepId){
        return ApiResponse.<StepResponse>builder()
                .result(recipeService.addOrUpdateStep(recipeId, request,stepId))
                .build();
    }

    // delete step of recipe
    @DeleteMapping("/{recipeId}/steps/{stepId}")
    public ApiResponse<?> deleteStep(
            @PathVariable String recipeId,
            @PathVariable String stepId){
        recipeService.deleteStep(recipeId, stepId);
        return ApiResponse.builder()
                .build();
    }


    // get list of steps of recipe
    @GetMapping("/{recipeId}/steps")
    public ApiResponse<List<StepResponse>> getListOfSteps(
            @PathVariable String recipeId){
        return ApiResponse.<List<StepResponse>>builder()
                .result(recipeService.getListOfSteps(recipeId))
                .build();
    }

}
