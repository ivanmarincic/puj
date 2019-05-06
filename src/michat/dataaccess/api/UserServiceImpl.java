package michat.dataaccess.api;

import michat.Main;
import michat.dataaccess.model.User;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.List;

public class UserServiceImpl extends BaseService implements UserService {

    @Override
    public String getProfileImageUrl(String filename) {
        return "https://ivanmarincic.com/mi-chat/api/users/profileImage/" + filename;
    }

    @Override
    public void setProfileImage(File file) {
        setLoggedInUser(Main.loggedInUser);
        postString("/api/users/setProfileImage",
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file)).build(), null);
    }

    @Override
    public void updateProfile(User user, boolean changePassword, ResponseListener<User> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonObject("/api/users/updateProfile", new FormBody.Builder()
                .add("full_name", user.getFullname())
                .add("email", user.getEmail())
                .add("change_password", changePassword ? "true" : "false")
                .add("password", user.getPassword() != null ? user.getPassword() : "")
                .build(), User.class, responseListener);
    }

    @Override
    public void search(String searchQuery, ResponseListener<List<User>> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonList("/api/users/search", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"searchQuery\": \"%s\"}", searchQuery)), User.class, responseListener);
    }
}
