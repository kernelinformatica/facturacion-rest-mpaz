/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.math.BigDecimal;

/**
 *
 * @author administrador
 */
public class PosicionStockGralResponse implements Payload {
    
    private String codProducto;
    private String descripcion;
    private BigDecimal anterior;
    private BigDecimal facturas;
    private BigDecimal remitos;
    private BigDecimal saldo;
    
    public PosicionStockGralResponse(String codProducto, String descripcion, BigDecimal anterior, BigDecimal facturas, BigDecimal remitos) {
        this.codProducto = codProducto;
        this.descripcion = descripcion;
        this.anterior = anterior;
        this.facturas = facturas;
        this.remitos = remitos;
        this.saldo = anterior.add(facturas).subtract(remitos);
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
