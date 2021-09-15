/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import entidades.CanjesContratosCereales;
import entidades.ParametrosCanjes;
import java.math.BigDecimal;

/**
 *
 * @author administrador
 */
public class ParametrosCanjeResponse implements Payload {
    
    Integer idParametroCanje;
    BigDecimal interesDiario;
    short diasLibres;
    String ctaContableSisa;
    CanjesContratosCerealesResponse canjeCereal;
    
    public ParametrosCanjeResponse(ParametrosCanjes pc) {
        this.idParametroCanje = pc.getIdParametroCanje();
        this.interesDiario = pc.getInteresDiario();
        this.diasLibres = pc.getDiasLIbres();
        this.ctaContableSisa = pc.getCtaContableSisa();
        this.canjeCereal = new CanjesContratosCerealesResponse(pc.getCanjeCereal());
    }
    
    public void setIdParametroCanje(Integer idParametroCanje) {
        this.idParametroCanje = idParametroCanje;
    }
    
    public Integer getIdParametroCanje() {
        return idParametroCanje;
    }
    
    public void setInteresDiario(BigDecimal interesDiario) {
        this.interesDiario = interesDiario;
    }
    
    public BigDecimal getInteresDiario() {
        return interesDiario;
    }
    
    public void setDiasLibres(short diasLibres) {
        this.diasLibres = diasLibres;
    }
    
    public short getDiasLibres() {
        return diasLibres;
    }
    
    public void setCtaContableSisa(String ctaContableSisa) {
        this.ctaContableSisa = ctaContableSisa;
    }
    
    public String getCtaContableSisa() {
        return ctaContableSisa;
    }
    
    public void setCanjeCereal(CanjesContratosCereales canjeCereal) {
        this.canjeCereal = new CanjesContratosCerealesResponse(canjeCereal);
    }
    
    public CanjesContratosCerealesResponse getCanjeCereal() {
        return canjeCereal;
    }
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
