package rh.model.session;

public class userConnecter {
    private static String username;
    private static String role;
    private static String id;

    public static void setUser(String user, String userRole) {
        username = user;
        role = userRole;
    }
    public static void setUser(String idEmploye){
        id = idEmploye;
    }

    public static String getId(){
        return id;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static void clear() {
        username = null;
        role = null;
    }
}
