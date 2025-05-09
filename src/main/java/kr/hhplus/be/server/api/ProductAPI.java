package kr.hhplus.be.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.exception.ApiErrorResponse;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.product.dto.response.PopularProductResponse;
import kr.hhplus.be.server.domain.product.dto.response.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.dto.response.ProductListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "Product", description = "상품 API")
@RequestMapping("/api/v1/products")
public interface ProductAPI {

    @Operation(summary = "상품 목록 조회", description = "전체 상품 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    ResponseEntity<CustomApiResponse<ProductListResponse>> getProducts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: name,asc 또는 price,desc)", example = "name,asc")
            @RequestParam(required = false) String sort
    );

    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = ProductDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "상품 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{productId}")
    ResponseEntity<CustomApiResponse<ProductDetailResponse>> getProduct(
            @Parameter(description = "상품 ID", example = "1001", required = true)
            @PathVariable Long productId
    );

    @Operation(summary = "인기 판매 상품 조회", description = "최근 3일간 판매량이 높은 상위 5개 스니커즈 상품 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = PopularProductResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/popular")
    ResponseEntity<CustomApiResponse<PopularProductResponse>> getPopularProducts();
}