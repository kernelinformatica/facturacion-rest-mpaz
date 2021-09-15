package datos;

import entidades.SisEstado;

/**
 *
 * @author FrancoSili
 */
public class SisEstadoResponse implements Payload{
    private Integer idSisEstados;
    private String descripcion;

    public SisEstadoResponse(SisEstado s) {
        this.idSisEstados = s.getIdSisEstados();
        this.descripcion = s.getDescripcion();
    }

    public Integer getIdSisEstados() {
        return idSisEstados;
    }

    public void setIdSisEstados(Integer idSisEstados) {
        this.idSisEstados = idSisEstados;
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
