package datos;

import entidades.FactPie;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DarioQuiroga

 */
public class FactPieResponse implements Payload{
    private int idFactPie;
    private String detalle;
    private  BigDecimal porcentaje;
    private String ctaContable;
    private int idConceptos;
    private int idFactCab;
    private int idSisTipoModelo;
    private BigDecimal baseImponible;
    private String operador;

    

    public FactPieResponse(int idFactPie, String detalle, BigDecimal porcentaje, String ctaContable, int idConceptos, int idFactCab,int idSisTipoModelo, BigDecimal baseImponible, String operador) {
        this.idFactPie = idFactPie;
        this.detalle = detalle;
        this.porcentaje = porcentaje;
         this.ctaContable = ctaContable;
        this.idConceptos = idConceptos;
        this.idFactCab = idFactCab;
        this.idSisTipoModelo = idSisTipoModelo;
        this.baseImponible = baseImponible;
        this.operador = operador;
                
        
    }
    
    public FactPieResponse(FactPie p) {
        this.idFactPie = p.getIdFactPie();
        this.detalle = p.getDetalle();
        this.porcentaje = p.getPorcentaje();
        this.ctaContable = p.getCtaContable();
        this.idConceptos = p.getIdConceptos().getIdConceptos();
        this.idFactCab = p.getIdFactCab().getIdFactCab();
        this.idSisTipoModelo = p.getIdSisTipoModelo().getIdSisTipoModelo();
        this.baseImponible = p.getBaseImponible();
        this.operador = p.getOperador();
    }

    public int getIdFactCab() {
        return idFactCab;
    }

    public void setIdFactCab(int idFactCab) {
        this.idFactCab = idFactCab;
    }

    public int getIdFactPie() {
        return idFactPie;
    }

    public void setIdFactPie(int idFactPie) {
        this.idFactPie = idFactPie;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getCtaContable() {
        return ctaContable;
    }

    public void setCtaContable(String ctaContable) {
        this.ctaContable = ctaContable;
    }

    public int getIdConceptos() {
        return idConceptos;
    }

    public void setIdConceptos(int idConceptos) {
        this.idConceptos = idConceptos;
    }

    public int getIdSisTipoModelo() {
        return idSisTipoModelo;
    }

    public void setIdSisTipoModelo(int idSisTipoModelo) {
        this.idSisTipoModelo = idSisTipoModelo;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    
    
   
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
