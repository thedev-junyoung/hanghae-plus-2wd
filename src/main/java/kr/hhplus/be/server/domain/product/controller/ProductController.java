package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.api.ProductAPI;
import kr.hhplus.be.server.common.dto.Pagination;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.product.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                        .name("Nike Air Jordan 1 Retro High OG")
                        .brand("Nike")
                        .price(BigDecimal.valueOf(299000))
                        .originalPrice(BigDecimal.valueOf(250000))
                        .stockQuantity(8)
                        .releaseDate(LocalDate.of(2025, 1, 15))
                        .availableSizes(List.of(250, 255, 260, 265, 270))
                        .description("조던 1 클래식 시카고 컬러")
                        .imageUrl("/images/products/aj1-chicago.jpg")
                        .createdAt(LocalDateTime.now().minusDays(7))
                        .updatedAt(LocalDateTime.now())
                        .build(),
                ProductDTO.builder()
                        .id(2L)
                        .name("Adidas Yeezy Boost 350 V2")
                        .brand("Adidas")
                        .price(BigDecimal.valueOf(289000))
                        .originalPrice(BigDecimal.valueOf(240000))
                        .stockQuantity(10)
                        .releaseDate(LocalDate.of(2024, 12, 1))
                        .availableSizes(List.of(255, 260, 265, 270, 275, 280))
                        .description("이지부스트 제브라 리스탁")
                        .imageUrl("/images/products/yeezy-zebra.jpg")
                        .createdAt(LocalDateTime.now().minusDays(14))
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
                .name("Nike Dunk Low Retro")
                .brand("Nike")
                .price(BigDecimal.valueOf(139000))
                .originalPrice(BigDecimal.valueOf(129000))
                .stockQuantity(15)
                .releaseDate(LocalDate.of(2025, 3, 1))
                .availableSizes(List.of(250, 260, 270))
                .description("나이키 덩크 로우 블랙 화이트")
                .imageUrl("/images/products/dunk-low-bw.jpg")
                .createdAt(LocalDateTime.now().minusDays(3))
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
                        .name("Nike Air Force 1 '07")
                        .brand("Nike")
                        .price(BigDecimal.valueOf(129000))
                        .originalPrice(BigDecimal.valueOf(119000))
                        .stockQuantity(20)
                        .salesCount(220)
                        .rank(1)
                        .popularityScore(96.5)
                        .resellPriceChangeRate(18.3)
                        .avgResellPrice(BigDecimal.valueOf(160000))
                        .availableSizes(List.of(250, 260, 270, 280))
                        .imageUrl("/images/products/airforce-white.jpg")
                        .description("나이키 에어포스1 올백")
                        .createdAt(LocalDateTime.now().minusDays(25))
                        .updatedAt(LocalDateTime.now())
                        .build(),
                PopularProductDTO.builder()
                        .id(2L)
                        .name("New Balance 990v6")
                        .brand("New Balance")
                        .price(BigDecimal.valueOf(259000))
                        .originalPrice(BigDecimal.valueOf(249000))
                        .stockQuantity(12)
                        .salesCount(180)
                        .rank(2)
                        .popularityScore(91.2)
                        .resellPriceChangeRate(12.4)
                        .avgResellPrice(BigDecimal.valueOf(310000))
                        .availableSizes(List.of(255, 265, 275))
                        .imageUrl("/images/products/nb990v6.jpg")
                        .description("990v6 그레이 Made in USA")
                        .createdAt(LocalDateTime.now().minusDays(18))
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