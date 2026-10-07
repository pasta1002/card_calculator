package com.card.calculator.controller;

import com.card.calculator.domain.Transaction;
import com.card.calculator.repository.TransactionRepository;
import com.card.calculator.repository.UserCardRepository;
import com.card.calculator.service.CardCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
public class CardCalculatorController {

    private final CardCalculatorService cardCalculatorService;
    private final UserCardRepository userCardRepository;
    private final TransactionRepository transactionRepository;

    /**
     * 특정 사용자의 결제 내역 계산 실행 및 결과 조회 API
     */
    @PostMapping("/calculate/{userNum}")
    public List<Transaction> calculateAndGetResults(@PathVariable String userNum) {
        // 1. 계산 서비스 실행
        cardCalculatorService.calculatePerformanceAndBenefits(userNum);

        // 2. 결과가 반영된 트랜잭션 목록 조회
        Long userCardId = userCardRepository.findByUserNum(userNum)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                .getId();

        return transactionRepository.findByUserCardId(userCardId);
    }
}