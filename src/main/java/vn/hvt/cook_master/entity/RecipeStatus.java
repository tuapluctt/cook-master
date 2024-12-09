package vn.hvt.cook_master.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RecipeStatus {
    @JsonProperty("draft")
    DRAFT,
    @JsonProperty("published")
    PUBLISHED,
    @JsonProperty("approved")
    APPROVED
}
