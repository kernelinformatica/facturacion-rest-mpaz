package datos;

import entidades.Permiso;

/**
 *
 * @author FrancoSili
 */
public class PermisosResponse {
    private Integer idPermiso;
    private Boolean alta;
    private Boolean baja;
    private Boolean modificacion;
    private MenuResponse menu;

    public PermisosResponse(Permiso p) {
        this.idPermiso = p.getIdPermiso();
        this.alta = p.getAlta();
        this.baja = p.getBaja();
        this.modificacion = p.getModificacion();
        this.menu = new MenuResponse(p.getMenu());
    }

    
    
    public Integer getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Integer idPermiso) {
        this.idPermiso = idPermiso;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    public Boolean getBaja() {
        return baja;
    }

    public void setBaja(Boolean baja) {
        this.baja = baja;
    }

    public Boolean getModificacion() {
        return modificacion;
    }

    public void setModificacion(Boolean modificacion) {
        this.modificacion = modificacion;
    }

    public MenuResponse getMenu() {
        return menu;
    }

    public void setMenu(MenuResponse menu) {
        this.menu = menu;
    }
    
    
}
