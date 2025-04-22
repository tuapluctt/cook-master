package vn.hvt.cook_master.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long recipeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
     User user;

    @Column(name = "title")
     String title;

    @Column(name = "description")
     String description;

    @Column(name = "image_s3_key")
    private String ImageS3Key;

    @Column(name = "image_s3_bucket")
    private String ImageS3Bucket;

    @Column(name = "cook_time")
     String cookTime;


    @Enumerated(EnumType.STRING)
     RecipeStatus status;

    @Column(name = "portions")
     String portions;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
     Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
     Timestamp updatedAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
     List<RecipeStep> recipeSteps;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Ingredient> Ingredients;

    public void addIngredient(Ingredient ingredient) {
        if (this.Ingredients == null) {
            this.Ingredients = new java.util.ArrayList<>();
        }
        ingredient.setRecipe(this); // Set the recipe for the ingredient
        this.Ingredients.add(ingredient); // Add the ingredient to the recipe's list
    }

    public void addStep(RecipeStep step) {
    }
}


