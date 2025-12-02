package com.ausganslage.ausgangslageBackend.repository;

import com.ausganslage.ausgangslageBackend.model.ChatMessage;
import com.ausganslage.ausgangslageBackend.model.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop50ByLobbyOrderByCreatedAtAsc(Lobby lobby);
}


