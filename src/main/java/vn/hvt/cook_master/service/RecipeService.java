package vn.hvt.cook_master.service;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.constant.S3KeyPrefix;
import vn.hvt.cook_master.dto.ImageDTO;
import vn.hvt.cook_master.dto.request.IngredientRequest;
import vn.hvt.cook_master.dto.request.RecipeRequest;
import vn.hvt.cook_master.dto.request.StepRequest;
import vn.hvt.cook_master.dto.response.*;
import vn.hvt.cook_master.entity.*;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.mapper.IngredientMapper;
import vn.hvt.cook_master.mapper.RecipeStepMapper;
import vn.hvt.cook_master.mapper.StepImageMapper;
import vn.hvt.cook_master.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecipeService {
    RecipeRepository recipeRepository;
    UserRepository userRepository;
    IngredientRepository ingredientRepository;
    IngredientMapper ingredientMapper;
    RecipeStepRepository recipeStepRepository;
    StepImageRepository stepImageRepository;
    RecipeStepMapper recipeStepMapper;
    StepImageMapper stepImageMapper;
    StorageImageService storageImageService;


    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CreateRecipeResponse createRecipe() {
        log.info("Create new recipe");
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", auth.getName());
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });


        Recipe recipe = Recipe.builder()
                .user(user)
                .status(RecipeStatus.DRAFT)
                .build();

        String recipeId = recipeRepository.save(recipe).getRecipeId().toString();

        return CreateRecipeResponse.builder()
                .recipeId(recipeId)
                .build();
    }

    @Transactional
    public RecipeResponse getRecipe(String id) {
        log.info("Get recipe with id: {}", id);
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", id);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });

        List<StepResponse> stepResponses = this.getListOfSteps(String.valueOf(recipe.getRecipeId()));

        List<IngredientResponse> ingredientResponses = this.getListOfIngredients(String.valueOf(recipe.getRecipeId()));

        return RecipeResponse.builder()
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .cookTime(recipe.getCookTime())
                .status(recipe.getStatus().name())
                .ingredients(ingredientResponses)
                .steps(stepResponses)
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public RecipeResponse updateRecipe(String id, RecipeRequest request, String field) {
        log.info("Update recipe with id: {} , with partial: {}", id, field);

        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", id);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });
        RecipeResponse response = RecipeResponse.builder()
                .build();
        if (field != null && !field.isEmpty()) {

            switch (field) {
                case "title":
                    recipe.setTitle(request.getTitle());
                    response.setTitle(request.getTitle());
                    break;
                case "description":
                    recipe.setDescription(request.getDescription());
                    response.setDescription(request.getDescription());
                    break;
                case "cookTime":
                    recipe.setCookTime(request.getCookTime());
                    response.setCookTime(request.getCookTime());
                    break;
                case "status":
                    recipe.setStatus(RecipeStatus.valueOf(request.getStatus()));
                    response.setStatus(request.getStatus());
                    break;
                default:
                    log.warn("Invalid field: {}", field);
                    throw new AppException(ErrorCode.INVALID_FIELD);
            }
        } else {
            log.warn("Field to update is null or empty");
            throw new AppException(ErrorCode.FIELD_NULL_OR_EMPTY);
        }

        recipeRepository.save(recipe);
        log.info("Recipe updated successfully with ID: {}, partial : {}", id, field);

        return response;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String uploadImageRecipe(String recipeId, MultipartFile file) {
        log.info("Upload image for recipe with ID: {}", recipeId);
        var recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", recipeId);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });

        if (recipe.getImageS3Key() != null) {
            storageImageService.deleteImage(recipe.getImageS3Key());
        }

        ImageDTO imageDTO = storageImageService.uploadImage(file, S3KeyPrefix.RECIPE_IMAGE_PREFIX);

        recipe.setImageS3Key(imageDTO.getImageKey());
        recipe.setImageS3Bucket(imageDTO.getImageS3Bucket());

        recipeRepository.save(recipe);
        return imageDTO.getUrl();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public IngredientResponse addIngredient(String recipeId, IngredientRequest request) {
        log.info("Add ingredient to recipe with ID: {}", recipeId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", recipeId);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .quantity(request.getQuantity())
                .build();

        recipe.addIngredient(ingredient);

        recipeRepository.save(recipe);

        return IngredientResponse.builder()
                .id(ingredient.getIngredientId())
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .build();

    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public IngredientResponse updateIngredientOfRecipe(String recipeId, IngredientRequest request, String recipeIngredientId) {
        log.info("Update ingredient of recipe with ID: {} , ingredient ID : {}", recipeId, recipeIngredientId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        Ingredient ingredient = ingredientRepository.findById(recipeIngredientId)
                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

        if (!ingredient.getRecipe().getRecipeId().equals(recipe.getRecipeId())) {
            log.warn("Ingredient not found in recipe with ID: {}", recipeId);
            throw new AppException(ErrorCode.INGREDIENT_NOT_FOUND);
        }

        ingredient.setName(request.getName());
        ingredient.setUnit(request.getUnit());
        ingredient.setQuantity(request.getQuantity());

        ingredientRepository.save(ingredient);
        return IngredientResponse.builder()
                .id(ingredient.getIngredientId())
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteIngredientOfRecipe(String recipeId, String recipeIngredientId) {
        log.info("Delete ingredient of recipe with ID: {}", recipeId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", recipeId);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });

        Ingredient ingredient = ingredientRepository.findById(recipeIngredientId)
                .orElseThrow(() -> {
                    log.warn("Ingredient not found with ID: {}", recipeIngredientId);
                    return new AppException(ErrorCode.INGREDIENT_NOT_FOUND);
                });

        if (!ingredient.getRecipe().getRecipeId().equals(recipe.getRecipeId())) {
            log.warn("Ingredient not found in recipe with ID: {}", recipeId);
            throw new AppException(ErrorCode.INGREDIENT_NOT_FOUND);
        }

        ingredientRepository.delete(ingredient);
        log.info("Ingredient deleted successfully with ID: {}", recipeIngredientId);
        return;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<IngredientResponse> getListOfIngredients(String recipeId) {
        log.info("Get list of ingredients of recipe with ID: {}", recipeId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", recipeId);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });

        List<IngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                .map(ingredientMapper::toIngredientResponse)
                .toList();

        ingredientResponses.forEach(ingredientResponse -> log.info("Ingredient: {}", ingredientResponse.getName()));

        return ingredientResponses;
    }

    // This method is used to upload image to step or update image of step if stepImageId is provided
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public StepImageResponse uploadImageToStep(String stepId, MultipartFile file, String stepImageId) {
        log.info("Upload image to step with ID: {} ", stepId);
        var step = recipeStepRepository.findById(stepId)
                .orElseThrow(() -> {
                    log.warn("Step not found with ID: {}", stepId);
                    return new AppException(ErrorCode.STEP_NOT_FOUND);
                });
        // upload image to s3
        ImageDTO imageDTO = storageImageService.uploadImage(file, S3KeyPrefix.RECIPE_PREFIX);
        StepImage stepImage ;

        // check if stepImageId is provided
        if (stepImageId != null) {// update image of step
            log.info("Update image for step with ID: {} ", stepId);

            stepImage = stepImageRepository.findById(stepImageId)
                    .orElseThrow(() -> {
                        log.warn("Step image not found with ID: {}", stepImageId);
                        return new AppException(ErrorCode.STEP_IMAGE_NOT_FOUND);
                    });

            if (stepImage.getStep().getStepId() != step.getStepId()) {
                log.warn("Step image not found in step with ID: {}", stepId);
                throw new AppException(ErrorCode.STEP_IMAGE_NOT_FOUND);
            }

            storageImageService.deleteImage(stepImage.getS3Key());
            stepImage.setS3Key(imageDTO.getImageKey());
            stepImageRepository.save(stepImage);

        }else { // add new image to step
            log.info("Add image to step with ID: {} ", stepId);
            stepImage = StepImage.builder()
                    .s3Key(imageDTO.getImageKey())
                    .s3Bucket(imageDTO.getImageS3Bucket())
                    .step(step)
                    .displayOrder(getNextSortOrder(step.getStepId()))
                    .build();
            step.addStepImage(stepImage);
            stepImage = stepImageRepository.save(stepImage);
            recipeStepRepository.save(step);
        }

        return StepImageResponse.builder()
                .imageUrl(imageDTO.getUrl())
                .stepImageId(String.valueOf(stepImage.getStepImageId()))
                .displayOrder(stepImage.getDisplayOrder())
                .build();
    }

    // This method is used to get the next display order for the step image( get max display order of step image)
    private Integer getNextSortOrder(Long stepId) {
        Integer maxOrder = stepImageRepository.findMaxDisplayOrderByStepId(stepId);
        return maxOrder != null ? maxOrder + 1 : 1;
    }


    // This method is used to add or update step of recipe if stepId is provided
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public StepResponse addOrUpdateStep(String recipeId, StepRequest request, String stepId) {
        log.info("Add step to recipe with ID: {}", recipeId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", recipeId);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });
        RecipeStep step;

        if (stepId != null) { // update step
            log.info("Update step with ID: {}", stepId);
            step = recipeStepRepository.findById(stepId)
                    .orElseThrow(() -> {
                        log.warn("Step not found with ID: {}", stepId);
                        return new AppException(ErrorCode.STEP_NOT_FOUND);
                    });

            if (!step.getRecipe().getRecipeId().equals(recipe.getRecipeId())) {
                log.warn("Step not found in recipe with ID: {}", recipeId);
                throw new AppException(ErrorCode.STEP_NOT_FOUND);
            }

            step.setDescription(request.getDescription());
            step = recipeStepRepository.save(step);

        }else {
            log.info("Add step to recipe with ID: {}", recipeId);
            step = RecipeStep.builder()
                    .stepNumber(getNextStepNumber(recipe.getRecipeId()))
                    .description(request.getDescription())
                    .recipe(recipe)
                    .build();

            recipe.getRecipeSteps().add(step);
            step = recipeStepRepository.save(step);
        }

        recipeRepository.save(recipe);

        return StepResponse.builder()
                .stepId(String.valueOf(step.getStepId()))
                .stepNumber(step.getStepNumber())
                .description(step.getDescription())
                .build();
    }

    private Integer getNextStepNumber(Long recipeId) {
        Integer maxStepNumber = recipeStepRepository.findMaxStepNumberByRecipeId(recipeId);
        return maxStepNumber != null ? maxStepNumber + 1 : 1;
    }

    // This method is used to delete step of recipe
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteStep(String recipeId, String stepId) {
        log.info("Delete step with ID: {} from recipe with ID: {}", stepId, recipeId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", recipeId);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });

        RecipeStep step = recipeStepRepository.findById(stepId)
                .orElseThrow(() -> {
                    log.warn("Step not found with ID: {}", stepId);
                    return new AppException(ErrorCode.STEP_NOT_FOUND);
                });

        if (!step.getRecipe().getRecipeId().equals(recipe.getRecipeId())) {
            log.warn("Step not found in recipe with ID: {}", recipeId);
            throw new AppException(ErrorCode.STEP_NOT_FOUND);
        }


        recipeStepRepository.delete(step);
        log.info("Step deleted successfully with ID: {}", stepId);
    }

    // This method is used to get image by stepId
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public StepResponse getImageByStepId(String stepId) {
        log.info("Get image by stepId: {}", stepId);
        RecipeStep step = recipeStepRepository.findById(stepId)
                .orElseThrow(() -> {
                    log.warn("Step not found with ID: {}", stepId);
                    return new AppException(ErrorCode.STEP_NOT_FOUND);
                });

        List<StepImageResponse> stepImageResponses = step.getImages().stream()
                .map(steptemp -> StepImageResponse.builder()
                        .imageUrl(storageImageService.getUrl(steptemp.getS3Key()))
                        .stepImageId(String.valueOf(steptemp.getStepImageId()))
                        .displayOrder(steptemp.getDisplayOrder())
                        .build())
                .toList();

        return StepResponse.builder()
                .stepId(String.valueOf(step.getStepId()))
                .stepNumber(step.getStepNumber())
                .description(step.getDescription())
                .images(stepImageResponses)
                .build();
    }


    // This method is used to get list of steps of recipe
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<StepResponse> getListOfSteps(String recipeId) {
        log.info("Get list of steps of recipe with ID: {}", recipeId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Recipe not found with ID: {}", recipeId);
                    return new AppException(ErrorCode.RECIPE_NOT_FOUND);
                });

        List<StepResponse> stepResponses = recipe.getRecipeSteps().stream()
                .map(steptemp -> this.getImageByStepId(String.valueOf(steptemp.getStepId())))
                .toList();

        return stepResponses;
    }


}
