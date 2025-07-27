package com.example.hamster.data.network;

import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetCategory;
import com.example.hamster.data.model.AssetsResponse;
import com.example.hamster.data.model.LoginRequest;
import com.example.hamster.data.model.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // --- Auth ---
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // --- Assets (Inventory) ---
    @GET("assets")
    Call<AssetsResponse> getAssets(@Query("page") int page, @Query("limit") int limit);

    @GET("assets/{id}")
    Call<Asset> getAssetById(@Path("id") String assetId);

    @GET("assets/by-code/{code}")
    Call<Asset> getAssetByCode(@Path("code") String assetCode);

    // TODO: Buat Request Body class untuk POST & PATCH
    // @POST("assets")
    // Call<Asset> createAsset(@Body CreateAssetRequest request);

    // @PATCH("assets/{id}")
    // Call<Asset> updateAsset(@Path("id") String assetId, @Body UpdateAssetRequest request);

    @DELETE("assets/{id}")
    Call<Void> deleteAsset(@Path("id") String assetId);


    // --- Asset Categories ---
    @GET("asset-categories")
    Call<List<AssetCategory>> getAssetCategories(); // Asumsi respons berupa List

    @GET("asset-categories/{id}")
    Call<AssetCategory> getAssetCategoryById(@Path("id") String categoryId);

    // TODO: Buat Request Body class untuk POST & PUT
    // @POST("asset-categories")
    // Call<AssetCategory> createAssetCategory(@Body CreateCategoryRequest request);

    // @PUT("asset-categories/{id}")
    // Call<AssetCategory> updateAssetCategory(@Path("id") String categoryId, @Body UpdateCategoryRequest request);


    // --- Asset Subcategories ---
    // TODO: Buat model data untuk AssetSubcategoryResponse
    // @GET("asset-subcategories")
    // Call<AssetSubcategoryResponse> getAssetSubcategories(@Query("page") int page, @Query("limit") int limit);


    // --- Asset Activation ---
    // TODO: Buat model data untuk responses & requests
    // @GET("asset-activation/settings")
    // Call<ActivationSettingsResponse> getActivationSettings();

    // @POST("asset-activation/start")
    // Call<ActivationStartResponse> startActivation(@Body StartActivationRequest request);

    // @POST("asset-activation/approve")
    // Call<Void> approveActivation(@Body ApproveActivationRequest request);
}