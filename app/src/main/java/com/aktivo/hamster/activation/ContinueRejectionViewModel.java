package com.aktivo.hamster.activation;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.aktivo.hamster.data.model.response.RejectionResponse;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;
import com.aktivo.hamster.utils.FileUtils;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContinueRejectionViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();

    public ContinueRejectionViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getActionResult() { return actionResult; }

    public void continueRejection(String transactionId, Uri photoUri) {
        isLoading.setValue(true);

        File photoFile = FileUtils.getFile(getApplication(), photoUri);
        if (photoFile == null) {
            errorMessage.setValue("File foto tidak valid.");
            actionResult.setValue(false);
            isLoading.setValue(false);
            return;
        }

        RequestBody transactionIdBody = RequestBody.create(MediaType.parse("text/plain"), transactionId);
        String mimeType = getApplication().getContentResolver().getType(photoUri);
        if (mimeType == null) mimeType = "image/jpeg";
        RequestBody photoBody = RequestBody.create(MediaType.parse(mimeType), photoFile);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", photoFile.getName(), photoBody);

        apiService.continueRejection(transactionId, transactionIdBody, photoPart).enqueue(new Callback<RejectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<RejectionResponse> call, @NonNull Response<RejectionResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    actionResult.setValue(true);
                } else {
                    errorMessage.setValue("Gagal melanjutkan proses. Kode: " + response.code());
                    actionResult.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RejectionResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Permintaan jaringan gagal: " + t.getMessage());
                actionResult.setValue(false);
            }
        });
    }
}