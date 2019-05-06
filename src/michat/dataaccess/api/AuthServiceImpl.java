package michat.dataaccess.api;

import michat.dataaccess.model.User;
import okhttp3.FormBody;

public class AuthServiceImpl extends BaseService implements AuthService {

    @Override
    public void login(String username, String password, ResponseListener<User> responseListener) {
        postJsonObject("/api/auth/login", new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build(), User.class, responseListener);
    }
}