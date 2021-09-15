package datos;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */

public class OrdenPagoImputaResponse implements Payload {
    private Integer idOPCab;
    private Integer idCteTipo;
    private String descCorta;
    private Long numero;
    private Date fechaEmision;
    private Integer idPadron;
    private Integer idSisTipoOperacion;

    public OrdenPagoImputaResponse(Integer idOPCab, Integer idCteTipo, String descCorta, Long numero, Date fechaEmision, Integer idPadron, Integer idSisTipoOperacion) {
        this.idOPCab = idOPCab;
        this.idCteTipo = idCteTipo;
        this.descCorta = descCorta;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.idPadron = idPadron;
        this.idSisTipoOperacion = idSisTipoOperacion;
    }

    public Integer getIdOPCab() {
        return idOPCab;
    }

    public void setIdOPCab(Integer idOPCab) {
        this.idOPCab = idOPCab;
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
