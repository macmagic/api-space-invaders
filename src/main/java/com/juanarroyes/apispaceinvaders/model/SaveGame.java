package com.juanarroyes.apispaceinvaders.model;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "savegame")
@EntityListeners(AuditingEntityListener.class)
public class SaveGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id")
    private String playerId;

    @Column(name = "maze_height")
    private Integer mazeHeight;

    @Column(name = "maze_width")
    private Integer mazeWidth;

    @Column(name = "maze_objects_discovered")
    private String mazeObjectsDiscovered;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public SaveGame() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Integer getMazeHeight() {
        return mazeHeight;
    }

    public void setMazeHeight(Integer mazeHeight) {
        this.mazeHeight = mazeHeight;
    }

    public Integer getMazeWidth() {
        return mazeWidth;
    }

    public void setMazeWidth(Integer mazeWidth) {
        this.mazeWidth = mazeWidth;
    }

    public String getMazeObjectsDiscovered() {
        return mazeObjectsDiscovered;
    }

    public void setMazeObjectsDiscovered(String mazeObjectsDiscovered) {
        this.mazeObjectsDiscovered = mazeObjectsDiscovered;
    }
}
