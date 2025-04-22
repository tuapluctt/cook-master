package vn.hvt.cook_master.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true, nullable = false)
     String username;

    @Column(name = "full_name", nullable = false)
     String fullName; // Họ và tên đầy đủ

    @Column(name = "nick_name")
     String nickName;

    @Column(unique = true, nullable = false)
     String email;

    @Column(nullable = false)
     String passwordHash;

    @Column(name = "address")
     String address;

    @Column(name = "description")
     String description;

    @Column(name = "avatar_s3_key")
     String avatarS3Key;

    @Column(name = "avatar_s3_bucket")
     String avatarS3Bucket;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
     Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
     Timestamp updatedAt;

    @OneToMany(mappedBy = "user")
     List<Recipe> recipes;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<Role> roles;

}
