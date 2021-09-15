package datos;

import entidades.SisModulo;

/**
 *
 * @author FrancoSili
 */
public class SisModuloResponse implements Payload{
    private Integer idSisModulos;
    private String descripcion;

    public SisModuloResponse(SisModulo s) {
        this.idSisModulos = s.getIdSisModulos();
        this.descripcion = s.getDescripcion();
    }

    public Integer getIdSisModulos() {
        return idSisModulos;
    }

    public void setIdSisModulos(Integer idSisModulos) {
        this.idSisModulos = idSisModulos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
