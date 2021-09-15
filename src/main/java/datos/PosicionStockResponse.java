/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author administrador
 */
public class PosicionStockResponse implements Payload {
    
    private Date fechaEmision;
    private String descripcion;
    private long numero;
    private BigDecimal factura;
    private BigDecimal remito;
    private String operacion;
    
    public PosicionStockResponse(Date fechaEmision, String descripcion, long numero, BigDecimal factura, BigDecimal remito, String operacion) {
        this.fechaEmision = fechaEmision;
        this.descripcion = descripcion;
        this.numero = numero;
        this.factura = factura;
        this.remito = remito;
        this.operacion = operacion;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
