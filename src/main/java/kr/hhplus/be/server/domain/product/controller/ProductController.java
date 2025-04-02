package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.api.ProductAPI;
import kr.hhplus.be.server.common.dto.Pagination;
import kr.hhplus.be.server.common.response.CustomApiResponse;
import kr.hhplus.be.server.domain.product.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductAPI {

    @Override
    public ResponseEntity<CustomApiResponse<ProductListResponse>> getProducts(int page, int size, String sort) {
        List<ProductDTO> products = List.of(
                ProductDTO.builder()
                        .id(1L)
                        .name("맥북 프로")
                        .price(BigDecimal.valueOf(3000000))
                        .stockQuantity(5)
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now())
                        .build(),
                ProductDTO.builder()
                        .id(2L)
                        .name("아이폰 15")
                        .price(BigDecimal.valueOf(1500000))
                        .stockQuantity(10)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        ProductListResponse response = ProductListResponse.builder()
                .products(products)
                .pagination(Pagination.builder()
                        .page(page)
                        .size(size)
                        .totalElements(products.size())
                        .totalPages(1)
                        .build())
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<ProductDetailResponse>> getProduct(Long productId) {
        ProductDTO product = ProductDTO.builder()
                .id(productId)
                .name("갤럭시 S24")
                .price(BigDecimal.valueOf(1200000))
                .stockQuantity(8)
                .createdAt(LocalDateTime.now().minusDays(7))
                .updatedAt(LocalDateTime.now())
                .build();

        ProductDetailResponse response = new ProductDetailResponse();
        response.setProduct(product);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<PopularProductResponse>> getPopularProducts() {
        List<PopularProductDTO> popularProducts = List.of(
                PopularProductDTO.builder()
                        .id(1L)
                        .name("에어팟 프로")
                        .price(BigDecimal.valueOf(300000))
                        .stockQuantity(12)
                        .salesCount(150)
                        .rank(1)
                        .createdAt(LocalDateTime.now().minusMonths(1))
                        .updatedAt(LocalDateTime.now())
                        .build(),
                PopularProductDTO.builder()
                        .id(2L)
                        .name("아이패드 에어")
                        .price(BigDecimal.valueOf(800000))
                        .stockQuantity(6)
                        .salesCount(120)
                        .rank(2)
                        .createdAt(LocalDateTime.now().minusDays(20))
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        PopularProductResponse response = new PopularProductResponse();
        response.setProducts(popularProducts);
        response.setPeriodStart(LocalDateTime.now().minusDays(3).toString());
        response.setPeriodEnd(LocalDateTime.now().toString());

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}