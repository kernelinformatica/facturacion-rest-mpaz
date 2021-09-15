package datos;

import entidades.SisCotDolar;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */
public class SisCotDolarResponse implements Payload{
    private Integer idSisCotDolar;
    private Date fechaCotizacion;
    private BigDecimal cotizacion;

    public SisCotDolarResponse(SisCotDolar s) {
        this.idSisCotDolar = s.getIdSisCotDolar();
        this.fechaCotizacion = s.getFechaCotizacion();
        this.cotizacion = s.getCotizacion();
    }
    
    public Integer getIdSisCotDolar() {
        return idSisCotDolar;
    }

    public void setIdSisCotDolar(Integer idSisCotDolar) {
        this.idSisCotDolar = idSisCotDolar;
    }

    public Date getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(Date fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
