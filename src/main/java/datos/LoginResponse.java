package datos;

import entidades.Acceso;
import entidades.Usuario;

/*
 *
 * @author FrancoSili
 */

public class LoginResponse implements Payload{
    private UsuarioResponse cuenta;
    private PerfilResponse perfil;
    private AccesoResponse acceso;
    
    public LoginResponse(Usuario usuario, Acceso acceso) {
        this.cuenta = new UsuarioResponse(usuario);
        this.perfil = new PerfilResponse(usuario.getIdPerfil());
        this.acceso = new AccesoResponse(acceso);
    }
    
    ////////////////////////////////////////////////////////////
    /////////             GETTERS AND SETTERS          /////////
    ////////////////////////////////////////////////////////////

    public PerfilResponse getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilResponse perfil) {
        this.perfil = perfil;
    }

    public UsuarioResponse getCuenta() {
        return cuenta;
    }

    public void setCuenta(UsuarioResponse cuenta) {
        this.cuenta = cuenta;
    }

    public AccesoResponse getAcceso() {
        return acceso;
    }

    public void setAcceso(AccesoResponse acceso) {
        this.acceso = acceso;
    }
    
    

    ////////////////////////////////////////////////////////////
    /////////                 OVERRIDE                 /////////
    ////////////////////////////////////////////////////////////
    
    @Override
    public String getClassName() {
        return LoginResponse.class.getSimpleName();
    }
}
