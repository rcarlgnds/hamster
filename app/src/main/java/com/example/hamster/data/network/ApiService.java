package com.example.hamster.data.network;

import android.app.Notification;

import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetActivationSetting;
import com.example.hamster.data.model.AssetActivationStatus;
import com.example.hamster.data.model.AssetCategory;
import com.example.hamster.data.model.AssetDetailResponse;
import com.example.hamster.data.model.AssetSubCategory;
import com.example.hamster.data.model.AssetsResponse;
import com.example.hamster.data.model.Brand;
import com.example.hamster.data.model.Building;
import com.example.hamster.data.model.Division;
import com.example.hamster.data.model.Floor;
import com.example.hamster.data.model.Hospital;
import com.example.hamster.data.model.LoginRequest;
import com.example.hamster.data.model.LoginResponse;
import com.example.hamster.data.model.MediaFile;
import com.example.hamster.data.model.OptionItem;
import com.example.hamster.data.model.Position;
import com.example.hamster.data.model.RolePermission;
import com.example.hamster.data.model.Room;
import com.example.hamster.data.model.SubRoom;
import com.example.hamster.data.model.UpdateAssetRequest;
import com.example.hamster.data.model.User;
import com.example.hamster.data.model.Vendor;
import com.example.hamster.data.model.WorkingUnit;
import com.example.hamster.data.model.request.ApproveActivationRequest;
import com.example.hamster.data.model.request.CreateAssetActivationSettingRequest;
import com.example.hamster.data.model.request.CreateAssetCategoryRequest;
import com.example.hamster.data.model.request.CreateAssetSubCategoryRequest;
import com.example.hamster.data.model.request.CreateBrandRequest;
import com.example.hamster.data.model.request.CreateBuildingRequest;
import com.example.hamster.data.model.request.CreateDivisionRequest;
import com.example.hamster.data.model.request.CreateHospitalRequest;
import com.example.hamster.data.model.request.CreateNotificationRequest;
import com.example.hamster.data.model.request.CreatePositionRequest;
import com.example.hamster.data.model.request.CreateRoomRequest;
import com.example.hamster.data.model.request.CreateSubRoomRequest;
import com.example.hamster.data.model.request.CreateUserRequest;
import com.example.hamster.data.model.request.CreateVendorRequest;
import com.example.hamster.data.model.request.CreateWorkingUnitRequest;
import com.example.hamster.data.model.request.ExternalAssetRequest;
import com.example.hamster.data.model.request.PrintByIdsRequest;
import com.example.hamster.data.model.request.PrintLabelRequest;
import com.example.hamster.data.model.request.RefreshTokenRequest;
import com.example.hamster.data.model.request.StartActivationRequest;
import com.example.hamster.data.model.request.UpdateAssetActivationSettingRequest;
import com.example.hamster.data.model.request.UpdateAssetCategoryRequest;
import com.example.hamster.data.model.request.UpdateAssetSubCategoryRequest;
import com.example.hamster.data.model.request.UpdateBrandRequest;
import com.example.hamster.data.model.request.UpdateBuildingRequest;
import com.example.hamster.data.model.request.UpdateDivisionRequest;
import com.example.hamster.data.model.request.UpdateHospitalRequest;
import com.example.hamster.data.model.request.UpdateNotificationRequest;
import com.example.hamster.data.model.request.UpdatePositionRequest;
import com.example.hamster.data.model.request.UpdateRolePermissionRequest;
import com.example.hamster.data.model.request.UpdateRoomRequest;
import com.example.hamster.data.model.request.UpdateSubRoomRequest;
import com.example.hamster.data.model.request.UpdateUserRequest;
import com.example.hamster.data.model.request.UpdateVendorRequest;
import com.example.hamster.data.model.request.UpdateWorkingUnitRequest;
import com.example.hamster.data.model.response.AssetActivationResponse;
import com.example.hamster.data.model.response.AssetActivationSettingResponse;
import com.example.hamster.data.model.response.AssetActivationStatusResponse;
import com.example.hamster.data.model.response.AssetApprovalStatusResponse;
import com.example.hamster.data.model.response.AssetByCodeResponse;
import com.example.hamster.data.model.response.AssetCategoryResponse;
import com.example.hamster.data.model.response.AssetSubCategoryResponse;
import com.example.hamster.data.model.response.BrandResponse;
import com.example.hamster.data.model.response.BuildingResponse;
import com.example.hamster.data.model.response.DivisionResponse;
import com.example.hamster.data.model.response.FloorResponse;
import com.example.hamster.data.model.response.HospitalResponse;
import com.example.hamster.data.model.response.MediaFileResponse;
import com.example.hamster.data.model.response.NotificationResponse;
import com.example.hamster.data.model.response.OptionsResponse;
import com.example.hamster.data.model.response.PendingApprovalsResponse;
import com.example.hamster.data.model.response.PositionResponse;
import com.example.hamster.data.model.response.PrinterStatusResponse;
import com.example.hamster.data.model.response.RolePermissionResponse;
import com.example.hamster.data.model.response.RoomResponse;
import com.example.hamster.data.model.response.SubRoomResponse;
import com.example.hamster.data.model.response.TemplateResponse;
import com.example.hamster.data.model.response.UnreadCountResponse;
import com.example.hamster.data.model.response.UserResponse;
import com.example.hamster.data.model.response.VendorResponse;
import com.example.hamster.data.model.response.WorkingUnitResponse;
import com.example.hamster.data.model.token.RefreshTokenResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // --- Auth ---
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("auth/me")
    Call<User> getCurrentUser();

    @POST("auth/refresh")
    Call<RefreshTokenResponse> refreshToken(@Body RefreshTokenRequest request);

    // --- Users ---
    @GET("users/options")
    Call<OptionsResponse> getUserOptions();

    @GET("users")
    Call<UserResponse> getUsers(@Query("page") int page, @Query("limit") int limit);

    @POST("users")
    Call<User> createUser(@Body CreateUserRequest request);

    @GET("users/deleted")
    Call<UserResponse> getDeletedUsers(@Query("page") int page, @Query("limit") int limit);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String userId);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body UpdateUserRequest request);

    @DELETE("users/{id}")
    Call<Void> softDeleteUser(@Path("id") String userId);

    @PATCH("users/{id}/restore")
    Call<Void> restoreUser(@Path("id") String userId);

    @DELETE("users/{id}/hard")
    Call<Void> hardDeleteUser(@Path("id") String userId);

    // --- Hospitals ---
    @GET("hospitals/options")
    Call<OptionsResponse> getHospitalOptions();

    @GET("hospitals/{id}/buildings/options")
    Call<OptionsResponse> getBuildingOptionsForHospital(@Path("id") String hospitalId);

    @GET("hospitals")
    Call<HospitalResponse> getHospitals(@Query("page") int page, @Query("limit") int limit);

    @POST("hospitals")
    Call<Hospital> createHospital(@Body CreateHospitalRequest request);

    @GET("hospitals/deleted")
    Call<HospitalResponse> getDeletedHospitals(@Query("page") int page, @Query("limit") int limit);

    @GET("hospitals/{id}")
    Call<Hospital> getHospitalById(@Path("id") String hospitalId);

    @PUT("hospitals/{id}")
    Call<Hospital> updateHospital(@Path("id") String hospitalId, @Body UpdateHospitalRequest request);

    @DELETE("hospitals/{id}")
    Call<Void> softDeleteHospital(@Path("id") String hospitalId);

    @PATCH("hospitals/{id}/restore")
    Call<Void> restoreHospital(@Path("id") String hospitalId);

    @DELETE("hospitals/{id}/hard")
    Call<Void> hardDeleteHospital(@Path("id") String hospitalId);


    // --- Buildings ---
    @GET("buildings/options")
    Call<OptionsResponse> getBuildingOptions();

    @GET("buildings")
    Call<BuildingResponse> getBuildings(@Query("page") int page, @Query("limit") int limit);

    @POST("buildings")
    Call<Building> createBuilding(@Body CreateBuildingRequest request);

    @GET("buildings/deleted")
    Call<BuildingResponse> getDeletedBuildings(@Query("page") int page, @Query("limit") int limit);

    @GET("buildings/{id}")
    Call<Building> getBuildingById(@Path("id") String buildingId);

    @PUT("buildings/{id}")
    Call<Building> updateBuilding(@Path("id") String buildingId, @Body UpdateBuildingRequest request);

    @DELETE("buildings/{id}")
    Call<Void> softDeleteBuilding(@Path("id") String buildingId);

    @PATCH("buildings/{id}/restore")
    Call<Void> restoreBuilding(@Path("id") String buildingId);

    @DELETE("buildings/{id}/hard")
    Call<Void> hardDeleteBuilding(@Path("id") String buildingId);

    @GET("buildings/{buildingId}/floors/options")
    Call<OptionsResponse> getFloorOptionsForBuilding(@Path("buildingId") String buildingId);

    // --- Floors ---
    @GET("floors")
    Call<FloorResponse> getFloors(@Query("page") int page, @Query("limit") int limit);

    @Multipart
    @POST("floors")
    Call<Floor> createFloor(@Part("data") RequestBody data, @Part MultipartBody.Part floorPlan);

    @GET("floors/deleted")
    Call<FloorResponse> getDeletedFloors(@Query("page") int page, @Query("limit") int limit);

    @GET("floors/{id}")
    Call<Floor> getFloorById(@Path("id") String floorId);

    @Multipart
    @PUT("floors/{id}")
    Call<Floor> updateFloor(@Path("id") String floorId, @Part("data") RequestBody data, @Part MultipartBody.Part floorPlan);

    @DELETE("floors/{id}")
    Call<Void> softDeleteFloor(@Path("id") String floorId);

    @GET("floors/{id}/rooms/options")
    Call<OptionsResponse> getRoomOptionsByFloor(@Path("id") String floorId);

    @PATCH("floors/{id}/restore")
    Call<Void> restoreFloor(@Path("id") String floorId);

    @DELETE("floors/{id}/hard")
    Call<Void> hardDeleteFloor(@Path("id") String floorId);


    // --- Rooms ---
    @GET("rooms")
    Call<RoomResponse> getRooms(@Query("page") int page, @Query("limit") int limit);

    @POST("rooms")
    Call<Room> createRoom(@Body CreateRoomRequest request);

    @GET("rooms/deleted")
    Call<RoomResponse> getDeletedRooms(@Query("page") int page, @Query("limit") int limit);

    @GET("rooms/{id}")
    Call<Room> getRoomById(@Path("id") String roomId);

    @PUT("rooms/{id}")
    Call<Room> updateRoom(@Path("id") String roomId, @Body UpdateRoomRequest request);

    @DELETE("rooms/{id}")
    Call<Void> deleteRoom(@Path("id") String roomId);

    @GET("rooms/{id}/sub-rooms/options")
    Call<OptionsResponse> getSubRoomOptionsByRoom(@Path("id") String roomId);

    @PATCH("rooms/{id}/restore")
    Call<Void> restoreRoom(@Path("id") String roomId);

    @DELETE("rooms/{id}/hard")
    Call<Void> hardDeleteRoom(@Path("id") String roomId);

    // --- Sub-Rooms ---
    @GET("sub-rooms/by-room/{roomId}")
    Call<List<SubRoom>> getSubRoomsByRoom(@Path("roomId") String roomId);

    @GET("sub-rooms")
    Call<SubRoomResponse> getSubRooms(@Query("page") int page, @Query("limit") int limit);

    @POST("sub-rooms")
    Call<SubRoom> createSubRoom(@Body CreateSubRoomRequest request);

    @GET("sub-rooms/{id}")
    Call<SubRoom> getSubRoomById(@Path("id") String subRoomId);

    @PUT("sub-rooms/{id}")
    Call<SubRoom> updateSubRoom(@Path("id") String subRoomId, @Body UpdateSubRoomRequest request);

    @DELETE("sub-rooms/{id}")
    Call<Void> deleteSubRoom(@Path("id") String subRoomId);

    @GET("sub-rooms/deleted")
    Call<SubRoomResponse> getDeletedSubRooms(@Query("page") int page, @Query("limit") int limit);

    @PATCH("sub-rooms/{id}/restore")
    Call<Void> restoreSubRoom(@Path("id") String subRoomId);

    @DELETE("sub-rooms/{id}/hard")
    Call<Void> hardDeleteSubRoom(@Path("id") String subRoomId);


    // --- Divisions ---
    @GET("divisions/options")
    Call<OptionsResponse> getDivisionOptions();

    @GET("divisions")
    Call<DivisionResponse> getDivisions(@Query("page") int page, @Query("limit") int limit);

    @POST("divisions")
    Call<Division> createDivision(@Body CreateDivisionRequest request);

    @GET("divisions/deleted")
    Call<DivisionResponse> getDeletedDivisions(@Query("page") int page, @Query("limit") int limit);

    @GET("divisions/{id}")
    Call<Division> getDivisionById(@Path("id") String divisionId);

    @PUT("divisions/{id}")
    Call<Division> updateDivision(@Path("id") String divisionId, @Body UpdateDivisionRequest request);

    @DELETE("divisions/{id}")
    Call<Void> deleteDivision(@Path("id") String divisionId);

    @PATCH("divisions/{id}/restore")
    Call<Void> restoreDivision(@Path("id") String divisionId);

    @DELETE("divisions/{id}/hard")
    Call<Void> hardDeleteDivision(@Path("id") String divisionId);

    // --- Positions ---
    @DELETE("positions/{id}")
    Call<Void> deletePosition(@Path("id") String positionId);

    @GET("positions/{id}")
    Call<Position> getPositionById(@Path("id") String positionId);

    @PUT("positions/{id}")
    Call<Position> updatePosition(@Path("id") String positionId, @Body UpdatePositionRequest request);

    @GET("positions/options")
    Call<OptionsResponse> getPositionOptions();

    @GET("positions")
    Call<PositionResponse> getPositions(@Query("page") int page, @Query("limit") int limit);

    @POST("positions")
    Call<Position> createPosition(@Body CreatePositionRequest request);

    @GET("positions/deleted")
    Call<PositionResponse> getDeletedPositions(@Query("page") int page, @Query("limit") int limit);

    @PATCH("positions/{id}/restore")
    Call<Void> restorePosition(@Path("id") String positionId);

    @DELETE("positions/{id}/hard")
    Call<Void> hardDeletePosition(@Path("id") String positionId);

    // --- Working Units ---
    @GET("working-units/options")
    Call<OptionsResponse> getWorkingUnitOptions();

    @GET("working-units")
    Call<WorkingUnitResponse> getWorkingUnits(@Query("page") int page, @Query("limit") int limit);

    @POST("working-units")
    Call<WorkingUnit> createWorkingUnit(@Body CreateWorkingUnitRequest request);

    @GET("working-units/deleted")
    Call<WorkingUnitResponse> getDeletedWorkingUnits(@Query("page") int page, @Query("limit") int limit);

    @GET("working-units/{id}")
    Call<WorkingUnit> getWorkingUnitById(@Path("id") String workingUnitId);

    @PUT("working-units/{id}")
    Call<WorkingUnit> updateWorkingUnit(@Path("id") String workingUnitId, @Body UpdateWorkingUnitRequest request);

    @DELETE("working-units/{id}")
    Call<Void> deleteWorkingUnit(@Path("id") String workingUnitId);

    @PATCH("working-units/{id}/restore")
    Call<Void> restoreWorkingUnit(@Path("id") String workingUnitId);

    @DELETE("working-units/{id}/hard")
    Call<Void> hardDeleteWorkingUnit(@Path("id") String workingUnitId);


    // --- Asset Categories ---
    @GET("asset-categories/options")
    Call<OptionsResponse> getAssetCategoryOptions();

    @GET("asset-categories")
    Call<AssetCategoryResponse> getAssetCategories(@Query("page") int page, @Query("limit") int limit);

    @POST("asset-categories")
    Call<AssetCategory> createAssetCategory(@Body CreateAssetCategoryRequest request);

    @GET("asset-categories/deleted")
    Call<AssetCategoryResponse> getDeletedAssetCategories(@Query("page") int page, @Query("limit") int limit);

    @GET("asset-categories/{id}")
    Call<AssetCategory> getAssetCategoryById(@Path("id") String categoryId);

    @PUT("asset-categories/{id}")
    Call<AssetCategory> updateAssetCategory(@Path("id") String categoryId, @Body UpdateAssetCategoryRequest request);

    @DELETE("asset-categories/{id}")
    Call<Void> deleteAssetCategory(@Path("id") String categoryId);

    @PATCH("asset-categories/{id}/restore")
    Call<Void> restoreAssetCategory(@Path("id") String categoryId);

    @DELETE("asset-categories/{id}/hard")
    Call<Void> hardDeleteAssetCategory(@Path("id") String categoryId);

    @GET("asset-categories/{id}/asset-subcategories/options")
    Call<OptionsResponse> getAssetSubCategoryOptions(@Path("id") String categoryId);

    // --- Asset Subcategories ---
    @GET("asset-subcategories")
    Call<AssetSubCategoryResponse> getAssetSubCategories(@Query("page") int page, @Query("limit") int limit);

    @POST("asset-subcategories")
    Call<AssetSubCategory> createAssetSubCategory(@Body CreateAssetSubCategoryRequest request);

    @GET("asset-subcategories/{id}")
    Call<AssetSubCategory> getAssetSubCategoryById(@Path("id") String subCategoryId);

    @PUT("asset-subcategories/{id}")
    Call<AssetSubCategory> updateAssetSubCategory(@Path("id") String subCategoryId, @Body UpdateAssetSubCategoryRequest request);

    @DELETE("asset-subcategories/{id}")
    Call<Void> deleteAssetSubCategory(@Path("id") String subCategoryId);

    @GET("asset-subcategories/deleted")
    Call<AssetSubCategoryResponse> getDeletedAssetSubCategories(@Query("page") int page, @Query("limit") int limit);

    @PATCH("asset-subcategories/{id}/restore")
    Call<Void> restoreAssetSubCategory(@Path("id") String subCategoryId);

    @DELETE("asset-subcategories/{id}/hard")
    Call<Void> hardDeleteAssetSubCategory(@Path("id") String subCategoryId);


    // --- Assets ---
    @GET("assets/options")
    Call<OptionsResponse> getAssetOptions();

    @GET("assets/print")
    Call<List<Asset>> getAssetsForPrinting();

    @Multipart
    @POST("assets")
    Call<Asset> createAsset(@Part("data") RequestBody data, @Part List<MultipartBody.Part> files);

    @GET("assets")
    Call<AssetsResponse> getAssets(@Query("page") int page, @Query("limit") int limit);

    @GET("assets/deleted")
    Call<AssetsResponse> getDeletedAssets(@Query("page") int page, @Query("limit") int limit);

    @GET("assets/by-code/{code}")
    Call<AssetByCodeResponse> getAssetByCode(@Path("code") String assetCode);

    @GET("assets/{id}")
    Call<AssetDetailResponse> getAssetById(@Path("id") String assetId);

    @Multipart
    @PATCH("assets/{id}")
    Call<Asset> updateAsset(
            @Path("id") String assetId,
            @PartMap Map<String, RequestBody> fields,
            @Part List<MultipartBody.Part> fileParts
    );

    @DELETE("assets/{id}")
    Call<Void> softDeleteAsset(@Path("id") String assetId);

    @PATCH("assets/{id}/restore")
    Call<Void> restoreAsset(@Path("id") String assetId);

    @DELETE("assets/{id}/hard")
    Call<Void> hardDeleteAsset(@Path("id") String assetId);

    @DELETE("assets/{id}/media/{mediaId}")
    Call<Void> deleteAssetMedia(@Path("id") String assetId, @Path("mediaId") String mediaId);

    @POST("assets/print-labels")
    Call<Void> printAssetLabels(@Body PrintLabelRequest request);

    @POST("assets/print-by-ids")
    Call<Void> printAssetLabelsByIds(@Body PrintByIdsRequest request);


    // --- Printer ---
    @POST("printer/print-labels")
    Call<Void> printLabels(@Body PrintLabelRequest request);

    @GET("printer/status")
    Call<PrinterStatusResponse> getPrinterStatus();

    @GET("printer/templates")
    Call<List<TemplateResponse>> getPrinterTemplates();


    // --- Notifications ---
    @POST("notifications")
    Call<Notification> createNotification(@Body CreateNotificationRequest request);

    @GET("notifications")
    Call<NotificationResponse> getNotifications(@Query("page") int page, @Query("limit") int limit);

    @GET("notifications/unread-count")
    Call<UnreadCountResponse> getUnreadNotificationCount();

    @PATCH("notifications/{id}/read")
    Call<Void> markNotificationAsRead(@Path("id") String notificationId, @Body UpdateNotificationRequest request);

    @PATCH("notifications/mark-all-read")
    Call<Void> markAllNotificationsAsRead();

    @DELETE("notifications/{id}")
    Call<Void> deleteNotification(@Path("id") String notificationId);

    // --- Vendors ---
    @GET("vendors/options")
    Call<OptionsResponse> getVendorOptions();

    @GET("vendors")
    Call<VendorResponse> getVendors(@Query("page") int page, @Query("limit") int limit);

    @POST("vendors")
    Call<Vendor> createVendor(@Body CreateVendorRequest request);

    @GET("vendors/deleted")
    Call<VendorResponse> getDeletedVendors(@Query("page") int page, @Query("limit") int limit);

    @GET("vendors/{id}")
    Call<Vendor> getVendorById(@Path("id") String vendorId);

    @PUT("vendors/{id}")
    Call<Vendor> updateVendor(@Path("id") String vendorId, @Body UpdateVendorRequest request);

    @DELETE("vendors/{id}")
    Call<Void> deleteVendor(@Path("id") String vendorId);

    @PATCH("vendors/{id}/restore")
    Call<Void> restoreVendor(@Path("id") String vendorId);

    @DELETE("vendors/{id}/hard")
    Call<Void> hardDeleteVendor(@Path("id") String vendorId);

    // --- Brands ---
    @GET("brands/options")
    Call<OptionsResponse> getBrandOptions();

    @GET("brands")
    Call<BrandResponse> getBrands(@Query("page") int page, @Query("limit") int limit);

    @POST("brands")
    Call<Brand> createBrand(@Body CreateBrandRequest request);

    @GET("brands/deleted")
    Call<BrandResponse> getDeletedBrands(@Query("page") int page, @Query("limit") int limit);

    @GET("brands/{id}")
    Call<Brand> getBrandById(@Path("id") String brandId);

    @PUT("brands/{id}")
    Call<Brand> updateBrand(@Path("id") String brandId, @Body UpdateBrandRequest request);

    @DELETE("brands/{id}")
    Call<Void> deleteBrand(@Path("id") String brandId);

    @PATCH("brands/{id}/restore")
    Call<Void> restoreBrand(@Path("id") String brandId);

    @DELETE("brands/{id}/hard")
    Call<Void> hardDeleteBrand(@Path("id") String brandId);

    // --- Role Permissions ---
    @GET("role-permissions/get-all")
    Call<List<RolePermissionResponse>> getAllRolePermissions();

    @GET("role-permissions")
    Call<RolePermissionResponse> getRolePermissions(@Query("page") int page, @Query("limit") int limit);

    @GET("role-permissions/{divisionId}/{positionId}")
    Call<RolePermission> getRolePermissionByDivisionAndPosition(@Path("divisionId") String divisionId, @Path("positionId") String positionId);

    @PUT("role-permissions/{divisionId}/{positionId}")
    Call<RolePermission> updateRolePermission(@Path("divisionId") String divisionId, @Path("positionId") String positionId, @Body UpdateRolePermissionRequest request);


    // --- Asset Activation ---
    @GET("asset-activation/settings")
    Call<AssetActivationSettingResponse> getApprovalSettings(@Query("page") int page, @Query("limit") int limit);

    @POST("asset-activation/settings")
    Call<AssetActivationSetting> createApprovalSetting(@Body CreateAssetActivationSettingRequest request);

    @GET("asset-activation/pending-approvals")
    Call<PendingApprovalsResponse> getPendingApprovals(@Query("page") int page, @Query("limit") int limit);

    @Multipart
    @POST("asset-activation/start")
    Call<Void> startAssetActivation(
            @Part("assetCode") RequestBody assetCode,
            @Part MultipartBody.Part photo
    );

    @POST("asset-activation/approve")
    Call<Void> approveAssetActivation(@Body ApproveActivationRequest request);

    @GET("asset-activation/status/{assetId}")
    Call<AssetActivationStatusResponse> getAssetActivationStatus(@Path("assetId") String assetId);

    @PUT("asset-activation/settings/{id}")
    Call<AssetActivationSetting> updateApprovalSetting(@Path("id") String settingId, @Body UpdateAssetActivationSettingRequest request);

    @DELETE("asset-activation/settings/{id}")
    Call<Void> deleteApprovalSetting(@Path("id") String settingId);

    @GET("asset-activation/settings/{id}")
    Call<AssetActivationSetting> getApprovalSettingById(@Path("id") String settingId);

    @GET("asset-activation/{id}")
    Call<AssetActivationResponse> getAssetActivationData(@Path("id") String transactionId);


    // --- Media ---
    @GET("media")
    Call<MediaFileResponse> getMediaFiles(@Query("page") int page, @Query("limit") int limit);

    @GET("media/deleted")
    Call<MediaFileResponse> getDeletedMediaFiles(@Query("page") int page, @Query("limit") int limit);

    @GET("media/{id}")
    Call<MediaFile> getMediaFileById(@Path("id") String mediaId);

    @DELETE("media/{id}")
    Call<Void> deleteMediaFile(@Path("id") String mediaId);

    @PATCH("media/{id}/restore")
    Call<Void> restoreMediaFile(@Path("id") String mediaId);

    @DELETE("media/{id}/hard")
    Call<Void> hardDeleteMediaFile(@Path("id") String mediaId);

    @GET("media/{id}/check")
    Call<Void> checkMediaFile(@Path("id") String mediaId);


    // --- External ---
    @POST("external/assets")
    Call<Asset> createExternalAsset(@Body ExternalAssetRequest request);
}