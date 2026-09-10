package com.gamebasic.game.service;

import com.gamebasic.common.exception.GameNotFoundException;
import com.gamebasic.game.dto.CreateRequest;
import com.gamebasic.game.dto.GameDetailResponse;
import com.gamebasic.game.dto.ProgressRequest;
import com.gamebasic.game.dto.GameSummaryResponse;
import com.gamebasic.game.entity.Game;
import com.gamebasic.game.repository.GameRepository;
import com.gamebasic.runcard.dto.CardResponse;
import com.gamebasic.runcard.dto.RunCardRequest;
import com.gamebasic.runcard.entity.RunCard;
import com.gamebasic.runcard.repository.RunCardRepository;
import com.gamebasic.common.exception.GameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final RunCardRepository runCardRepository;

    @Transactional
    public GameDetailResponse createGame(CreateRequest request) {
        Game game = gameRepository.save(new Game(request.getPlayerName()));
        saveDeck(game, request.getDeck());
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for (RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return new GameDetailResponse(
            game.getId(),
            game.getPlayerName(),
            game.getCurrentHp(),
            game.getCurrentFloor(),
            game.getPhase(),
            game.getStatus(),
            deck
        );
    }

    private void saveDeck(Game game, List<RunCardRequest> deck) {
        List<RunCard> cards = new ArrayList<>();
        for (RunCardRequest card : deck) {
            cards.add(new RunCard(game, card.getCardType(), card.getAcquiredFloor()));
        }
        runCardRepository.saveAll(cards);
    }

    private Game findGame(Long gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public GameDetailResponse updateProgress(Long gameId, ProgressRequest request) {
        Game game = findGame(gameId);
        game.updateProgress(
            request.getCurrentHp(),
            request.getCurrentFloor(),
            request.getPhase(),
            request.getStatus()
        );
        // 요청의 deck은 저장할 덱 전체이므로 기존 카드를 모두 지우고 요청 순서대로 다시 저장합니다.
        runCardRepository.deleteAllByGame(game);
        saveDeck(game, request.getDeck());
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for (RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return new GameDetailResponse(
            game.getId(),
            game.getPlayerName(),
            game.getCurrentHp(),
            game.getCurrentFloor(),
            game.getPhase(),
            game.getStatus(),
            deck
        );
    }

    // TODO (Lv 7): 게임 목록 조회. 주석을 풀고 구현하세요.
     @Transactional(readOnly = true)
     public List<GameSummaryResponse> getGames() {

        //DB에서 게임 목록 가져오기
        List<Game> games = gameRepository.findAllByOrderByIdDesc();

        //가져온 게임을 하나씩 GameSummaryResponse로 바꿔서 추가한 다음에 Controller로 보낸다.
        List<GameSummaryResponse> responses = new ArrayList<>();

        //games 안에 있는 게임을 하나씩 꺼내서 변환시켜주고 responses List에 추가해준다.
        for (Game game : games) {
            responses.add(new GameSummaryResponse(
                    game.getId(),
                    game.getPlayerName(),
                    game.getCurrentFloor(),
                    game.getCurrentHp(),
                    game.getPhase(),
                    game.getStatus()
            ));
        }

        //결과값 반환
         return responses;
     }

    // TODO (Lv 7): 게임 상세 조회. 주석을 풀고 구현하세요.
    @Transactional(readOnly = true)
    public GameDetailResponse getGame(Long gameId) {

        //특정 게임 하나를 DB에서 찾아서, 그 게임의 카드 덱까지 포함된 GameDetailResponse로 만들어 반환하는 코드

        //사용자가 해당 게임을 불러오면 DB에서 해당 gameid인 게임을 찾아옴, 만약에 없다면 예외처리함
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        //해당 게임의 카드 가져온다. 여기서 이 게임에 속한 RunCard들 전부를 가져오지만 ID 오름차순으로 가져오게 한다.
        List<RunCard> cards =
                runCardRepository.findAllByGameOrderByIdAsc(game);

        //CardResponse를 담을 리스트 만들기
        List<CardResponse> deck = new ArrayList<>();

        //CardResponse로 변환해서 리스트 안에 넣어주는 작업
        for (RunCard card: cards) {
            deck.add(new CardResponse(
                    card.getId(),
                    card.getCardType(),
                    card.getAcquiredFloor()
            ));
        }

        //지금까지 모은 데이터를 하나의 응답 DTO로 포장해서 만드는 것
        return new GameDetailResponse(
                game.getId(),
                game.getPlayerName(),
                game.getCurrentHp(),
                game.getCurrentFloor(),
                game.getPhase(),
                game.getStatus(),
                deck
        );
    }

    // TODO (Lv 8): 플레이어 이름 변경 — 변경 감지로 수정
    // TODO (Lv 8): 게임 삭제
}
