package com.ausganslage.ausgangslageBackend.config;

import com.ausganslage.ausgangslageBackend.model.*;
import com.ausganslage.ausgangslageBackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final ChatMessageRepository chatMessageRepository;

    public DataLoader(UserRepository userRepository, LobbyRepository lobbyRepository,
                     LobbyPlayerRepository lobbyPlayerRepository, ChatMessageRepository chatMessageRepository) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void run(String... args) {
        // Benutzer erstellen
        User user1 = new User();
        user1.setUsername("alice");
        user1.setPasswordHash("hashed_password_1");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("bob");
        user2.setPasswordHash("hashed_password_2");
        user2 = userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("charlie");
        user3.setPasswordHash("hashed_password_3");
        user3 = userRepository.save(user3);

        // Lobby erstellen (mit user1 als Host)
        Lobby lobby = new Lobby();
        lobby.setCode("ABC123");
        lobby.setHost(user1);
        lobby.setStatus(LobbyStatus.WAITING);
        lobby = lobbyRepository.save(lobby);

        // Spieler zur Lobby hinzufügen
        LobbyPlayer player1 = new LobbyPlayer();
        player1.setLobby(lobby);
        player1.setUser(user1);
        player1.setDisplayName("Alice");
        player1.setAlive(true);
        lobbyPlayerRepository.save(player1);

        LobbyPlayer player2 = new LobbyPlayer();
        player2.setLobby(lobby);
        player2.setUser(user2);
        player2.setDisplayName("Bob");
        player2.setAlive(true);
        lobbyPlayerRepository.save(player2);

        LobbyPlayer player3 = new LobbyPlayer();
        player3.setLobby(lobby);
        player3.setUser(user3);
        player3.setDisplayName("Charlie");
        player3.setAlive(true);
        lobbyPlayerRepository.save(player3);

        // Chat-Nachrichten hinzufügen
        ChatMessage msg1 = new ChatMessage();
        msg1.setLobby(lobby);
        msg1.setUser(user1);
        msg1.setContent("Willkommen im Spiel!");
        chatMessageRepository.save(msg1);

        ChatMessage msg2 = new ChatMessage();
        msg2.setLobby(lobby);
        msg2.setUser(user2);
        msg2.setContent("Lasst uns spielen!");
        chatMessageRepository.save(msg2);
    }
}

