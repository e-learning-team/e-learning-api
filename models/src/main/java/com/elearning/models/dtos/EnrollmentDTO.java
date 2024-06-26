package com.elearning.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    private String id;
    @JsonProperty("course_id")
    private String courseId;
    @JsonProperty("course")
    private CourseDTO courseDTO;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("price_purchase")
    private String pricePurchase;
    @JsonProperty("current_course")
    private String currentCourse;

    @JsonProperty("current_millis")
    private Long currentMillis;

    @JsonProperty("percent_complete")
    private int percentComplete;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private Date createAt;

    @JsonProperty("update_by")
    private String updateBy;
    @JsonProperty("rating")
    private RatingDTO ratingDTO;
    @JsonProperty("update_at")
    private Date updatedAt;

    @JsonProperty("is_deleted")
    private boolean isDeleted = false;
    @JsonProperty("completed_course_ids")
    private List<String> completedCourseIds = new ArrayList<>();
}
