package datos;

import entidades.SisIVA;
import java.math.BigDecimal;

/**
 *
 * @author Franco Sili
 */
public class SisIVAResponse implements Payload {
    private Integer idIVA;
    private String descripción;
    private String descCorta;
    private BigDecimal porcIVA;

    public SisIVAResponse(SisIVA s) {
        this.idIVA = s.getIdIVA();
        this.descCorta = s.getDescCorta();
        this.descripción = s.getDescripcion();
        this.porcIVA = s.getPorcIVA();
    }
    
    public Integer getIdIVA() {
        return idIVA;
    }

    public void setIdIVA(Integer idIVA) {
        this.idIVA = idIVA;
    }

    public String getDescripción() {
        return descripción;
    }

    public void setDescripción(String descripción) {
        this.descripción = descripción;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

    public BigDecimal getPorcIVA() {
        return porcIVA;
    }

    public void setPorcIVA(BigDecimal porcIVA) {
        this.porcIVA = porcIVA;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
