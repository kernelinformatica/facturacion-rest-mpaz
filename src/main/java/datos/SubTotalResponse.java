package datos;

import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class SubTotalResponse implements Payload {
    private BigDecimal subTotal;
    private BigDecimal subTotalIva;
    private BigDecimal precioDesc;

    public SubTotalResponse(BigDecimal subTotal, BigDecimal subTotalIva, BigDecimal precioDesc) {
        this.subTotal = subTotal;
        this.subTotalIva = subTotalIva;
        this.precioDesc = precioDesc;
    }

    public SubTotalResponse() {
        this.subTotal = new BigDecimal(0);
        this.subTotalIva = new BigDecimal(0);
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getSubTotalIva() {
        return subTotalIva;
    }

    public void setSubTotalIva(BigDecimal subTotalIva) {
        this.subTotalIva = subTotalIva;
    }

    public BigDecimal getPrecioDesc() {
        return precioDesc;
    }

    public void setPrecioDesc(BigDecimal precioDesc) {
        this.precioDesc = precioDesc;
    }

    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
