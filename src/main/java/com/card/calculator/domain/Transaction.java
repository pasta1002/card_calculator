package com.card.calculator.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_card_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 지연 로딩 프록시 객체의 JSON 직렬화 노이즈 제거
    private UserCard userCard;

    private String merchantName;      // 가맹점명 (예: 스타벅스, 국세청)
    private String category;          // 카테고리 (LUNCH, TAX, UTILITY, GENERAL 등)
    private Long amount;              // 결제 금액
    private LocalDateTime approvedAt; // 결제 일시

    private boolean isInterestFree;   // 무이자 할부 여부 (true면 실적 제외 로직 적용)

    // 계산 후 업데이트될 결과 필드
    private boolean isPerformanceTarget; // 전월 실적 인정 여부
    private Long appliedBenefitAmount;   // 적용된 할인/적립 금액

    public void updateCalculationResult(boolean isPerformanceTarget, Long appliedBenefitAmount) {
        this.isPerformanceTarget = isPerformanceTarget;
        this.appliedBenefitAmount = appliedBenefitAmount;
    }
}