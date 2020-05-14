package com.xitij.tiktuk.API;

import com.google.gson.JsonElement;
import com.xitij.tiktuk.POJO.Banner.Request.BannerRequest;
import com.xitij.tiktuk.POJO.Banner.Response.BannerResponse;
import com.xitij.tiktuk.POJO.ChangePassword.ChangePasswordRequest;
import com.xitij.tiktuk.POJO.ChangePassword.ChangePasswordResponse;
import com.xitij.tiktuk.POJO.Comment.Delete.Request.DeleteCommentRequest;
import com.xitij.tiktuk.POJO.Comment.Delete.Response.DeleteCommentResponse;
import com.xitij.tiktuk.POJO.Comment.Recieve.Request.CommentRequest;
import com.xitij.tiktuk.POJO.Comment.Recieve.Response.CommentResponse;
import com.xitij.tiktuk.POJO.Comment.Send.Request.AddCommentRequest;
import com.xitij.tiktuk.POJO.Comment.Send.Response.AddCommentResponse;
import com.xitij.tiktuk.POJO.Contest.Request.ContestRequest;
import com.xitij.tiktuk.POJO.Contest.Response.ContestResponse;
import com.xitij.tiktuk.POJO.ContestVideo.ContestVideoResponse;
import com.xitij.tiktuk.POJO.ContestVideo.Request.ContestVideoRequest;
import com.xitij.tiktuk.POJO.Follow.DoFollowRequest;
import com.xitij.tiktuk.POJO.Follow.DoFollowResponse;
import com.xitij.tiktuk.POJO.Following.FollowingResponse;
import com.xitij.tiktuk.POJO.Following.FollowingRequest;
import com.xitij.tiktuk.POJO.ForgetPassword.Request.ForgetPasswordRequest;
import com.xitij.tiktuk.POJO.ForgetPassword.Response.ForgetPasswordResponse;
import com.xitij.tiktuk.POJO.LikenShare.Requests.AddLikeRequest;
import com.xitij.tiktuk.POJO.LikenShare.Requests.ShareRequest;
import com.xitij.tiktuk.POJO.LikenShare.LikenShareResponse;
import com.xitij.tiktuk.POJO.LogIn.Response.LogIn;
import com.xitij.tiktuk.POJO.LogIn.Request.LogInRequest;
import com.xitij.tiktuk.POJO.Search.Request.SearchRequest;
import com.xitij.tiktuk.POJO.Search.Response.SearchResponse;
import com.xitij.tiktuk.POJO.SignUp.Request.SignUpRequest;
import com.xitij.tiktuk.POJO.SignUp.Response.SignUpResponse;
import com.xitij.tiktuk.POJO.Upload.Multipart.UploadImageResponse;
import com.xitij.tiktuk.POJO.Upload.Multipart.UploadVideoResponse;
import com.xitij.tiktuk.POJO.Upload.Video.AddVideoRequest;
import com.xitij.tiktuk.POJO.Upload.Video.AddVideoResponse;
import com.xitij.tiktuk.POJO.User.Request.UserRequest;
import com.xitij.tiktuk.POJO.User.UpdateProfile.Request.UpdateProfileRequest;
import com.xitij.tiktuk.POJO.User.UpdateProfile.UpdateProfileResponse;
import com.xitij.tiktuk.POJO.User.UserResponse;
import com.xitij.tiktuk.POJO.Username.Request.UsernameRequest;
import com.xitij.tiktuk.POJO.Username.UsernameResponse;
import com.xitij.tiktuk.POJO.Video.DeleteVideo.DeleteVideoRequest;
import com.xitij.tiktuk.POJO.Video.DeleteVideo.DeleteVideoResponse;
import com.xitij.tiktuk.POJO.Video.Request.Popular.PopularVideoRequest;
import com.xitij.tiktuk.POJO.Video.Request.Live.LiveVideoRequest;
import com.xitij.tiktuk.POJO.Video.Request.Following.FollowingVideoRequest;
import com.xitij.tiktuk.POJO.Video.VideoResponse;
import com.xitij.tiktuk.POJO.payment.PaymentResponse;
import com.xitij.tiktuk.POJO.paymentdetail.PaymentDetailResponse;
import com.xitij.tiktuk.POJO.service.Service;
import com.xitij.tiktuk.POJO.service.ServiceResponse;
import com.xitij.tiktuk.POJO.userpoints.UserPointsResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;

public interface ApiInterface {

    /*
     * Get Log In API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<LogIn> getLogIn(@Body LogInRequest request);

    /*
     * Get SignUP API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<SignUpResponse> getSignUp(@Body SignUpRequest request);

    /*
    * Get Forget Password API
    * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<ForgetPasswordResponse> getForgetPassword(@Body ForgetPasswordRequest request);

    /*
     * Get Change Password API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<ChangePasswordResponse> getChangePassword(@Body ChangePasswordRequest request);

    /*
     * Get Existing Username API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<UsernameResponse> getUsername(@Body UsernameRequest request);

    /*
     * Get Banner API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<BannerResponse> getBanner(@Body BannerRequest request);

    /*
     * Get Popular Videos API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<VideoResponse> getPopularVideos(@Body PopularVideoRequest request);

    /*
     * Get Live Videos API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<VideoResponse> getLiveVideos(@Body LiveVideoRequest request);

    /*
     * Get Live Videos API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<VideoResponse> getFollowingVideos(@Body FollowingVideoRequest request);

    /*
     * Get Own User Profile API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<UserResponse> getUserProfile(@Body UserRequest request);

    /*
     * Get Search People API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<SearchResponse> getSearchPeople(@Body SearchRequest request);

    /*
     * Get Contest List API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<ContestResponse> getContestList(@Body ContestRequest request);

    /*
     * Get Contest Videos API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<ContestVideoResponse> getContestVideos(@Body ContestVideoRequest request);

    /*
     * Get Video Comments API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<CommentResponse> getVideoComment(@Body CommentRequest request);

    /*
     * Set Video Comments API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<AddCommentResponse> addVideoComment(@Body AddCommentRequest request);

    /*
     * Delete Video Comments API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<DeleteCommentResponse> removeVideoComment(@Body DeleteCommentRequest request);

    /*
     * Add Video after upload API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<AddVideoResponse> addVideo(@Body AddVideoRequest request);

    /*
     * Add Video after upload API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<DeleteVideoResponse> deleteVideo(@Body DeleteVideoRequest request);

    /*
     * Add Like Video API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<LikenShareResponse> addLike(@Body AddLikeRequest request);

    /*
     * Add Share Video API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<LikenShareResponse> saveShare(@Body ShareRequest request);

    /*
     * Get Following People API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<FollowingResponse> getFollowingPeople(@Body FollowingRequest request);

    /*
     * Update User Profile API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<UpdateProfileResponse> updateProfile(@Body UpdateProfileRequest request);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<ServiceResponse> countView(@Body Service service);

    /*
     * Start Following/Unfollowing the person API
     * */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<DoFollowResponse> doFollow(@Body DoFollowRequest request);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<PaymentResponse> getPaymentService(@Body Service service);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<UserPointsResponse> getUserPoints(@Body Service service);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<PaymentDetailResponse> addPaymentDetail(@Body Service service);

    /**
     * get audio list for mixing with video
     * @param service
     * @return
     */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("WebService/service")
    Call<JsonElement> audioService(@Body Service service);

    /*
    *
    * Multipart for posting Videos and pictures
    * */


    /*
    *For Posting Videos
    * */
    @Multipart
    @POST("upload/uploadvideo")
    Call<UploadVideoResponse> uploadVideo(@Part MultipartBody.Part video, @Part MultipartBody.Part image);

    /*
     *Update Profile Picture
     * */
    @Multipart
    @POST("upload/uploadprofilepic")
    Call<UploadImageResponse> uploadProfilePhoto(@Part MultipartBody.Part image);

}
