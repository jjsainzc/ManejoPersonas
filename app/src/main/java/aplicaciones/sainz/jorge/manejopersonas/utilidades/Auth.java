package aplicaciones.sainz.jorge.manejopersonas.utilidades;

import java.util.List;

/**
 * Clase para establece autorizacion y roles
 */
public class Auth {
    private String user;
    private String pass;
    private List<String> roles;

    public Auth() {
    }

    public Auth(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public Auth(String user, String pass, List<String> roles) {
        this.user = user;
        this.pass = pass;
        this.roles = roles;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
