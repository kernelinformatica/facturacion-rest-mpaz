package datos;
/**
 *
 * @author FrancoSili
 */
public class MenuSucursalResponse {
    private MenuResponse menuResponse;

    public MenuSucursalResponse(entidades.Menu ms) {
        this.menuResponse = new MenuResponse(ms);
    }
    
    public MenuResponse getMenuResponse() {
        return menuResponse;
    }

    public void setMenuResponse(MenuResponse menuResponse) {
        this.menuResponse = menuResponse;
    }

    
}
