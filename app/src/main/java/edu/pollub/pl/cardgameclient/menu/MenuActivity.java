package edu.pollub.pl.cardgameclient.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.authorization.changeLogin.ChangeNickActivity;
import edu.pollub.pl.cardgameclient.authorization.changePassword.ChangePasswordActivity;
import edu.pollub.pl.cardgameclient.authorization.login.LoginService;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import edu.pollub.pl.cardgameclient.game.create.GameOrganizationActivity;
import edu.pollub.pl.cardgameclient.game.find.FindGameActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.LOGIN_KEY;

@ContentView(R.layout.activity_menu)
public class MenuActivity extends SimpleNetworkActivity {

    @InjectView(R.id.title)
    private TextView textView;

    @InjectView(R.id.createGameButton)
    private Button createGameButton;

    @InjectView(R.id.findGameButton)
    private Button findGameButton;

    @InjectView(R.id.changeNick)
    private Button changNickButton;

    @InjectView(R.id.changePassword)
    private Button changePasswordButton;

    @InjectView(R.id.logoutButton)
    private Button logoutButton;

    @Inject
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        welcomeUser();
        createGameButton.setOnClickListener((v) -> goTo(GameOrganizationActivity.class));
        findGameButton.setOnClickListener((v) -> goTo(FindGameActivity.class));
        changNickButton.setOnClickListener((v) -> goTo(ChangeNickActivity.class));
        changePasswordButton.setOnClickListener((v) -> goTo(ChangePasswordActivity.class));
        logoutButton.setOnClickListener((v) -> simpleNetworkTask(new LogoutTask()).execute());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        welcomeUser();
    }

    @SuppressLint("SetTextI18n")
    private void welcomeUser() {
        String login = fetchString(LOGIN_KEY);
        if(!login.isEmpty()) {
            textView.setText(getMessage(R.string.welcome) + " " + login);
        }
        else {
            comeBack();
        }
    }

    private class LogoutTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            loginService.logout();
        }

        @Override
        public void onSuccess() {
            logout();
        }
    }
}
