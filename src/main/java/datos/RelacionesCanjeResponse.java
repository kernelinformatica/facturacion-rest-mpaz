package datos;

import entidades.RelacionesCanje;
import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */

public class RelacionesCanjeResponse implements Payload {
    private Integer idRelacionSisCanje;
    private Integer codigoCosecha;
    private Integer codigoClase;
    private String descripcion;
    private BigDecimal factor;
    private SisCanjeResponse idSisCanje;

    public RelacionesCanjeResponse(RelacionesCanje r) {
        this.idRelacionSisCanje = r.getIdRelacionSisCanje();
        this.codigoCosecha = r.getCodigoCosecha();
        this.codigoClase = r.getCodigoClase();
        this.descripcion = r.getDescripcion();
        this.factor = r.getFactor();
        this.idSisCanje = new SisCanjeResponse(r.getIdSisCanje());
    }

    public Integer getIdRelacionSisCanje() {
        return idRelacionSisCanje;
    }

    public void setIdRelacionSisCanje(Integer idRelacionSisCanje) {
        this.idRelacionSisCanje = idRelacionSisCanje;
    }

    public Integer getCodigoCosecha() {
        return codigoCosecha;
    }

    public void setCodigoCosecha(Integer codigoCosecha) {
        this.codigoCosecha = codigoCosecha;
    }

    public Integer getCodigoClase() {
        return codigoClase;
    }

    public void setCodigoClase(Integer codigoClase) {
        this.codigoClase = codigoClase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public SisCanjeResponse getIdSisCanje() {
        return idSisCanje;
    }

    public void setIdSisCanje(SisCanjeResponse idSisCanje) {
        this.idSisCanje = idSisCanje;
    }
    
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
