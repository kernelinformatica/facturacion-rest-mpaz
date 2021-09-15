/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import entidades.SisaPorcentaje;
import java.math.BigDecimal;

/**
 *
 * @author administrador
 */
public class EstadoSisaResponse implements Payload {
    private Integer idSisPorcentajeSisa;
    private Integer codEstado;
    private BigDecimal iva;
    private BigDecimal ganancias;
    private BigDecimal ivaArroz;
    private BigDecimal gananciaArroz;
    private BigDecimal percepIva;
    private String ctaIva;
    private String ctaGan;
    private Boolean isCurrent;
            
    public EstadoSisaResponse(SisaPorcentaje sisaPorcentaje) {
        this.idSisPorcentajeSisa = sisaPorcentaje.getIdSisPorcentajeSisa();
        this.codEstado = sisaPorcentaje.getCodEstado();
        this.iva = sisaPorcentaje.getIva();
        this.ganancias = sisaPorcentaje.getGanancias();
        this.ivaArroz = sisaPorcentaje.getIvaArroz();
        this.gananciaArroz = sisaPorcentaje.getGananciaArroz();
        this.percepIva = sisaPorcentaje.getPercepIva();
        this.ctaIva = sisaPorcentaje.getCtaIva();
        this.ctaGan = sisaPorcentaje.getCtaGan();
        this.isCurrent = false;
    }
    
    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
