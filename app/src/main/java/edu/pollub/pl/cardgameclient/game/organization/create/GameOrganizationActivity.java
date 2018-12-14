package edu.pollub.pl.cardgameclient.game.organization.create;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import javax.inject.Inject;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import edu.pollub.pl.cardgameclient.communication.websocket.StompMessageListener;
import edu.pollub.pl.cardgameclient.game.organization.GameOrganizationService;
import edu.pollub.pl.cardgameclient.game.progress.PlayGameActivity;
import event.game.organization.GameStartedEvent;
import response.GameResponse;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.GAME_QUEUE;

@ContentView(R.layout.activity_create_game)
public class GameOrganizationActivity extends SimpleNetworkActivity {

    private String organizedGameEndpoint;

    @InjectView(R.id.cancelButton)
    private Button cancelButton;

    @Inject
    private GameOrganizationService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancelButton.setOnClickListener(v -> simpleNetworkTask(new CloseGameTask()).execute());
    }

    @Override
    protected void onStart() {
        super.onStart();
        simpleNetworkTask(new OrganizeGameTask()).execute();
    }

    private void listerForPlayerJoin() {
        subscribe(organizedGameEndpoint, new PlayerJoinedListener());
    }

    private class OrganizeGameTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            GameResponse organizedGame = service.organize();
            organizedGameEndpoint = GAME_QUEUE + "/" + organizedGame.getId();
            listerForPlayerJoin();
        }

        @Override
        public void onSuccess() {
            showToast(R.string.gameCreated);
        }
    }

    private class CloseGameTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            service.close();
        }

        @Override
        public void onSuccess() {
            unSubscribe(organizedGameEndpoint);
            comeBack();
        }
    }

    private class PlayerJoinedListener extends StompMessageListener<GameStartedEvent> {

        @Override
        public void onMessage(GameStartedEvent event) {
            unSubscribe(organizedGameEndpoint);
            showToast(R.string.gameStarted);
            Intent intent = new Intent(GameOrganizationActivity.this, PlayGameActivity.class);
            intent.putExtra("event", event);
            startActivityForResult(intent, 0);
        }

        PlayerJoinedListener() {
            super(GameStartedEvent.class);
        }
    }
}
