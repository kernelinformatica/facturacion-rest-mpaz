package datos;

import entidades.CteTipo;
import entidades.OrdenesPagosPCab;
import entidades.OrdenesPagosDetalle;
import entidades.FactCab;
import entidades.Sucursal;
import java.math.BigDecimal;
import java.sql.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author DaarioQuiroga
 */
public class OrdenPagoDetalleResponse implements Payload {
   private Integer idOPDetalle; 
   private Integer idOPCab; 
   private Integer item;
   private  Integer idFactCab;
   private BigDecimal pagadoDolar;  
   private BigDecimal importePesificado;
   private Integer idFormaPago;
   private BigDecimal cotDolarFact;
   private BigDecimal difCotizacion;
   private BigDecimal ivaDifCotizacion;
   private Integer idIVA;
    
    public OrdenPagoDetalleResponse(OrdenesPagosDetalle d) throws ParseException {
        this.idOPDetalle = d.getIdOPDetalle();
        this.idOPCab =  d.getIdOPCab().getIdOPCab();
        this.item = d.getItem();
        this.idFactCab = d.getIdFactCab();
        this.pagadoDolar = d.getPagadoDolar();
        this.importePesificado = d.getImportePesificado();
        this.idFormaPago = d.getIdFormaPago();
        this.cotDolarFact = d.getCotDolarFact();
        this.difCotizacion = d.getDifCotizacion();
        this.idIVA = d.getIdIVA();
        this.ivaDifCotizacion = d.getIvaDifCotizacion();
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
    
    public OrdenPagoDetalleResponse(
            Integer idOPDetalle, 
            Integer idOPCab, 
            Integer item, 
            Integer idFactCab,
            BigDecimal pagadoDolar,
            BigDecimal importePesificado,
            Integer idFormaPago,
            BigDecimal cotDolarFact,
            BigDecimal difCotizacion,
            Integer idIVA,
            BigDecimal ivaDifCotizacion) {
        this.idOPDetalle = idOPDetalle;
        this.idOPCab =  idOPCab;
        this.item = item;
        this.idFactCab = idFactCab;
        this.pagadoDolar = pagadoDolar;
        this.importePesificado = importePesificado;
        this.idFormaPago = idFormaPago;
        this.cotDolarFact = cotDolarFact;
        this.difCotizacion = difCotizacion;
        this.idIVA = idIVA;
        this.ivaDifCotizacion = ivaDifCotizacion;
       
    }

   
   
    

    public Integer getIdOPDetalle() {
        return idOPDetalle;
    }

    public void setIdOPDetalle(Integer idOPDetalle) {
        this.idOPDetalle = idOPDetalle;
    }

    public Integer getIdOPCab() {
        return idOPCab;
    }

    public void setIdOPCab(Integer idOPCab) {
        this.idOPCab = idOPCab;
    }

   

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Integer getIdFactCab() {
        return idFactCab;
    }

    public void setIdFactCab(Integer idFactCab) {
        this.idFactCab = idFactCab;
    }

    public BigDecimal getPagadoDolar() {
        return pagadoDolar;
    }

    public void setPagadoDolar(BigDecimal pagadoDolar) {
        this.pagadoDolar = pagadoDolar;
    }

    public BigDecimal getImportePesificado() {
        return importePesificado;
    }

    public void setImportePesificado(BigDecimal importePesificado) {
        this.importePesificado = importePesificado;
    }

    public Integer getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(Integer idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public BigDecimal getCotDolarFact() {
        return cotDolarFact;
    }

    public void setCotDolarFact(BigDecimal cotDolarFact) {
        this.cotDolarFact = cotDolarFact;
    }

    public BigDecimal getDifCotizacion() {
        return difCotizacion;
    }

    public void setDifCotizacion(BigDecimal difCotizacion) {
        this.difCotizacion = difCotizacion;
    }

    public BigDecimal getIvaDifCotizacion() {
        return ivaDifCotizacion;
    }

    public void setIvaDifCotizacion(BigDecimal ivaDifCotizacion) {
        this.ivaDifCotizacion = ivaDifCotizacion;
    }

    public Integer getIdIVA() {
        return idIVA;
    }

    public void setIdIVA(Integer idIVA) {
        this.idIVA = idIVA;
    }

    /*public FactCabResponse getFactura() {
        return FactCab;
    }

    public void setFactura(FactCabResponse FactCab) {
        this.FactCab = FactCab;
    }*/
    
     
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 
}
