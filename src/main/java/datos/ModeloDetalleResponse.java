package datos;

import entidades.ModeloDetalle;
import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class ModeloDetalleResponse implements Payload {
    private Integer idModeloDetalle;   
    private String ctaContable;   
    private Integer orden;
    private String descripcion;
    private String dh;
    private Boolean prioritario;
    private BigDecimal totalModelo;
    private BigDecimal valor;
    private String operador;
    private BigDecimal baseImponible;
    private SisTipoModeloResponse tipoModelo;
    private ContPlanCuentaResponse planCuenta;
    private SisModuloResponse sisModulo;
    private LibroResponse libro;

    public ModeloDetalleResponse(ModeloDetalle s) {
        this.idModeloDetalle = s.getIdModeloDetalle();
        this.ctaContable = s.getCtaContable();
        this.orden = s.getOrden();
        this.descripcion = s.getDescripcion();
        this.dh = s.getDh();
        this.prioritario = s.getPrioritario();
        this.valor = s.getValor();
        this.operador = s.getOperador();
        this.tipoModelo = new SisTipoModeloResponse(s.getIdSisTipoModelo());
        this.sisModulo = new SisModuloResponse(s.getIdSisModulo());
        this.libro = new LibroResponse(s.getIdLibro());
    }
    
    public ModeloDetalleResponse(ModeloDetalle s, BigDecimal total, BigDecimal porcentaje, BigDecimal baseImponible) {
        this.idModeloDetalle = s.getIdModeloDetalle();
        this.ctaContable = s.getCtaContable();
        this.orden = s.getOrden();
        this.operador = s.getOperador();
        this.descripcion = s.getDescripcion();
        this.dh = s.getDh();
        this.prioritario = s.getPrioritario();
        this.totalModelo = total;
        this.valor = porcentaje;
        this.baseImponible = baseImponible; 
        this.sisModulo = new SisModuloResponse(s.getIdSisModulo());
        this.tipoModelo = new SisTipoModeloResponse(s.getIdSisTipoModelo()); 
        this.libro = new LibroResponse(s.getIdLibro());
    }
    
//    public ModeloDetalleResponse(ModeloDetalle s, ContPlanCuentaResponse planCuenta) {
//        this.idModeloDetalle = s.getIdModeloDetalle();
//        this.ctaContable = s.getCtaContable();
//        this.orden = s.getOrden();
//        this.descripcion = s.getDescripcion();
//        this.dh = s.getDh();
//        this.prioritario = s.getPrioritario();
//        this.valor = s.getValor();
//        this.operador = s.getOperador();
//        this.tipoModelo = new SisTipoModeloResponse(s.getIdSisTipoModelo());
//        this.planCuenta = planCuenta;
//    }
    
    public Integer getIdModeloDetalle() {
        return idModeloDetalle;
    }

    public void setIdModeloDetalle(Integer idModeloDetalle) {
        this.idModeloDetalle = idModeloDetalle;
    }

    public String getCtaContable() {
        return ctaContable;
    }

    public void setCtaContable(String ctaContable) {
        this.ctaContable = ctaContable;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public Boolean getPrioritario() {
        return prioritario;
    }

    public void setPrioritario(Boolean prioritario) {
        this.prioritario = prioritario;
    }
    
    public BigDecimal getTotalModelo() {
        return totalModelo;
    }

    public void setTotalModelo(BigDecimal totalModelo) {
        this.totalModelo = totalModelo;
    }
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public SisTipoModeloResponse getTipoModelo() {
        return tipoModelo;
    }

    public void setTipoModelo(SisTipoModeloResponse tipoModelo) {
        this.tipoModelo = tipoModelo;
    }

    public ContPlanCuentaResponse getPlanCuenta() {
        return planCuenta;
    }

    public void setPlanCuenta(ContPlanCuentaResponse planCuenta) {
        this.planCuenta = planCuenta;
    }

    public SisModuloResponse getSisModulo() {
        return sisModulo;
    }

    public void setSisModulo(SisModuloResponse sisModulo) {
        this.sisModulo = sisModulo;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public LibroResponse getLibro() {
        return libro;
    }

    public void setLibro(LibroResponse libro) {
        this.libro = libro;
    }       
}
