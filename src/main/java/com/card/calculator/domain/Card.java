// 카드 기본 정보
package com.card.calculator.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 예: "카드의정석 EVERY 1", "TOEVER 1Q My Lunch"
    private String company;     // 예: "우리카드", "하나카드"
}