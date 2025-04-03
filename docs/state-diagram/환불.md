
```mermaid
stateDiagram-v2
    [*] --> REFUND_REQUESTED

    REFUND_REQUESTED --> PROCESSING: 환불 처리 중
    PROCESSING --> REFUNDED: 환불 성공
    PROCESSING --> FAILED: 환불 실패
    FAILED --> PROCESSING: 재시도
    FAILED --> MANUAL: 수동 환불 처리

    REFUNDED --> [*]
    MANUAL --> [*]

```