package datos;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */
public class StockResponse implements Payload {
    private String comprobante;
    private BigDecimal numero;
    private Date fechaEmision;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal pendiente;
    private String deposito;
    private Boolean trazable;
    private String rubro;
    private String subRubro;
    private Integer idFactCab;
    private BigDecimal stockFisico;
    private BigDecimal stockVirtual;


    public StockResponse(String comprobante, BigDecimal numero, Date fechaEmision, BigDecimal ingresos, BigDecimal egresos, BigDecimal pendiente, String deposito, Boolean trazable, String rubro, String subRubro, Integer idFactCab) {
        this.comprobante = comprobante;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.ingresos = ingresos;
        this.egresos = egresos;
        this.pendiente = pendiente;
        this.deposito = deposito;
        this.trazable = trazable;
        this.rubro = rubro;
        this.subRubro = subRubro;
        this.idFactCab = idFactCab;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public BigDecimal getNumero() {
        return numero;
    }

    public void setNumero(BigDecimal numero) {
        this.numero = numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
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

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
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

    public Integer getIdFactCab() {
        return idFactCab;
    }

    public void setIdFactCab(Integer idFactCab) {
        this.idFactCab = idFactCab;
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
