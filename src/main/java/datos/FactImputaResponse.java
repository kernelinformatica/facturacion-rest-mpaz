package datos;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */

public class FactImputaResponse implements Payload {
    private Integer idFactCab;
    private Integer idCteTipo;
    private String descCorta;
    private Long numero;
    private Date fechaEmision;
    private Integer idPadron;
    private Integer idSisTipoOperacion;

    public FactImputaResponse(Integer idFactCab, Integer idCteTipo, String descCorta, Long numero, Date fechaEmision, Integer idPadron, Integer idSisTipoOperacion) {
        this.idFactCab = idFactCab;
        this.idCteTipo = idCteTipo;
        this.descCorta = descCorta;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.idPadron = idPadron;
        this.idSisTipoOperacion = idSisTipoOperacion;
    }


    
    public Integer getIdFactCab() {
        return idFactCab;
    }

    public void setIdFactCab(Integer idFactCab) {
        this.idFactCab = idFactCab;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Integer getIdCteTipo() {
        return idCteTipo;
    }

    public void setIdCteTipo(Integer idCteTipo) {
        this.idCteTipo = idCteTipo;
    }

    public Integer getIdPadron() {
        return idPadron;
    }

    public void setIdPadron(Integer idPadron) {
        this.idPadron = idPadron;
    }

    public Integer getIdSisTipoOperacion() {
        return idSisTipoOperacion;
    }

    public void setIdSisTipoOperacion(Integer idSisTipoOperacion) {
        this.idSisTipoOperacion = idSisTipoOperacion;
    }

    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
