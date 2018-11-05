package edu.pollub.pl.cardgameclient.authorization.registration;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import command.RegisterCommand;
import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_register)
public class RegistrationActivity extends SimpleNetworkActivity {

    @InjectView(R.id.newPasswordEditText)
    private EditText loginInput;

    @InjectView(R.id.oldPasswordEditText)
    private EditText passwordInput;

    @InjectView(R.id.confirmNewPasswordEditText)
    private EditText confirmPasswordInput;

    @InjectView(R.id.cancelButton)
    private Button loginButton;

    @InjectView(R.id.submitButton)
    private Button registerButton;

    @Inject
    private RegistrationService registrationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginButton.setOnClickListener(v -> comeBack());
        registerButton.setOnClickListener(v -> simpleNetworkTask(new RegisterTask()).execute());
    }

    protected RegisterCommand getCommand() {
        return new RegisterCommand(loginInput.getText().toString(), passwordInput.getText().toString(), confirmPasswordInput.getText().toString());
    }


    private void clearInputs() {
        loginInput.setText("");
        passwordInput.setText("");
        confirmPasswordInput.setText("");
    }

    private class RegisterTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            RegisterCommand command = getCommand();
            registrationService.register(command);
        }

        @Override
        public void onSuccess() {
            showToast(R.string.registerSuccess);
            clearInputs();
            comeBack();
        }

        @Override
        public Boolean isValid() {
            String login = loginInput.getText().toString();
            String password = passwordInput.getText().toString();
            String passwordConfirmation = confirmPasswordInput.getText().toString();

            if(login.isEmpty()) {
                showToast(R.string.loginRequired);
                return false;
            }
            if(password.isEmpty()) {
                showToast(R.string.passwordRequired);
                return false;
            }
            if(!passwordConfirmation.equals(password)) {
                showToast(R.string.passwordConfirmationFail);
                return false;
            }
            return true;
        }
    }
}
