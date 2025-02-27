package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

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
    @DisplayName("Get All Transactions - Success")
    void getAllTransactions() {
        given(transactionService.getAllTransactions()).willReturn(Collections.singletonList(transaction));

        List<Transaction> result = transactionController.getAllTransactions();

        assertThat(result).isEqualTo(Collections.singletonList(transaction));
    }

    @Test
    @DisplayName("Get Transaction By ID - Success")
    void givenTransactionId_whenGetTransactionById_thenReturnTransaction() {
        given(transactionService.getTransactionById(anyLong())).willReturn(transaction);

        ResponseEntity<Transaction> result = transactionController.getTransactionById(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(transaction);
    }

    @Test
    @DisplayName("Get Transaction By ID - Not Found")
    void givenNonExistingTransactionId_whenGetTransactionById_thenReturnNotFound() {
        given(transactionService.getTransactionById(anyLong())).willThrow(new EntityNotFoundException());

        ResponseEntity<Transaction> result = transactionController.getTransactionById(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @DisplayName("Update Transaction - Success")
    void givenUpdatedTransaction_whenUpdateTransaction_thenReturnUpdatedTransaction() {
        Transaction transaction1 = transaction;
        transaction1.setAmount(BigDecimal.valueOf(100));
        given(transactionService.updateTransaction(any(Transaction.class))).willReturn(transaction1);

        ResponseEntity<Transaction> updatedTransaction = transactionController.updateTransaction(transaction.getId(), transaction1);

        assertThat(updatedTransaction.getStatusCode().value()).isEqualTo(200);
        assertThat(updatedTransaction.getBody()).isEqualTo(transaction1);
    }

    @Test
    @DisplayName("Update Transaction - Not Found")
    void givenNonExistingTransaction_whenUpdateTransaction_thenReturnNotFound() {
        given(transactionService.updateTransaction(transaction)).willThrow(new EntityNotFoundException("not found"));

        ResponseEntity<Transaction> result = transactionController.updateTransaction(99L, transaction);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @DisplayName("Delete Transaction - Success")
    void givenTransactionId_whenDeleteTransaction_thenReturnOk() {
        willDoNothing().given(transactionService).deleteTransaction(transaction.getId());

        ResponseEntity<String> result = transactionController.deleteTransaction(transaction.getId());

        assertThat(result.getStatusCode().value()).isEqualTo(200); //OK
        assertThat(result.getBody()).isEqualTo("Transaction deleted successfully");
    }

    @Test
    @DisplayName("Delete Transaction - Not Found")
    void givenNonExistingTransactionId_whenDeleteTransaction_thenReturnBadRequest() {
        willThrow(new IllegalArgumentException("Transaction not found")).given(transactionService).deleteTransaction(99L);

        ResponseEntity<String> result = transactionController.deleteTransaction(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("Transaction not found");
    }

    /*
    @Test
    @DisplayName("Send Money - Success")
    void givenSendMoneyRequest_whenSendMoney_thenReturnOk() {
        when(transactionService.sendMoney(anyLong(), anyLong(), any(BigDecimal.class), anyString())).thenReturn(transaction);

        ResponseEntity<?> result = transactionController.sendMoney(sendMoneyRequest);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(transaction);
    }
     */
}