package datos;

import entidades.SisCanje;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */
public class SisCanjeResponse implements Payload{
    private Integer idSisCanje;
    private String descripcion;
    private Long precio;
    private Date fechaVto;
    private Long interes;

    public SisCanjeResponse(SisCanje s) {
        this.idSisCanje = s.getIdSisCanje();
        this.descripcion = s.getDescripcion();
        this.precio = s.getPrecio();
        this.fechaVto = s.getFechaVto();
        this.interes = s.getInteres();
    }

    
    public Integer getIdSisCanje() {
        return idSisCanje;
    }

    public void setIdSisCanje(Integer idSisCanje) {
        this.idSisCanje = idSisCanje;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }

    public Date getFechaVto() {
        return fechaVto;
    }

    public void setFechaVto(Date fechaVto) {
        this.fechaVto = fechaVto;
    }

    public Long getInteres() {
        return interes;
    }

    public void setInteres(Long interes) {
        this.interes = interes;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
