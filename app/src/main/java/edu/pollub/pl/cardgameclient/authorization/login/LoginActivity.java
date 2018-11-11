package edu.pollub.pl.cardgameclient.authorization.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import command.LoginCommand;
import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import edu.pollub.pl.cardgameclient.communication.websocket.StompMessageListener;
import edu.pollub.pl.cardgameclient.menu.MenuActivity;
import edu.pollub.pl.cardgameclient.authorization.registration.RegistrationActivity;
import event.LogoutUserEvent;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.AUTH_QUEUE;
import static edu.pollub.pl.cardgameclient.config.ConfigConst.LOGIN_KEY;


@ContentView(R.layout.activity_login)
public class LoginActivity extends SimpleNetworkActivity {

    @InjectView(R.id.loginEditText)
    private EditText loginInput;

    @InjectView(R.id.passwordEditText)
    private EditText passwordInput;

    @InjectView(R.id.loginButton)
    private Button loginButton;

    @InjectView(R.id.registerButton)
    private Button registerButton;

    @Inject
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginButton.setOnClickListener(v -> simpleNetworkTask(new LoginTask()).execute());
        registerButton.setOnClickListener(v -> {
                    goTo(RegistrationActivity.class);
                    clearInputs();
                }
        );
    }

    protected LoginCommand getCommand() {
        return new LoginCommand(loginInput.getText().toString(), passwordInput.getText().toString());
    }


    private void clearInputs() {
        loginInput.setText("");
        passwordInput.setText("");
    }

    private void listerForLogout() {
        subscribe(AUTH_QUEUE, new LogoutListener());
    }

    private class LoginTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            LoginCommand command = getCommand();
            String login = loginService.login(command).getLogin();
            saveString(LOGIN_KEY, login);
        }

        @Override
        public void onSuccess() {
            showToast(R.string.loginSuccess);
            clearInputs();
            connect();
            listerForLogout();
            goTo(MenuActivity.class);
        }

        @Override
        public Boolean isValid() {
            String login = loginInput.getText().toString();
            String password = passwordInput.getText().toString();

            if(login.isEmpty()) {
                showToast(R.string.loginRequired);
                return false;
            }
            if(password.isEmpty()) {
                showToast(R.string.passwordRequired);
                return false;
            }
            return true;
        }
    }

    private class LogoutListener extends StompMessageListener<LogoutUserEvent> {

        @Override
        public void onMessage(LogoutUserEvent messageBody) {
            logout();
        }

        LogoutListener() {
            super(LogoutUserEvent.class);
        }

    }
}
