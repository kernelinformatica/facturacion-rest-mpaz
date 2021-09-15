package datos;

/**
 *
 * @author Franco Sili
 */
public class MenuResponse {
    private String idMenu;
    private String nombre;
    private String idPadre;
    private Integer orden;
    private String icono;
    private String nombreForm;
    
    public MenuResponse(entidades.Menu menu) {
        this.idMenu = menu.getIdMenu();
        this.nombre = menu.getNombre();
        this.idPadre = menu.getIdPadre();
        this.orden = menu.getOrden();
        this.icono = menu.getIcono();
        this.nombreForm = menu.getNombreForm();
    }
    
    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(String idPadre) {
        this.idPadre = idPadre;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
    
    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getNombreForm() {
        return nombreForm;
    }

    public void setNombreForm(String nombreForm) {
        this.nombreForm = nombreForm;
    }
    
    
 
}
