package com.card.calculator.config;

import com.card.calculator.domain.Card;
import com.card.calculator.domain.Transaction;
import com.card.calculator.domain.UserCard;
import com.card.calculator.repository.CardRepository;
import com.card.calculator.repository.TransactionRepository;
import com.card.calculator.repository.UserCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. 카드 등록
        Card wooriCard = cardRepository.save(Card.builder().name("카드의정석 EVERY 1").company("우리카드").build());
        Card hanaCard = cardRepository.save(Card.builder().name("CLUB SK").company("하나카드").build());

        // 2. 사용자 카드 등록
        UserCard user1 = userCardRepository.save(UserCard.builder().userNum("USER_WOORI").card(wooriCard).build());
        UserCard user2 = userCardRepository.save(UserCard.builder().userNum("USER_HANA").card(hanaCard).build());

        // 3. 결제 내역 등록 (우리카드 사용자)
        transactionRepository.save(Transaction.builder()
                .userCard(user1).merchantName("스타벅스").category("COFFEE").amount(10000L)
                .approvedAt(LocalDateTime.now()).isInterestFree(false).build());

        transactionRepository.save(Transaction.builder()
                .userCard(user1).merchantName("국세청 (지방세)").category("TAX").amount(150000L)
                .approvedAt(LocalDateTime.now()).isInterestFree(false).build()); // 실적 제외 대상

        // 4. 결제 내역 등록 (하나카드 사용자)
        transactionRepository.save(Transaction.builder()
                .userCard(user2).merchantName("구로식당").category("LUNCH").amount(50000L)
                .approvedAt(LocalDateTime.now()).isInterestFree(false).build());

        transactionRepository.save(Transaction.builder()
                .userCard(user2).merchantName("가전제품 (무이자)").category("SHOPPING").amount(300000L)
                .approvedAt(LocalDateTime.now()).isInterestFree(true).build()); // 무이자 -> 실적/혜택 제외
    }
}