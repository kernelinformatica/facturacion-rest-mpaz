package datos;

import entidades.Perfil;
import java.util.List;

/**
 *
 * @author FrancoSili
 */
public class PerfilResponse implements Payload{
    private Integer idPerfil;
    private String descripcion;
    private List<PermisosResponse> permisos;
    private SucursalResponse sucursal;
    

    public PerfilResponse(Perfil perfil) {
        this.idPerfil = perfil.getIdPerfil();
        this.descripcion = perfil.getDescripcion();
        this.sucursal = new SucursalResponse(perfil.getIdSucursal());
    }
    
    ////////////////////////////////////////////////////////////
    /////////             GETTERS AND SETTERS          /////////
    ////////////////////////////////////////////////////////////

    public Integer getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Integer idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public SucursalResponse getSucursal() {
        return sucursal;
    }

    public void setSucursal(SucursalResponse sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
