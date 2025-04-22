package vn.hvt.cook_master.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "recipe_steps")
public class RecipeStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long stepId;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
     Recipe recipe;

    @Column(name = "step_number", nullable = false)
     Integer stepNumber;
     String description;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<StepImage> images;

    public void addStepImage(StepImage stepImage) {
        if (this.images == null) {
            this.images = new java.util.ArrayList<>();
        }
        stepImage.setStep(this);
        this.images.add(stepImage);
    }

}
