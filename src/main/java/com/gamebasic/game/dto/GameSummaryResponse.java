package com.gamebasic.game.dto;


import com.gamebasic.game.entity.GamePhase;
import com.gamebasic.game.entity.GameStatus;
import lombok.Getter;

@Getter
public class GameSummaryResponse {
    private Long id;
    private String playerName;
    private Integer currentFloor;
    private Integer currentHp;
    private GamePhase phase;
    private GameStatus status;

    public GameSummaryResponse(
            Long id,
            String playerName,
            Integer currentFloor,
            Integer currentHp,
            GamePhase phase,
            GameStatus status
    ) {
        this.id = id;
        this.playerName = playerName;
        this.currentFloor = currentFloor;
        this.currentHp = currentHp;
        this.phase = phase;
        this.status = status;
    }
}
