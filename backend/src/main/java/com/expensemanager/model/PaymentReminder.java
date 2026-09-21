package com.expensemanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_reminders", indexes = {
        @Index(name = "idx_reminder_user_duedate", columnList = "user_id, due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReminder {

    public enum Status {
        UPCOMING,
        DUE,
        OVERDUE,
        PAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "bill_name", nullable = false, length = 100)
    private String billName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.UPCOMING;

    /**
     * Whether this reminder recreates itself next period once paid
     * (electricity, rent, subscriptions) vs. a one-off bill.
     * Left simple (monthly-only) for v1; extend with a proper
     * RecurrenceRule if other periods are needed later.
     */
    @Column(name = "is_recurring", nullable = false)
    @Builder.Default
    private boolean recurring = false;
}
