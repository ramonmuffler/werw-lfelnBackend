package com.ausganslage.ausgangslageBackend.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Simple statistics tracked per user
    @Column(nullable = false)
    private int villagerWins = 0;

    @Column(nullable = false)
    private int villagerLosses = 0;

    @Column(nullable = false)
    private int werewolfWins = 0;

    @Column(nullable = false)
    private int werewolfLosses = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getVillagerWins() {
        return villagerWins;
    }

    public void setVillagerWins(int villagerWins) {
        this.villagerWins = villagerWins;
    }

    public int getVillagerLosses() {
        return villagerLosses;
    }

    public void setVillagerLosses(int villagerLosses) {
        this.villagerLosses = villagerLosses;
    }

    public int getWerewolfWins() {
        return werewolfWins;
    }

    public void setWerewolfWins(int werewolfWins) {
        this.werewolfWins = werewolfWins;
    }

    public int getWerewolfLosses() {
        return werewolfLosses;
    }

    public void setWerewolfLosses(int werewolfLosses) {
        this.werewolfLosses = werewolfLosses;
    }

    @Transient
    public int getTotalWins() {
        return villagerWins + werewolfWins;
    }

    @Transient
    public int getTotalLosses() {
        return villagerLosses + werewolfLosses;
    }
}


