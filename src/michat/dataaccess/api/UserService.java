package michat.dataaccess.api;

import michat.dataaccess.model.User;

import java.io.File;
import java.util.List;

public interface UserService {
    String getProfileImageUrl(String filename);

    void setProfileImage(File file);

    void updateProfile(User user, boolean changePassword, BaseService.ResponseListener<User> responseListener);

    void search(String searchQuerz, BaseService.ResponseListener<List<User>> responseListener);
}
