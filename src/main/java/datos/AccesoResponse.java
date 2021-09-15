package datos;

import java.util.Date;
import entidades.Acceso;

/**
 *
 * @author FrancoSili
 */
public class AccesoResponse {
    private String token;
    private java.util.Date fechaDesde;
    private java.util.Date fechaHasta;

    public AccesoResponse(Acceso acceso) {
        this.token = acceso.getToken();
        this.fechaDesde = acceso.getFechaDesde();
        this.fechaHasta = acceso.getFechaHasta();
    }
    
    ////////////////////////////////////////////////////////////
    /////////             GETTERS AND SETTERS          /////////
    ////////////////////////////////////////////////////////////

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
}
