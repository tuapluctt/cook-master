package vn.hvt.cook_master.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;


import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "follows")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long followId;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false) // Người theo dõi
    User follower;

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false) // Người được theo dõi
    User following;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Timestamp createdAt;
}
