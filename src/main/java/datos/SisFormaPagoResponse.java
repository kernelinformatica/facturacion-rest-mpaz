package datos;

import entidades.SisFormaPago;

/**
 *
 * @author FrancoSili
 */
public class SisFormaPagoResponse implements Payload {
    private Integer idSisFormaPago;
    private String descripcion;
    
    public SisFormaPagoResponse(SisFormaPago s) {
        this.idSisFormaPago = s.getIdSisFormaPago();
        this.descripcion = s.getDescripcion();
    }

    public Integer getIdSisFormaPago() {
        return idSisFormaPago;
    }

    public void setIdSisFormaPago(Integer idSisFormaPago) {
        this.idSisFormaPago = idSisFormaPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripción) {
        this.descripcion = descripción;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
