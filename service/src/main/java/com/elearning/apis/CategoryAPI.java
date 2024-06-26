package com.elearning.apis;

import com.elearning.controller.CategoryController;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.searchs.ParameterSearchCategory;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCategoryBuildType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category API")
@ExtensionMethod(Extensions.class)
public class CategoryAPI {
    @Autowired
    CategoryController categoryController;

    @Operation(summary = "Danh sách danh mục")
    @GetMapping
    public List<CategoryDTO> getCategories(@RequestParam(value = "build_type") EnumCategoryBuildType buildType,
                                           @RequestParam(value = "level", required = false) Integer level,
                                           @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                           @RequestParam(value = "categories_ids", required = false) List<String> categoriesIds,
                                           @RequestParam(value = "count_courses", required = false) Boolean countCourses,
                                           @RequestParam(value = "parent_ids", required = false) List<String> parentIds) {
        ParameterSearchCategory parameterSearchCategory = new ParameterSearchCategory();
        if (buildType != null) {
            parameterSearchCategory.setBuildType(buildType.name());
        }
        if (level != null) {
            parameterSearchCategory.setLevel(level);
        }
        if (isDeleted != null) {
            parameterSearchCategory.setIsDeleted(isDeleted);
        }
        if (!categoriesIds.isNullOrEmpty()) {
            parameterSearchCategory.setCategoriesIds(categoriesIds);
        }
        if (!parentIds.isNullOrEmpty()) {
            parameterSearchCategory.setParentIds(parentIds);
        }
        if(countCourses != null && countCourses){
            parameterSearchCategory.setCountTotalCourse(true);
        }else{
            parameterSearchCategory.setCountTotalCourse(false);
        }
        return categoryController.searchCategoryDTOS(parameterSearchCategory);
    }
    @GetMapping("/top-{value}-categories")
    public List<CategoryDTO> getTopCategories(@PathVariable(value = "value") int top) {
        return categoryController.getTopCategories(top);
    }
    @Operation(summary = "Thêm danh mục")
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public CategoryDTO createCategory(@RequestBody CategoryDTO categoryDTO) {
        return categoryController.createCategory(categoryDTO);
    }

    @Operation(summary = "Sửa danh mục")
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public void updateCategory(CategoryDTO categoryDTO) {
        categoryController.updateCategory(categoryDTO);
    }

    @Operation(summary = "Cập nhật tên danh mục")
    @PutMapping("/update-name/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public void updateCategoryName(@PathVariable(value = "id") String id,
                                   @RequestBody String name) {
        categoryController.updateCategoryName(id, name);
    }

    @Operation(summary = "Xoá danh mục")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public void deleteCategory(@PathVariable(value = "id") String id) {
        categoryController.deleteCategory(id);
    }
}
