package datos;

import entidades.PtoVenta;

/**
 *
 * @author FrancoSili
 */
public class PtoVentaResponse implements Payload {
    private Integer idPtoVenta;
    private Integer ptoVenta;
    private String sucursal;

    public PtoVentaResponse(PtoVenta c) {
        this.idPtoVenta = c.getIdPtoVenta();
        this.ptoVenta = c.getPtoVenta();
        this.sucursal = c.getIdSucursal().getNombre();
    }

    public Integer getIdPtoVenta() {
        return idPtoVenta;
    }

    public void setIdPtoVenta(Integer idPtoVenta) {
        this.idPtoVenta = idPtoVenta;
    }
       
    public Integer getPtoVenta() {
        return ptoVenta;
    }

    public void setPtoVenta(Integer ptoVenta) {
        this.ptoVenta = ptoVenta;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
}
