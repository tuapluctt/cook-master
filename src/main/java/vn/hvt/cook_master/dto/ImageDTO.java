package vn.hvt.cook_master.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageDTO {
    String url;
    String imageKey;
    String presignedUrl;
    String ImageS3Bucket;
}
