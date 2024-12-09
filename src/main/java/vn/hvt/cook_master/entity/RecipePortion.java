package vn.hvt.cook_master.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;


import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity

@Table(name = "recipe_portions")
public class RecipePortion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portionId;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "portion_size", nullable = false)
    private String portionSize;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "portion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ingredient> ingredients;

}