package com.expensemanager.repository;

import com.expensemanager.model.PaymentReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentReminderRepository extends JpaRepository<PaymentReminder, Long> {

    List<PaymentReminder> findByUserIdOrderByDueDateAsc(Long userId);
    List<PaymentReminder> findByUserIdInOrderByDueDateAsc(List<Long> userIds);

    List<PaymentReminder> findByUserIdAndDueDateBetweenOrderByDueDateAsc(
            Long userId, LocalDate start, LocalDate end);
    List<PaymentReminder> findByUserIdInAndDueDateBetweenOrderByDueDateAsc(
            List<Long> userIds, LocalDate start, LocalDate end);

    List<PaymentReminder> findByUserIdAndStatusOrderByDueDateAsc(
            Long userId, PaymentReminder.Status status);
    List<PaymentReminder> findByUserIdInAndStatusOrderByDueDateAsc(
            List<Long> userIds, PaymentReminder.Status status);
}
