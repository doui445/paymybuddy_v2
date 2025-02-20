package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .username("steveLander")
                .build();
        User user2 = User.builder()
                .username("doui445")
                .build();

        transaction = Transaction.builder()
                .sender(user1)
                .receiver(user2)
                .description("test transaction")
                .amount(BigDecimal.valueOf(25.5))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Get All Transactions")
    void givenTransactionsList_whenGetAllTransactions_thenReturnTransactionsList() {
        List<Transaction> transactions = Collections.singletonList(transaction);
        given(transactionRepository.findAll()).willReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(transactions, result);
    }

    @Test
    @DisplayName("Get Transaction By ID - Success")
    void givenTransactionId_whenGetTransactionById_thenReturnTransactionObject() {
        given(transactionRepository.findById(1L)).willReturn(Optional.of(transaction));

        Transaction retrievedTransaction = transactionService.getTransactionById(1L);

        assertThat(retrievedTransaction).isEqualTo(transaction);
    }

    @Test
    @DisplayName("Get Transaction By ID - Not Found")
    void givenNonExistingTransactionId_whenGetTransactionById_thenThrowException() {
        given(transactionRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(99L));
    }

    @Test
    @DisplayName("Save Transaction - Success")
    void givenTransactionObject_whenSaveTransaction_thenReturnTransactionObject() {
        given(transactionRepository.save(transaction)).willReturn(transaction);

        Transaction savedTransaction = transactionService.saveTransaction(transaction);

        assertThat(savedTransaction).isNotNull();
        assertEquals(savedTransaction, transaction);
    }

    @Test
    @DisplayName("Update Transaction - Success")
    void givenTransactionObject_whenUpdateTransaction_thenReturnUpdatedTransactionObject() {
        Transaction transaction1 = transaction;
        transaction1.setAmount(BigDecimal.valueOf(50)); // Update field

        given(transactionRepository.findById(transaction.getId())).willReturn(Optional.of(transaction));
        given(transactionRepository.save(any(Transaction.class))).willReturn(transaction1);

        Transaction updatedTransaction = transactionService.updateTransaction(transaction1);

        assertThat(updatedTransaction.getAmount()).isEqualTo(BigDecimal.valueOf(50)); // Verify update
    }

    @Test
    @DisplayName("Update Transaction - Not Found")
    void givenNonExistingTransaction_whenUpdateTransaction_thenThrowException() {
        Transaction nonExistingTransaction = transaction;
        nonExistingTransaction.setId(99L); // Non-existing id

        given(transactionRepository.findById(nonExistingTransaction.getId())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.updateTransaction(nonExistingTransaction));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Delete Transaction")
    void givenTransactionId_whenDeleteTransaction_thenVerifyDeletion() {
        willDoNothing().given(transactionRepository).deleteById(transaction.getId());

        transactionService.deleteTransaction(transaction.getId());

        verify(transactionRepository, times(1)).deleteById(transaction.getId());
    }

}