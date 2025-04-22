package vn.hvt.cook_master.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "step_images")
public class StepImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepImageId;

    @ManyToOne
    @JoinColumn(name = "step_id", nullable = false)
    private RecipeStep step;

    private String s3Key;
    private String s3Bucket;
    private Integer displayOrder;

    // Các getter/setter
}
