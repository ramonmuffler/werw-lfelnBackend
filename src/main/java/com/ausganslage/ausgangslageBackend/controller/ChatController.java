package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.ChatMessage;
import com.ausganslage.ausgangslageBackend.model.Lobby;
import com.ausganslage.ausgangslageBackend.model.User;
import com.ausganslage.ausgangslageBackend.repository.ChatMessageRepository;
import com.ausganslage.ausgangslageBackend.repository.LobbyRepository;
import com.ausganslage.ausgangslageBackend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;

    public ChatController(
            ChatMessageRepository chatMessageRepository,
            LobbyRepository lobbyRepository,
            UserRepository userRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
    }

    public record SendMessageRequest(
            @NotNull Long userId,
            @NotBlank String message
    ) {}

    public record ChatMessageDto(
            Long id,
            Long userId,
            String username,
            String content,
            String createdAt
    ) {}

    @PostMapping("/{lobbyCode}")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageDto sendMessage(
            @PathVariable String lobbyCode,
            @Valid @RequestBody SendMessageRequest request
    ) {
        Lobby lobby = lobbyRepository.findByCode(lobbyCode)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChatMessage msg = new ChatMessage();
        msg.setLobby(lobby);
        msg.setUser(user);
        msg.setContent(request.message().trim());

        ChatMessage saved = chatMessageRepository.save(msg);
        return toDto(saved);
    }

    @GetMapping("/{lobbyCode}")
    public List<ChatMessageDto> getMessages(@PathVariable String lobbyCode) {
        Lobby lobby = lobbyRepository.findByCode(lobbyCode)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
        return chatMessageRepository.findTop50ByLobbyOrderByCreatedAtAsc(lobby)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ChatMessageDto toDto(ChatMessage msg) {
        return new ChatMessageDto(
                msg.getId(),
                msg.getUser().getId(),
                msg.getUser().getUsername(),
                msg.getContent(),
                msg.getCreatedAt().toString()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
}


