package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.dto.response.PopularProductResponse;
import kr.hhplus.be.server.domain.product.dto.response.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.dto.response.ProductListResponse;

public interface ProductService {
    /**
     * 상품 목록을 조회합니다.
     */
    ProductListResponse getProducts(int page, int size, String sort);

    /**
     * 단일 상품의 상세 정보를 조회합니다.
     */
    ProductDetailResponse getProduct(Long productId);

    /**
     * 최근 3일간 가장 많이 팔린 상위 5개 상품을 조회합니다.
     */
    PopularProductResponse getPopularProducts();

    /**
     * 상품 재고를 감소시킵니다. (주문 처리 시 사용)
     * @return 재고 감소 성공 여부
     */
    boolean decreaseStock(Long productId, int quantity);
}