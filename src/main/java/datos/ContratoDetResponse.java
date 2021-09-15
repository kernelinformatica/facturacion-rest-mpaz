package datos;

import entidades.ContratoDet;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */

public class ContratoDetResponse {
    private Integer idContratosDet;
    private Integer kilos;
    private BigDecimal importe;
    private String observaciones;
    private FactCabResponse factCab;

    public ContratoDetResponse(ContratoDet c) {
        this.idContratosDet = c.getIdContratosDet();
        this.kilos = c.getKilos();
        this.importe = c.getImporte();
        this.observaciones = c.getObservaciones();
        this.factCab = new FactCabResponse(c.getIdFactCab());
    }
       
    public Integer getIdContratosDet() {
        return idContratosDet;
    }

    public void setIdContratosDet(Integer idContratosDet) {
        this.idContratosDet = idContratosDet;
    }

    public Integer getKilos() {
        return kilos;
    }

    public void setKilos(Integer kilos) {
        this.kilos = kilos;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public FactCabResponse getFactCab() {
        return factCab;
    }

    public void setFactCab(FactCabResponse factCab) {
        this.factCab = factCab;
    }
    
    
}
