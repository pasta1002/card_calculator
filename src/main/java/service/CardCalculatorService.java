package com.card.calculator.service;

import com.card.calculator.domain.Card;
import com.card.calculator.domain.Transaction;
import com.card.calculator.domain.UserCard;
import com.card.calculator.repository.CardRepository;
import com.card.calculator.repository.TransactionRepository;
import com.card.calculator.repository.UserCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardCalculatorService {

    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final TransactionRepository transactionRepository;

    /**
     * 특정 사용자의 결제 내역을 바탕으로 전월 실적 및 할인/적립 혜택을 정밀 계산
     */
    @Transactional
    public void calculatePerformanceAndBenefits(String userNum) {
        UserCard userCard = userCardRepository.findByUserNum(userNum)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 카드입니다."));

        Card card = userCard.getCard();
        List<Transaction> transactions = transactionRepository.findByUserCardId(userCard.getId());

        for (Transaction tx : transactions) {
            boolean isTarget = checkPerformanceTarget(card.getCompany(), tx);
            long benefit = calculateBenefitAmount(card.getCompany(), tx, isTarget);

            tx.updateCalculationResult(isTarget, benefit);
        }
    }

    /**
     * 약관 기준: 전월 실적 인정 여부 판별
     */
    private boolean checkPerformanceTarget(String company, Transaction tx) {
        // 1. 공통 예외 조건: 무이자 할부 결제건은 실적 산정 제외
        if (tx.isInterestFree()) {
            return false;
        }

        // 2. 카테고리별 실적 제외 항목 (세금, 공과금 등)
        String category = tx.getCategory();
        if ("TAX".equals(category) || "UTILITY".equals(category) || "GIFT_CARD".equals(category)) {
            return false;
        }

        return true;
    }

    /**
     * 카드사별 혜택(할인/적립) 금액 계산
     */
    private long calculateBenefitAmount(String company, Transaction tx, boolean isPerformanceTarget) {
        // 무이자 할부건은 혜택 제외
        if (tx.isInterestFree()) {
            return 0L;
        }

        if ("우리카드".equals(company)) {
            // 우리카드 EVERY 1 기준: 기본 0.8% 할인
            return (long) (tx.getAmount() * 0.008);
        } else if ("하나카드".equals(company)) {
            // 하나카드 기준: 실적 인정 항목에 대해 1.0% 할인 (점심/가맹점 우대 등)
            if (isPerformanceTarget) {
                return (long) (tx.getAmount() * 0.010);
            }
        }

        return 0L;
    }
}