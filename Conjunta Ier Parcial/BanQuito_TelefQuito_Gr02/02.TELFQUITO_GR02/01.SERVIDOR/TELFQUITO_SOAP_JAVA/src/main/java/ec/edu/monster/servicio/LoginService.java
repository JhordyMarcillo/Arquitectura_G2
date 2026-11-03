package ec.edu.monster.servicio;

public class LoginService {
     public boolean login(String username, String password) {
        if ((username.equals("monster") && password.equals("774e993500f4027acfd72b7a7ee564b76ae43cf7c4c943ed0e0f364cca16b6ec")) || 
            (username.equals("admin") && password.equals("admin"))) {
            return true;
        }
        return false;
    }
}