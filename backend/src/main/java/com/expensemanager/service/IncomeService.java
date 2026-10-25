package com.expensemanager.service;

import com.expensemanager.dto.IncomeRequest;
import com.expensemanager.dto.IncomeResponse;
import com.expensemanager.model.Income;
import com.expensemanager.model.User;
import com.expensemanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final HouseholdService householdService;

    @Transactional
    public IncomeResponse create(User user, IncomeRequest request) {
        Income income = Income.builder()
                .user(user)
                .source(request.source())
                .amount(request.amount())
                .date(request.date())
                .description(request.description())
                .build();
        return toResponse(incomeRepository.save(income));
    }

    public List<IncomeResponse> list(User user, LocalDate start, LocalDate end) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        List<Income> results = (start != null && end != null)
                ? incomeRepository.findByUserIdInAndDateBetweenOrderByDateDesc(userIds, start, end)
                : incomeRepository.findByUserIdInOrderByDateDesc(userIds);
        return results.stream().map(this::toResponse).toList();
    }

    public IncomeResponse get(User user, Long id) {
        return toResponse(findOwned(user, id));
    }

    @Transactional
    public IncomeResponse update(User user, Long id, IncomeRequest request) {
        Income income = findOwned(user, id);
        income.setSource(request.source());
        income.setAmount(request.amount());
        income.setDate(request.date());
        income.setDescription(request.description());
        return toResponse(income);
    }

    @Transactional
    public void delete(User user, Long id) {
        Income income = findOwned(user, id);
        incomeRepository.delete(income);
    }

    private Income findOwned(User user, Long id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income record not found"));
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        if (!userIds.contains(income.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Income record not found");
        }
        return income;
    }

    private IncomeResponse toResponse(Income income) {
        User u = income.getUser();
        String username = u != null ? u.getUsername() : null;
        String fullName = u != null && u.getFullName() != null && !u.getFullName().isBlank() ? u.getFullName() : username;
        return new IncomeResponse(
                income.getId(), income.getSource(), income.getAmount(),
                income.getDate(), income.getDescription(),
                username, fullName);
    }
}
