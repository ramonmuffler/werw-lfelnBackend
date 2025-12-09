package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.*;
import com.ausganslage.ausgangslageBackend.repository.LobbyPlayerRepository;
import com.ausganslage.ausgangslageBackend.repository.LobbyRepository;
import com.ausganslage.ausgangslageBackend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private static final int MIN_PLAYERS = 5;
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final UserRepository userRepository;

    public LobbyController(
            LobbyRepository lobbyRepository,
            LobbyPlayerRepository lobbyPlayerRepository,
            UserRepository userRepository
    ) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.userRepository = userRepository;
    }

    public record CreateLobbyRequest(
            @NotNull Long hostUserId,
            @NotBlank String hostDisplayName
    ) {}

    public record JoinLobbyRequest(
            @NotNull Long userId,
            @NotBlank String displayName
    ) {}

    public record StartGameRequest(
            @NotNull Long hostUserId
    ) {}

    public record FinishGameRequest(
            @NotBlank String winnerSide // "VILLAGERS" or "WEREWOLVES"
    ) {}

    public record LobbyPlayerDto(
            Long id,
            Long userId,
            String displayName,
            String role,
            boolean alive,
            boolean isHost
    ) {}

    public record LobbyDto(
            Long id,
            String code,
            LobbyStatus status,
            Long hostUserId,
            List<LobbyPlayerDto> players
    ) {}

    public record StartRoundRequest(
            @NotNull Long userId
    ) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LobbyDto createLobby(@Valid @RequestBody CreateLobbyRequest request) {
        User host = userRepository.findById(request.hostUserId())
                .orElseThrow(() -> new IllegalArgumentException("Host user not found"));

        Lobby lobby = new Lobby();
        lobby.setHost(host);
        lobby.setCode(generateUniqueCode());

        Lobby savedLobby = lobbyRepository.save(lobby);

        LobbyPlayer hostPlayer = new LobbyPlayer();
        hostPlayer.setLobby(savedLobby);
        hostPlayer.setUser(host);
        hostPlayer.setDisplayName(request.hostDisplayName());
        hostPlayer.setAlive(true);
        lobbyPlayerRepository.save(hostPlayer);

        return toDto(savedLobby);
    }

    @PostMapping("/{code}/join")
    public LobbyDto joinLobby(@PathVariable String code, @Valid @RequestBody JoinLobbyRequest request) {
        Lobby lobby = lobbyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found"));

        if (lobby.getStatus() != LobbyStatus.WAITING) {
            throw new IllegalArgumentException("Lobby is not open for joining");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        lobbyPlayerRepository.findByLobbyAndUser(lobby, user)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("User already joined this lobby");
                });

        LobbyPlayer player = new LobbyPlayer();
        player.setLobby(lobby);
        player.setUser(user);
        player.setDisplayName(request.displayName());
        player.setAlive(true);
        lobbyPlayerRepository.save(player);

        return toDto(lobby);
    }

    @GetMapping("/{code}")
    public LobbyDto getLobby(@PathVariable String code) {
        Lobby lobby = lobbyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
        return toDto(lobby);
    }

    @PostMapping("/{code}/start")
    public Object startRound(@PathVariable String code, @Valid @RequestBody StartRoundRequest request) {
        Lobby lobby = lobbyRepository.findByCode(code)
                .orElse(null);
        if (lobby == null) {
            return Map.of("error", "Lobby nicht gefunden");
        }
        if (lobby.getStatus() == LobbyStatus.IN_GAME || lobby.getStatus() == LobbyStatus.FINISHED) {
            return Map.of("error", "Lobby ist bereits gestartet oder beendet");
        }
        if (!Objects.equals(lobby.getHost().getId(), request.userId())) {
            return Map.of("error", "Nur der Host kann die Runde starten");
        }
        // Spieler auf alive = true setzen
        List<LobbyPlayer> players = lobbyPlayerRepository.findByLobby(lobby);
        for (LobbyPlayer p : players) {
            p.setAlive(true);
        }
        lobbyPlayerRepository.saveAll(players);
        // Status setzen
        lobby.setStatus(LobbyStatus.IN_GAME);
        lobbyRepository.save(lobby);
        return toDto(lobby);
    }

    @PostMapping("/{code}/finish")
    public LobbyDto finishGame(@PathVariable String code, @Valid @RequestBody FinishGameRequest request) {
        Lobby lobby = lobbyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found"));

        List<LobbyPlayer> players = lobbyPlayerRepository.findByLobby(lobby);
        boolean villagersWon = "VILLAGERS".equalsIgnoreCase(request.winnerSide());

        for (LobbyPlayer lp : players) {
            User user = lp.getUser();
            boolean isWolfSide = lp.getRole() == GameRole.WEREWOLF;

            if (villagersWon) {
                if (isWolfSide) {
                    user.setWerewolfLosses(user.getWerewolfLosses() + 1);
                } else {
                    user.setVillagerWins(user.getVillagerWins() + 1);
                }
            } else {
                if (isWolfSide) {
                    user.setWerewolfWins(user.getWerewolfWins() + 1);
                } else {
                    user.setVillagerLosses(user.getVillagerLosses() + 1);
                }
            }
            userRepository.save(user);
        }

        lobby.setStatus(LobbyStatus.FINISHED);
        lobbyRepository.save(lobby);

        return toDto(lobby);
    }

    @GetMapping("/{code}/me/{userId}")
    public LobbyPlayerDto getMyPlayer(@PathVariable String code, @PathVariable Long userId) {
        Lobby lobby = lobbyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LobbyPlayer player = lobbyPlayerRepository.findByLobbyAndUser(lobby, user)
                .orElseThrow(() -> new IllegalArgumentException("Player not in lobby"));

        return toPlayerDto(player, lobby);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = randomCode(6);
            if (!lobbyRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate unique lobby code");
    }

    private String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(CODE_CHARS.length());
            sb.append(CODE_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    private LobbyDto toDto(Lobby lobby) {
        List<LobbyPlayer> players = lobbyPlayerRepository.findByLobby(lobby);
        List<LobbyPlayerDto> dtos = players.stream()
                .map(p -> toPlayerDto(p, lobby))
                .toList();

        return new LobbyDto(
                lobby.getId(),
                lobby.getCode(),
                lobby.getStatus(),
                lobby.getHost().getId(),
                dtos
        );
    }

    private LobbyPlayerDto toPlayerDto(LobbyPlayer p, Lobby lobby) {
        boolean isHost = Objects.equals(lobby.getHost().getId(), p.getUser().getId());
        return new LobbyPlayerDto(
                p.getId(),
                p.getUser().getId(),
                p.getDisplayName(),
                p.getRole() != null ? p.getRole().name() : null,
                p.isAlive(),
                isHost
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
}
