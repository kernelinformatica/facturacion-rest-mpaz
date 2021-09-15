package datos;


import entidades.OrdenesPagosPCab;
import entidades.OrdenesPagosPie;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 *
 * @author DarioQuiroga
 */
public class OrdenPagoPieResponse implements Payload {
   private  Integer idOPPie; 
   private Integer idOPCab; 
   private  Integer idImpuesto;
   private String detalle;
   private BigDecimal alicuota;
   private BigDecimal importeBase;
   private BigDecimal importeImpuesto;
   private long numeroRetencion;
    
    
    public OrdenPagoPieResponse(OrdenesPagosPie p) {
        this.idOPPie = p.getIdOPPie();
        this.idOPCab =  p.getIdOPCab().getIdOPCab();
        this.idImpuesto = p.getIdImpuesto();
        this.detalle = p.getDetalle();
        this.alicuota = p.getAlicuota();
        this.importeBase= p.getImporteBase();
        this.importeImpuesto= p.getImporteImpuesto();
        this.numeroRetencion= p.getNumeroRetencion();
                
    }

    public OrdenPagoPieResponse(
            int idOPPie, 
            int idOPCab, 
            int idImpuesto, 
            String detalle, 
            BigDecimal alicuota, 
            BigDecimal importeBase, 
            BigDecimal importeImpuesto, 
            long numeroRetencion) {
        
            this.idOPPie = idOPPie;
            this.idOPCab = idOPCab;
            this.idImpuesto = idImpuesto;
            this.detalle = detalle;
            this.alicuota = alicuota;
            this.importeBase = importeBase;
            this.importeImpuesto = importeImpuesto;
            this.numeroRetencion = numeroRetencion;
        
    }

    public Integer getIdOPPie() {
        return idOPPie;
    }

    public void setIdOPPie(Integer idOPPie) {
        this.idOPPie = idOPPie;
    }

    public Integer getIdOPCab() {
        return idOPCab;
    }

    public void setIdOPCab(Integer idOPCab) {
        this.idOPCab = idOPCab;
    }

    public long getNumeroRetencion() {
        return numeroRetencion;
    }

    public void setNumeroRetencion(long numeroRetencion) {
        this.numeroRetencion = numeroRetencion;
    }

   

    public Integer getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(Integer idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }

   //-------------
    public BigDecimal getImporteBase() {
        return importeBase;
    }

    public void setImporteBase(BigDecimal importeBase) {
        this.importeBase = importeBase;
    }
    
    public BigDecimal 
        getImporteImpuesto() {
        return importeImpuesto;
    }

    public void setImporteImpuesto(BigDecimal importeImpuesto) {
        this.importeImpuesto = importeImpuesto;
    }
   

    public void setNumeroRetencion(BigDecimal numeroRetencion) {
        this.alicuota = numeroRetencion;
    }
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 
}
