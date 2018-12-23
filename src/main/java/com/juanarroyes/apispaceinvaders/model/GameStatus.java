package com.juanarroyes.apispaceinvaders.model;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "game_status")
@EntityListeners(AuditingEntityListener.class)
public class GameStatus implements Serializable {

    @Id
    private String id;

    @Column(name = "maze_height")
    private int mazeHeight;

    @Column(name = "maze_width")
    private int mazeWidth;

    @Column(name = "last_objects_found")
    private String lastObjectsFound;

    @Column(name = "walls_found")
    private String wallsFound;

    @CreationTimestamp
    @Column(nullable =  false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    public void setMazeHeight(int mazeHeight) {
        this.mazeHeight = mazeHeight;
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public void setMazeWidth(int mazeWidth) {
        this.mazeWidth = mazeWidth;
    }

    public String getLastObjectsFound() {
        return lastObjectsFound;
    }

    public void setLastObjectsFound(String lastObjectsFound) {
        this.lastObjectsFound = lastObjectsFound;
    }

    public String getWallsFound() {
        return wallsFound;
    }

    public void setWallsFound(String wallsFound) {
        this.wallsFound = wallsFound;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
