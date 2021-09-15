package datos;

import entidades.FormaPagoDet;
import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class FormaPagoDetResponse implements Payload{
    private Integer idFormaPagoDet;
    private int cantDias;
    private BigDecimal porcentaje;
    private String detalle;
    private String ctaContable;
    private ContPlanCuentaResponse planCuenta;

    public FormaPagoDetResponse(FormaPagoDet f) {
        this.idFormaPagoDet = f.getIdFormaPagoDet();
        this.cantDias = f.getCantDias();
        this.porcentaje = f.getPorcentaje();
        this.detalle = f.getDetalle();
        this.ctaContable = f.getCtaContable();
    }

    public Integer getIdFormaPagoDet() {
        return idFormaPagoDet;
    }

    public void setIdFormaPagoDet(Integer idFormaPagoDet) {
        this.idFormaPagoDet = idFormaPagoDet;
    }

    public int getCantDias() {
        return cantDias;
    }

    public void setCantDias(int cantDias) {
        this.cantDias = cantDias;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getCtaContable() {
        return ctaContable;
    }

    public void setCtaContable(String ctaContable) {
        this.ctaContable = ctaContable;
    }

    public ContPlanCuentaResponse getPlanCuenta() {
        return planCuenta;
    }

    public void setPlanCuenta(ContPlanCuentaResponse planCuenta) {
        this.planCuenta = planCuenta;
    }
    
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
