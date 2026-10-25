package com.expensemanager.service;

import com.expensemanager.dto.HouseholdRequest;
import com.expensemanager.dto.HouseholdResponse;
import com.expensemanager.dto.JoinHouseholdRequest;
import com.expensemanager.model.Household;
import com.expensemanager.model.User;
import com.expensemanager.repository.HouseholdRepository;
import com.expensemanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    public HouseholdResponse getMyHousehold(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Household household = managedUser.getHousehold();
        if (household == null) {
            return null;
        }
        return toResponse(household);
    }

    @Transactional
    public HouseholdResponse createHousehold(User user, HouseholdRequest request) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (managedUser.getHousehold() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already a member of a household");
        }

        Household household = Household.builder()
                .name(request.name().trim())
                .inviteCode(Household.generateCode())
                .build();
        household = householdRepository.save(household);

        managedUser.setHousehold(household);
        userRepository.save(managedUser);

        return toResponse(household);
    }

    @Transactional
    public HouseholdResponse joinHousehold(User user, JoinHouseholdRequest request) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (managedUser.getHousehold() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already a member of a household");
        }

        Household household = householdRepository.findByInviteCode(request.inviteCode().trim().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid household invite code"));

        managedUser.setHousehold(household);
        userRepository.save(managedUser);

        return toResponse(household);
    }

    @Transactional
    public void leaveHousehold(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (managedUser.getHousehold() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a member of any household");
        }

        Household household = managedUser.getHousehold();
        managedUser.setHousehold(null);
        userRepository.save(managedUser);

        // If no members left, delete the household
        List<User> remaining = userRepository.findByHouseholdId(household.getId());
        if (remaining.isEmpty()) {
            householdRepository.delete(household);
        }
    }

    /**
     * Resolves the effective user IDs to query data for:
     * If user belongs to a household, returns all member user IDs in that household.
     * If user is individual, returns just [user.id].
     */
    public List<Long> getEffectiveUserIds(User user) {
        User managedUser = userRepository.findById(user.getId()).orElse(user);
        if (managedUser.getHousehold() != null) {
            return userRepository.findByHouseholdId(managedUser.getHousehold().getId())
                    .stream()
                    .map(User::getId)
                    .toList();
        }
        return List.of(user.getId());
    }

    private HouseholdResponse toResponse(Household household) {
        List<User> members = userRepository.findByHouseholdId(household.getId());
        List<HouseholdResponse.MemberSummary> memberSummaries = members.stream()
                .map(m -> new HouseholdResponse.MemberSummary(m.getId(), m.getUsername(), m.getFullName()))
                .toList();

        return new HouseholdResponse(
                household.getId(),
                household.getName(),
                household.getInviteCode(),
                memberSummaries
        );
    }
}
