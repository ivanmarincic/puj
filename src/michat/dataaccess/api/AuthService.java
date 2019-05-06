package michat.dataaccess.api;

import michat.dataaccess.model.User;

public interface AuthService {
    void login(String username, String password, BaseService.ResponseListener<User> responseListener);
}
