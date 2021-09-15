package datos;

import entidades.ContPlanCuenta;

/**
 *
 * @author FrancoSili
 */
public class ContPlanCuentaResponse implements Payload {
    private Integer planCuentas;
    private String planDescripcion;

    public ContPlanCuentaResponse(ContPlanCuenta p) {
        this.planCuentas = p.getPlanCuentas();
        this.planDescripcion = p.getPlanDescripcion();
    }

    public ContPlanCuentaResponse(Integer parseInt, String descripcion) {
        this.planCuentas = parseInt;
        this.planDescripcion = descripcion;
    }

    public Integer getPlanCuentas() {
        return planCuentas;
    }

    public void setPlanCuentas(Integer planCuentas) {
        this.planCuentas = planCuentas;
    }

    public String getPlanDescripcion() {
        return planDescripcion;
    }

    public void setPlanDescripcion(String planDescripcion) {
        this.planDescripcion = planDescripcion;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
