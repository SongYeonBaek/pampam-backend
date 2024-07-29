package com.example.pampam.category.service;

import com.example.pampam.category.model.entity.Category;
import com.example.pampam.category.model.request.PostRegisterCategoryReq;
import com.example.pampam.category.model.response.GetCategoryListRes;
import com.example.pampam.category.model.response.GetSearchProductToCategory;
import com.example.pampam.category.repository.CategoryRepository;
import com.example.pampam.common.BaseResponse;
import com.example.pampam.exception.EcommerceApplicationException;
import com.example.pampam.exception.ErrorCode;
import com.example.pampam.product.model.entity.Product;
import com.example.pampam.product.model.entity.ProductImage;
import com.example.pampam.product.repository.ProductRepository;
import com.example.pampam.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public BaseResponse<Object> searchCategoryType(String token, String categoryType) {
        token = JwtUtils.replaceToken(token);
        String authority = JwtUtils.getAuthority(token, secretKey);
        if (authority.equals("CONSUMER") || authority.equals("SELLER")) {
            List<GetSearchProductToCategory> productList = new ArrayList<>();
            Category category = categoryRepository.findByCategoryType(categoryType).orElseThrow(() ->
                    new EcommerceApplicationException(ErrorCode.NOT_FOUND_CATEGORY));

            for (Product product : category.getProducts()) {
                List<String> productImages = new ArrayList<>();
                for (ProductImage image : product.getImages()) {
                    productImages.add(image.getImagePath());
                }
                productList.add(GetSearchProductToCategory.buildProductToCategory(product, productImages));
            }

            return BaseResponse.successResponse("카테고리 조회 성공", productList);
        } else {
            throw new EcommerceApplicationException(ErrorCode.NOT_MATCH_AUTHORITY);
        }
    }

    public BaseResponse<Object> insertCategory(String token, Long idx, PostRegisterCategoryReq categoryInfo) {
        String authority = JwtUtils.getAuthority(token, secretKey);

        if (authority.equals("SELLER")) {
            Product product = productRepository.findById(idx).orElseThrow(() ->
                    new EcommerceApplicationException(ErrorCode.PRODUCT_NOT_FOUND));
            product.setCategory(Category.buildCategory(categoryInfo.getCategoryType()));
            return BaseResponse.successResponse("요청 성공", "카테고리 등록 완료");
        } else {
            throw new EcommerceApplicationException(ErrorCode.NOT_MATCH_AUTHORITY);
        }
    }

    public BaseResponse<List<GetCategoryListRes>> getCategoryList(String token) {
        String authority = JwtUtils.getAuthority(token, secretKey);
        if (authority.equals("CONSUMER") || authority.equals("SELLER")) {
            List<Category> categories = categoryRepository.findAll();
            List<GetCategoryListRes> categoryList = new ArrayList<>();

            for (Category category : categories) {
                categoryList.add(GetCategoryListRes.buildCategoryList(category));
            }

            return BaseResponse.successResponse("카테고리 조회 성공", categoryList);
        } else {
            throw new EcommerceApplicationException(ErrorCode.NOT_MATCH_AUTHORITY);
        }
    }
}
