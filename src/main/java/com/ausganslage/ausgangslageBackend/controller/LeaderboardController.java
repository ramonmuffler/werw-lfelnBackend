package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.User;
import com.ausganslage.ausgangslageBackend.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final UserRepository userRepository;

    public LeaderboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record LeaderboardEntry(
            Long id,
            String username,
            int villagerWins,
            int villagerLosses,
            int werewolfWins,
            int werewolfLosses,
            int totalWins
    ) {}

    @GetMapping
    public List<LeaderboardEntry> getLeaderboard() {
        return userRepository.findTop20ByOrderByVillagerWinsDescWerewolfWinsDesc()
                .stream()
                .map(this::toEntry)
                .toList();
    }

    private LeaderboardEntry toEntry(User u) {
        return new LeaderboardEntry(
                u.getId(),
                u.getUsername(),
                u.getVillagerWins(),
                u.getVillagerLosses(),
                u.getWerewolfWins(),
                u.getWerewolfLosses(),
                u.getTotalWins()
        );
    }
}


