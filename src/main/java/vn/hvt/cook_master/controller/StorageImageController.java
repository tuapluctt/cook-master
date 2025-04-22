package vn.hvt.cook_master.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.hvt.cook_master.dto.ImageDTO;
import vn.hvt.cook_master.dto.response.ApiResponse;
import vn.hvt.cook_master.service.StorageImageService;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StorageImageController {
    StorageImageService storageImageService;

}
