/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import entidades.Master;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author administrador
 */
public class MasterResponse implements Payload {

    private Date ingreso;
    private int asiento;
    private String planCuentas;
    private String detalle;
    private BigDecimal importe;
    private Date vencimiento;
    
    public MasterResponse(Master master) {
        this.ingreso = master.getMIngreso();
        this.asiento = master.getMAsiento();
        this.planCuentas = master.getPlanCuentas();
        this.detalle = master.getMDetalle();
        this.importe = master.getMImporte();
        this.vencimiento = master.getMVence();
    }
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
