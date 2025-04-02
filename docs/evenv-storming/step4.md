## Step 4 - Aggregate Event Storming
```mermaid
flowchart LR
%% 스타일 정의
    classDef event fill:#FFF3CD,stroke:#FF9800,color:#333,font-weight:bold;

%% Order Aggregate
    subgraph Order Aggregate
        O1[주문 요청이 접수되었다]:::event
        O2[주문 항목이 검증되었다]:::event
        O3[주문 금액이 계산되었다]:::event
        O4[결제가 완료되었다]:::event
        O5[주문이 생성되었다]:::event
        O6[주문 정보가 외부 플랫폼으로 전송되었다]:::event
    end

%% Product Aggregate
    subgraph Product Aggregate
        P1[상품 재고가 충분하다]:::event
    end

%% Coupon Aggregate
    subgraph Coupon Aggregate
        C1[쿠폰이 유효하다]:::event
        C2[쿠폰이 사용되었다]:::event
    end

%% Balance Aggregate
    subgraph Balance Aggregate
        B1[사용자 잔액이 확인되었다]:::event
        B2[잔액이 충분하다]:::event
        B3[결제 금액이 차감되었다]:::event
    end

```