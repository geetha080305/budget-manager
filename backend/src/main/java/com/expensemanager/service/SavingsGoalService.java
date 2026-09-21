package com.expensemanager.service;

import com.expensemanager.dto.ContributionRequest;
import com.expensemanager.dto.SavingsGoalRequest;
import com.expensemanager.dto.SavingsGoalResponse;
import com.expensemanager.model.SavingsGoal;
import com.expensemanager.model.User;
import com.expensemanager.repository.SavingsGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;

    @Transactional
    public SavingsGoalResponse create(User user, SavingsGoalRequest request) {
        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .targetDate(request.targetDate())
                .build(); // savedAmount defaults to 0 via @Builder.Default
        return toResponse(savingsGoalRepository.save(goal));
    }

    public List<SavingsGoalResponse> list(User user) {
        return savingsGoalRepository.findByUserIdOrderByIdDesc(user.getId())
                .stream().map(this::toResponse).toList();
    }

    public SavingsGoalResponse get(User user, Long id) {
        return toResponse(findOwned(user, id));
    }

    // Edits the goal's definition (name/target/date) - not savedAmount.
    // Progress is only changed via contribute(), so it can never be set
    // to an inconsistent value by accident.
    @Transactional
    public SavingsGoalResponse update(User user, Long id, SavingsGoalRequest request) {
        SavingsGoal goal = findOwned(user, id);
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setTargetDate(request.targetDate());
        return toResponse(goal);
    }

    @Transactional
    public SavingsGoalResponse contribute(User user, Long id, ContributionRequest request) {
        SavingsGoal goal = findOwned(user, id);
        goal.setSavedAmount(goal.getSavedAmount().add(request.amount()));
        return toResponse(goal);
    }

    @Transactional
    public void delete(User user, Long id) {
        savingsGoalRepository.delete(findOwned(user, id));
    }

    private SavingsGoal findOwned(User user, Long id) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings goal not found");
        }
        return goal;
    }

    private SavingsGoalResponse toResponse(SavingsGoal goal) {
        return new SavingsGoalResponse(
                goal.getId(), goal.getName(), goal.getTargetAmount(),
                goal.getSavedAmount(), goal.getRemainingAmount(), goal.getTargetDate());
    }
}
