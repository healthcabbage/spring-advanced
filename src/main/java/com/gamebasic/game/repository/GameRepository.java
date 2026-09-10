package com.gamebasic.game.repository;

import com.gamebasic.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    // TODO (Lv 7): API 명세의 조회 요구사항에 필요한 메서드를 설계하세요.

    List<Game> findAllByOrderByIdDesc();
}
