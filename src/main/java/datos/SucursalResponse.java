package datos;

import entidades.Menu;
import entidades.Permiso;
import entidades.Sucursal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Franco Sili
 */
public class SucursalResponse implements Payload {
    private Integer idSucursal;
    private String nombre;
    private String domicilio;
    private String codigoPostal;
    private EmpresaResponse empresa;
    private List<PermisosResponse> permisos;

    public SucursalResponse (Sucursal sucursal) {
        this.idSucursal = sucursal.getIdSucursal();
        this.nombre = sucursal.getNombre();
        this.domicilio = sucursal.getDomicilio();
        this.codigoPostal = sucursal.getCodigoPostal();
        this.empresa = new EmpresaResponse(sucursal.getIdEmpresa());
        this.permisos = new ArrayList<>();    
    }
    
    ////////////////////////////////////////////////////////////
    /////////             GETTERS AND SETTERS          /////////
    ////////////////////////////////////////////////////////////

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public EmpresaResponse getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaResponse empresa) {
        this.empresa = empresa;
    }

    public List<PermisosResponse> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<PermisosResponse> permisos) {
        this.permisos = permisos;
    }
     
    public void agregarPermisos(Collection<Permiso> permisos) {
       for(Permiso p : permisos) {
            PermisosResponse msr = new PermisosResponse(p);
            this.permisos.add(msr);
        }
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
