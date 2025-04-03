package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.api.ProductAPI;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.product.dto.response.PopularProductResponse;
import kr.hhplus.be.server.domain.product.dto.response.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.dto.response.ProductListResponse;
import kr.hhplus.be.server.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController implements ProductAPI {

    private final ProductService productService;

    @Override
    public ResponseEntity<CustomApiResponse<ProductListResponse>> getProducts(int page, int size, String sort) {
        ProductListResponse response = productService.getProducts(page, size, sort);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<ProductDetailResponse>> getProduct(Long productId) {
        ProductDetailResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<PopularProductResponse>> getPopularProducts() {
        PopularProductResponse response = productService.getPopularProducts();
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}