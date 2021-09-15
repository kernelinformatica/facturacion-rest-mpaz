package datos;

import entidades.SisMonedas;

/**
 *
 * @author FrancoSili
 */
public class SisMonedasResponse implements Payload {
    private Integer idMoneda;
    private String descripcion;
    
    public SisMonedasResponse(SisMonedas s) {
        this.idMoneda = s.getIdMoneda();
        this.descripcion = s.getDescripcion();
    }
    
    public Integer getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(Integer idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
