package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.response.CategoryTreeResponse;
import az.company.ecommerceapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryTreeResponse> getCategories() {
        return categoryService.getCategoryTree();
    }
}
