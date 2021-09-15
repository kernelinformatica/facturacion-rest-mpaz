package datos;

import entidades.FactFormaPago;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DarioQuiroga
 * idFactFormaPago, detalle, importe, porcentaje, fechaPago, diasPago, idFactCab, idFormaPago, ctaContable
 */
public class FactFormaPagoResponse implements Payload{
    private int idFactFormaPago;
    private String detalle;
    private  BigDecimal importe;
    private  BigDecimal porcentaje;
    private Date fechaPago;
    private int diasPago;
    private int idFactCab;
    private int idFormaPago;
    private String ctaContable;
    

    public FactFormaPagoResponse(int idFactFormaPago, String detalle, BigDecimal importe, BigDecimal porcentaje, Date fechaPago, int diasPago, int idFactCab, int idFormaPago, String ctaContable) {
        this.idFactFormaPago = idFactFormaPago;
        this.detalle = detalle;
        this.importe = importe;
        this.porcentaje = porcentaje;
        this.fechaPago = fechaPago;
        this.diasPago = diasPago;
        this.idFactCab = idFactCab;
        this.idFactFormaPago = idFactFormaPago;
        this.ctaContable = ctaContable;
        
        
                
        
    }
    
    public FactFormaPagoResponse(FactFormaPago f) {
        this.idFactFormaPago = f.getIdFactFormaPago();
        this.detalle = f.getDetalle();
        this.importe = f.getImporte();
        this.porcentaje = f.getPorcentaje();
        this.fechaPago = f.getFechaPago();
        this.diasPago = f.getDiasPago();
        this.idFactCab = f.getIdFactCab().getIdFactCab();
        this.idFactFormaPago = f.getIdFactFormaPago();
        this.ctaContable = f.getCtaContable();
    }

    public int getIdFactFormaPago() {
        return idFactFormaPago;
    }

    public void setIdFactFormaPago(int idFactFormaPago) {
        this.idFactFormaPago = idFactFormaPago;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public int getDiasPago() {
        return diasPago;
    }

    public void setDiasPago(int diasPago) {
        this.diasPago = diasPago;
    }

    public int getIdFactCab() {
        return idFactCab;
    }

    public void setIdFactCab(int idFactCab) {
        this.idFactCab = idFactCab;
    }

    public int getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(int idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public String getCtaContable() {
        return ctaContable;
    }

    public void setCtaContable(String ctaContable) {
        this.ctaContable = ctaContable;
    }

    
   
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
