package datos;

import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class FacturaResponse implements Payload{
    private String cuentaContable;
    private String descripcion;  
    private String operador;
    private BigDecimal importeTotal;
    private BigDecimal porcentaje;
    private BigDecimal baseImponible;
    private Integer orden;
    private Integer idSisTipoModelo;
    private Integer idLibro;

//    public FacturaResponse(String cuentaContable, String descripcion, String tipoModelo, BigDecimal importeTotal, BigDecimal porcentaje) {
//        this.cuentaContable = cuentaContable;
//        this.descripcion = descripcion;
//        this.importeTotal = importeTotal;        
//    }
    
    public FacturaResponse(String cuentaContable, String descripcion, BigDecimal importeTotal, BigDecimal porcentaje, Integer orden, Integer idSisTipoModelo, BigDecimal baseImponible, String operador, Integer idLibro) {
        this.cuentaContable = cuentaContable;
        this.descripcion = descripcion;
        this.importeTotal = importeTotal;
        this.porcentaje = porcentaje;
        this.orden = orden;
        this.idSisTipoModelo = idSisTipoModelo;
        this.baseImponible = baseImponible;
        this.operador = operador;
        this.idLibro = idLibro;
    }

    public String getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(String cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }
    
    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getIdSisTipoModelo() {
        return idSisTipoModelo;
    }

    public void setIdSisTipoModelo(Integer idSisTipoModelo) {
        this.idSisTipoModelo = idSisTipoModelo;
    }    

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }
    
}
