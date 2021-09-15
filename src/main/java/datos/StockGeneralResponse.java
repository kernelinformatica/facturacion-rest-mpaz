package datos;

import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class StockGeneralResponse implements Payload {
    private String codProducto;
    private String descripcion;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal fisicoImputado;
    private BigDecimal stockFisico;
    private BigDecimal ingresoVirtual;
    private BigDecimal egresoVirtual;
    private BigDecimal virtualImputado;
    private BigDecimal stockVirtual;
    private Boolean trazable;
    private String rubro;
    private String subRubro;

    public StockGeneralResponse(String codProducto, String descripcion, BigDecimal ingresos, BigDecimal egresos, BigDecimal fisicoImputado, BigDecimal stockFisico, BigDecimal ingresoVirtual, BigDecimal egresoVirtual, BigDecimal virtualImputado, BigDecimal stockVirtual, Boolean trazable, String rubro, String subRubro) {
        this.codProducto = codProducto;
        this.descripcion = descripcion;
        this.ingresos = ingresos;
        this.egresos = egresos;
        this.fisicoImputado = fisicoImputado;
        this.stockFisico = stockFisico;
        this.ingresoVirtual = ingresoVirtual;
        this.egresoVirtual = egresoVirtual;
        this.virtualImputado = virtualImputado;
        this.stockVirtual = stockVirtual;
        this.trazable = trazable;
        this.rubro = rubro;
        this.subRubro = subRubro;
    }


    
    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public Boolean getTrazable() {
        return trazable;
    }

    public void setTrazable(Boolean trazable) {
        this.trazable = trazable;
    }

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public String getSubRubro() {
        return subRubro;
    }

    public void setSubRubro(String subRubro) {
        this.subRubro = subRubro;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getFisicoImputado() {
        return fisicoImputado;
    }

    public void setFisicoImputado(BigDecimal fisicoImputado) {
        this.fisicoImputado = fisicoImputado;
    }

    public BigDecimal getIngresoVirtual() {
        return ingresoVirtual;
    }

    public void setIngresoVirtual(BigDecimal ingresoVirtual) {
        this.ingresoVirtual = ingresoVirtual;
    }

    public BigDecimal getEgresoVirtual() {
        return egresoVirtual;
    }

    public void setEgresoVirtual(BigDecimal egresoVirtual) {
        this.egresoVirtual = egresoVirtual;
    }

    public BigDecimal getVirtualImputado() {
        return virtualImputado;
    }

    public void setVirtualImputado(BigDecimal virtualImputado) {
        this.virtualImputado = virtualImputado;
    }

    public BigDecimal getStockFisico() {
        return stockFisico;
    }

    public void setStockFisico(BigDecimal stockFisico) {
        this.stockFisico = stockFisico;
    }

    public BigDecimal getStockVirtual() {
        return stockVirtual;
    }

    public void setStockVirtual(BigDecimal stockVirtual) {
        this.stockVirtual = stockVirtual;
    }
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
