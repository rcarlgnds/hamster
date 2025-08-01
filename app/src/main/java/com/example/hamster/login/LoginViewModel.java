package com.example.hamster.login;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.R;
import com.example.hamster.data.model.LoginRequest;
import com.example.hamster.data.model.LoginResponse;
import com.example.hamster.data.model.User;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import com.example.hamster.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    public enum LoginResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        API_ERROR,
        LOADING
    }

    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final SessionManager sessionManager;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<User> getUser() {
        return user;
    }

    // --- ADD THIS GETTER METHOD ---
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String email, String password) {
        loginResult.setValue(LoginResult.LOADING);

        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);
        Call<LoginResponse> call = apiService.login(new LoginRequest(email, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    sessionManager.createLoginSession(
                            loginResponse.getData().getAccessToken(),
                            loginResponse.getData().getRefreshToken(),
                            loginResponse.getData().getUser()
                    );
                    user.setValue(loginResponse.getData().getUser());
                    loginResult.setValue(LoginResult.SUCCESS);
                } else {
                    // Post specific error messages
                    errorMessage.setValue(getApplication().getString(R.string.login_failed));
                    loginResult.setValue(LoginResult.INVALID_CREDENTIALS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // Post specific error messages
                errorMessage.setValue("Failed to connect to the server.");
                loginResult.setValue(LoginResult.API_ERROR);
            }
        });
    }
}