package com.card.calculator.service;

import com.card.calculator.domain.Card;
import com.card.calculator.domain.Transaction;
import com.card.calculator.domain.UserCard;
import com.card.calculator.repository.CardRepository;
import com.card.calculator.repository.TransactionRepository;
import com.card.calculator.repository.UserCardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CardCalculatorServiceTest {

    @Autowired
    private CardCalculatorService cardCalculatorService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("우리카드: 세금 결제건은 실적 산정에서 제외(false)되어야 한다")
    void wooriCardTaxExclusionTest() {
        // given
        Card card = cardRepository.save(Card.builder().name("카드의정석 EVERY 1").company("우리카드").build());
        UserCard userCard = userCardRepository.save(UserCard.builder().userNum("TEST_USER").card(card).build());

        Transaction taxTx = transactionRepository.save(Transaction.builder()
                .userCard(userCard).merchantName("지방세").category("TAX").amount(100000L)
                .approvedAt(LocalDateTime.now()).isInterestFree(false).build());

        // when
        cardCalculatorService.calculatePerformanceAndBenefits("TEST_USER");

        // then
        Transaction result = transactionRepository.findById(taxTx.getId()).orElseThrow();
        assertThat(result.isPerformanceTarget()).isFalse(); // 세금은 실적 제외
        assertThat(result.getAppliedBenefitAmount()).isEqualTo(800L); // 0.8% 혜택 적용
    }

    @Test
    @DisplayName("하나카드: 무이자 할부 결제건은 실적 및 혜택에서 모두 제외되어야 한다")
    void hanaCardInterestFreeExclusionTest() {
        // given
        Card card = cardRepository.save(Card.builder().name("CLUB SK").company("하나카드").build());
        UserCard userCard = userCardRepository.save(UserCard.builder().userNum("TEST_HANA").card(card).build());

        Transaction noInterestTx = transactionRepository.save(Transaction.builder()
                .userCard(userCard).merchantName("전자제품").category("SHOPPING").amount(500000L)
                .approvedAt(LocalDateTime.now()).isInterestFree(true).build();

        // when
        cardCalculatorService.calculatePerformanceAndBenefits("TEST_HANA");

        // then
        Transaction result = transactionRepository.findById(noInterestTx.getId()).orElseThrow();
        assertThat(result.isPerformanceTarget()).isFalse(); // 무이자 -> 실적 제외
        assertThat(result.getAppliedBenefitAmount()).isEqualTo(0L); // 무이자 -> 혜택 0원
    }
}