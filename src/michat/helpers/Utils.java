package michat.helpers;

import michat.dataaccess.model.User;

import java.util.List;

public class Utils {

    public static String convertToParticipantsList(List<User> users) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < users.size(); i++) {
            builder.append(users.get(i).getId());
            if (i < users.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
