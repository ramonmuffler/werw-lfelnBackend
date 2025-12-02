package com.ausganslage.ausgangslageBackend.repository;

import com.ausganslage.ausgangslageBackend.model.Lobby;
import com.ausganslage.ausgangslageBackend.model.LobbyPlayer;
import com.ausganslage.ausgangslageBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LobbyPlayerRepository extends JpaRepository<LobbyPlayer, Long> {

    List<LobbyPlayer> findByLobby(Lobby lobby);

    Optional<LobbyPlayer> findByLobbyAndUser(Lobby lobby, User user);
}


