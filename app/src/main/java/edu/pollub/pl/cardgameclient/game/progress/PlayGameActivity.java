package edu.pollub.pl.cardgameclient.game.progress;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import edu.pollub.pl.cardgameclient.communication.websocket.StompMessageListener;
import edu.pollub.pl.cardgameclient.game.archives.ArchivedGameService;
import edu.pollub.pl.cardgameclient.game.organization.GameOrganizationService;
import edu.pollub.pl.cardgameclient.game.progress.service.GameProgressService;
import edu.pollub.pl.cardgameclient.menu.MenuActivity;
import event.game.organization.GameClosedEvent;
import event.game.organization.GameStartedEvent;
import event.game.progress.GameFinishedEvent;
import event.game.progress.NextRoundEvent;
import event.game.progress.PlayerAttackedEvent;
import event.game.progress.PlayerDefendedEvent;
import event.game.progress.PlayerStopAttackEvent;
import event.game.progress.PlayerStopDefenseEvent;
import lombok.RequiredArgsConstructor;
import model.Card;
import model.CardValue;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.GAME_QUEUE;


@ContentView(R.layout.activity_game)
public class PlayGameActivity extends SimpleNetworkActivity {

    private String gameId;

    private String gameEndpoint;

    private String enemyLogin;

    @InjectView(R.id.playerCards)
    private TwoWayView playerCardsListView;

    @InjectView(R.id.returnButton)
    private ImageButton returnButton;

    @InjectView(R.id.stopButton)
    private ImageButton stopButton;

    @InjectView(R.id.playerCard)
    private ImageView playerCardImageView;

    @InjectView(R.id.enemyCard)
    private ImageView enemyCardImageView;

    @InjectView(R.id.trump)
    private ImageView trumpImageView;

    @InjectView(R.id.cardsStack)
    private TextView cardsStackTextView;

    @InjectView(R.id.enemyCards)
    private TextView enemyCardsTextView;

    @Inject
    private GameOrganizationService organizationService;

    @Inject
    private GameProgressService progressService;

    private CardMovesFactory cardMovesFactory;

    private BattleGround battleGround;

    private PlayerCards playerCards;

    private EnemyCards enemyCards;

    private CardsStack cardsStack;

    private Trump trump;

    @Inject
    private ArchivedGameService archivedGameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        GameStartedEvent event = (GameStartedEvent) intent.getSerializableExtra("event");
        enemyLogin = event.getEnemyLogin();
        gameId = event.getGameId();
        gameEndpoint = GAME_QUEUE + "/" + gameId;
        initGameElements(event);
        returnButton.setOnClickListener(v -> exitGame());
        listenForGameClosed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe(gameEndpoint);
        simpleNetworkTask(new CloseGameTask()).execute();
    }

    private void initGameElements(GameStartedEvent event) {
        CardImagesLoader cil = new CardImagesLoader(this);
        cardMovesFactory = new CardMovesFactory();
        CardMoveCallback firstMove = prepareFirstMove(event.isStartFirst());
        playerCards = new PlayerCards(cil, playerCardsListView, event.getCards(), firstMove);
        battleGround = new BattleGround(cil, playerCardImageView, enemyCardImageView);
        trump = new Trump(cil, trumpImageView, event.getTrump());
        enemyCards = new EnemyCards(enemyCardsTextView);
        cardsStack = new CardsStack(cardsStackTextView);
        subscribe(gameEndpoint, new GameFinishedListener());
    }

    private CardMoveCallback prepareFirstMove(boolean startFist) {
        CardMoveCallback firstMove = cardMovesFactory.createFistMoveInRound(startFist);
        if(!startFist) {
            listenForEnemyAttack();
        }
        return firstMove;
    }

    private void listenForGameClosed() {
        subscribe(gameEndpoint, new GameClosedListener());
    }

    private void listenForEnemyDefense() {
        subscribe(gameEndpoint, new DefenseListener());
        subscribe(gameEndpoint, new EnemyStopDefenseListener());
    }

    private void listenForEnemyAttack() {
        subscribe(gameEndpoint, new AttackListener());
        subscribe(gameEndpoint, new EnemyStopAttackListener());
    }

    private void listenForPlayerStopAttack() {
        subscribe(gameEndpoint, new PlayerStopAttackListener());
    }

    private void listenForPlayerStopDefense() {
        subscribe(gameEndpoint, new PlayerStopDefenseListener());
    }

    private void stopListenForEnemyDefense() {
        unSubscribeEvent(gameEndpoint, PlayerAttackedEvent.class);
        unSubscribeEvent(gameEndpoint, PlayerStopAttackEvent.class);
    }

    private void stopListenForEnemyAttack() {
        unSubscribeEvent(gameEndpoint, PlayerDefendedEvent.class);
        unSubscribeEvent(gameEndpoint, PlayerStopDefenseEvent.class);
    }

    private void stopListenForPlayerStopAttack() {
        unSubscribeEvent(gameEndpoint, PlayerStopAttackEvent.class);
    }

    private void stopListenForPlayerStopDefense() {
        unSubscribeEvent(gameEndpoint, PlayerStopDefenseEvent.class);
    }

    private void playerAttack(Card attackCard) {
        listenForEnemyDefense();
        battleGround.clear();
        playerCards.removeCard(attackCard, cardMovesFactory.createPassive());
        battleGround.putPlayerCard(attackCard);
    }

    private void playerDefense(Card defenseCard) {
        listenForEnemyAttack();
        playerCards.removeCard(defenseCard, cardMovesFactory.createPassive());
        battleGround.putPlayerCard(defenseCard);
    }

    private void playerStopAttack(PlayerStopAttackEvent event) {
        stopListenForPlayerStopAttack();
        prepareNextRound(event);
    }

    private void playerStopDefense(PlayerStopDefenseEvent event) {
        stopListenForPlayerStopDefense();
        prepareNextRound(event);
    }

    private void enemyAttack(PlayerAttackedEvent event) {
        stopListenForEnemyAttack();
        Card enemyAttackCard = event.getAttackCard();
        DefenseCallBack defenseMove = cardMovesFactory.createDefense(enemyAttackCard);
        runOnUiThread(() -> {
            battleGround.clear();
            enemyCards.actualizeCardsCount(event.getAttackerCardsCount());
            battleGround.putEnemyCard(enemyAttackCard);
            playerCards.changeMove(defenseMove);
        });
    }

    private void enemyDefense(PlayerDefendedEvent event) {
        stopListenForEnemyDefense();
        Card enemyDefenseCard = event.getDefenseCard();
        AttackCallBack attackMove = cardMovesFactory.createAttack(event.getCardsOnBattleGround());
        runOnUiThread(() -> {
            enemyCards.actualizeCardsCount(event.getDefenderCardsCount());
            battleGround.putEnemyCard(enemyDefenseCard);
            playerCards.changeMove(attackMove);
        });
    }

    private void enemyStopAttack(PlayerStopAttackEvent event) {
        stopListenForEnemyAttack();
        prepareNextRound(event);
    }

    private void enemyStopDefense(PlayerStopDefenseEvent event) {
        stopListenForPlayerStopDefense();
        prepareNextRound(event);
    }

    private void prepareNextRound(NextRoundEvent event) {
        CardMoveCallback firstMove = prepareFirstMove(event.isFirstNextRound());
        runOnUiThread(() -> {
            battleGround.clear();
            cardsStack.actualizeCardsCount(event.getStackCardsCount());
            enemyCards.actualizeCardsCount(event.getEnemyCardsCount());
            playerCards.changeCards(event.getNewCards(), firstMove);
        });
    }

    private void finishGame(GameFinishedEvent event) {
        runOnUiThread(() -> {
            Toast playerWinToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
            playerWinToast.setText("Player: " + event.getWinnerLogin() + " won the game!");
            playerWinToast.show();
        });
        archivedGameService.archiveGame(
                event.getPoints(),
                event.getDestinationPlayer(),
                enemyLogin,
                event.getDestinationPlayer().equals(event.getWinnerLogin()),
                getApplicationContext());
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void exitGame() {
        showToast(R.string.gameClosed);
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    class AttackCallBack implements CardMoveCallback {

        private final boolean firstMoveInRound;
        private final Set<CardValue> valuesOnBattleGround;

        AttackCallBack(boolean firstMoveInRound, Set<CardValue> valuesOnBattleGround) {
            this.firstMoveInRound = firstMoveInRound;
            this.valuesOnBattleGround = valuesOnBattleGround;
            stopButton.setEnabled(this.canStop());
            stopButton.setOnClickListener(v -> stop());
        }

        @Override
        public void useCard(Card card) {
            playerCards.changeMove(cardMovesFactory.createPassive());
            simpleNetworkTask(new AttackTask(card)).execute();
        }

        @Override
        public void stop() {
            playerCards.changeMove(cardMovesFactory.createPassive());
            simpleNetworkTask(new StopAttackTask()).execute();
        }

        @Override
        public boolean canCardBeUsed(Card card) {
            return firstMoveInRound || valuesOnBattleGround.contains(card.getValue());
        }

        @Override
        public boolean canStop() {
            return !firstMoveInRound;
        }
    }

    @RequiredArgsConstructor
    private class AttackTask extends NetworkOperationStrategy {

        private final Card attackCard;

        @Override
        public void execute() throws Exception {
            progressService.attack(gameId, attackCard);
        }

        @Override
        public void onSuccess() {
            playerAttack(attackCard);
        }

    }

    private class StopAttackTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            listenForPlayerStopAttack();
            progressService.stopAttack(gameId);
        }

    }

    private class AttackListener extends StompMessageListener<PlayerAttackedEvent> {

        @Override
        public void onMessage(PlayerAttackedEvent event) {
            enemyAttack(event);
        }

        AttackListener() { super(PlayerAttackedEvent.class);}

    }

    private class EnemyStopAttackListener extends StompMessageListener<PlayerStopAttackEvent> {

        @Override
        public void onMessage(PlayerStopAttackEvent event) {
            enemyStopAttack(event);
        }

        EnemyStopAttackListener() { super(PlayerStopAttackEvent.class);}

    }

    private class PlayerStopAttackListener extends StompMessageListener<PlayerStopAttackEvent> {

        @Override
        public void onMessage(PlayerStopAttackEvent event) {
            playerStopAttack(event);
        }

        PlayerStopAttackListener() { super(PlayerStopAttackEvent.class);}
    }

    class DefenseCallBack implements CardMoveCallback {

        private final Card attackCard;

        DefenseCallBack(Card attackCard) {
            this.attackCard = attackCard;
            stopButton.setEnabled(this.canStop());
            stopButton.setOnClickListener(v -> stop());
        }

        @Override
        public void useCard(Card card) {
            playerCards.changeMove(cardMovesFactory.createPassive());
            simpleNetworkTask(new DefenseTask(card)).execute();
        }

        @Override
        public void stop() {
            playerCards.changeMove(cardMovesFactory.createPassive());
            simpleNetworkTask(new StopDefenseTask()).execute();
        }

        @Override
        public boolean canCardBeUsed(Card card) {
            return card.isStrangerThan(attackCard, trump.getColor());
        }

        @Override
        public boolean canStop() {
            return true;
        }
    }

    @RequiredArgsConstructor
    private class DefenseTask extends NetworkOperationStrategy {

        private final Card defenseCard;

        @Override
        public void execute() throws Exception {
            progressService.defense(gameId, defenseCard);
        }

        @Override
        public void onSuccess() {
            playerDefense(defenseCard);
        }

    }

    private class StopDefenseTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            listenForPlayerStopDefense();
            progressService.stopDefense(gameId);
        }

    }

    private class DefenseListener extends StompMessageListener<PlayerDefendedEvent> {

        @Override
        public void onMessage(PlayerDefendedEvent event) {
            enemyDefense(event);
        }

        DefenseListener() { super(PlayerDefendedEvent.class);}

    }

    private class EnemyStopDefenseListener extends StompMessageListener<PlayerStopDefenseEvent> {

        @Override
        public void onMessage(PlayerStopDefenseEvent event) {
            enemyStopDefense(event);
        }

        EnemyStopDefenseListener() { super(PlayerStopDefenseEvent.class);}

    }

    private class PlayerStopDefenseListener extends StompMessageListener<PlayerStopDefenseEvent> {

        @Override
        public void onMessage(PlayerStopDefenseEvent event) {
            playerStopDefense(event);
        }

        PlayerStopDefenseListener() { super(PlayerStopDefenseEvent.class);}
    }

    class PassiveCallBack implements CardMoveCallback {

        PassiveCallBack() {
            stopButton.setEnabled(this.canStop());
            stopButton.setOnClickListener(v -> stop());
        }

        @Override
        public void useCard(Card card) {}

        @Override
        public void stop() {}

        @Override
        public boolean canCardBeUsed(Card card) {
            return false;
        }

        @Override
        public boolean canStop() {
            return false;
        }
    }

    private class GameFinishedListener extends StompMessageListener<GameFinishedEvent> {

        @Override
        public void onMessage(GameFinishedEvent event) {
            finishGame(event);
        }

        GameFinishedListener() { super(GameFinishedEvent.class);}

    }

    private class GameClosedListener extends StompMessageListener<GameClosedEvent> {

        @Override
        public void onMessage(GameClosedEvent event) {
            exitGame();
        }

        GameClosedListener() { super(GameClosedEvent.class);}
    }

    private class CloseGameTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            organizationService.close();
        }

        @Override
        public void onSuccess() {
            exitGame();
        }
    }

    class CardMovesFactory {

        CardMoveCallback createFistMoveInRound(boolean playerStartFirst) {
            if(playerStartFirst) {
                return new PlayGameActivity.AttackCallBack(true, Collections.emptySet());
            }
            else {
                return new PlayGameActivity.PassiveCallBack();
            }
        }

        DefenseCallBack createDefense(Card enemyAttackCard) {
            return new DefenseCallBack(enemyAttackCard);
        }

        AttackCallBack createAttack(Set<CardValue> cardsOnBattleGround) {
            return new AttackCallBack(false, cardsOnBattleGround);
        }

        PassiveCallBack createPassive() {
            return new PlayGameActivity.PassiveCallBack();
        }
    }
}