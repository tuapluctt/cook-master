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
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long ingredientId;


    @ManyToOne
    @JoinColumn(name = "recipe_id")
    Recipe recipe;

    @Column
    String name;

    @Column
    String quantity;

}
