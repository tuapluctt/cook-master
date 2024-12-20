package vn.hvt.cook_master.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Column(name = "public_id", nullable = false)
     String publicId;

    @Column(name = "image_url", nullable = false)
     String imageUrl;

    @Column(name = "user_id", nullable = false)
     Long userId; // Người upload ảnh (để kiểm tra quyền)

    @Column(name = "recipe_id")
     Long recipeId; // ID của recipe (nếu là ảnh recipe)

    @Column(name = "step_id")
     Long stepId; // ID của step (nếu là ảnh step)

    @Column(name = "type")
     String type; // recipe, step, user

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
     Timestamp createdAt;
}