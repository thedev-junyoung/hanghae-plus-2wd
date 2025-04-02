package kr.hhplus.be.server.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "공통 API 응답 포맷")
public class ApiResponse<T> {

    // getter
    @Schema(description = "응답 메시지", example = "SUCCESS")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>("SUCCESS", null);
    }

}