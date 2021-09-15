package datos;

import entidades.SisCategoria;

/**
 *
 * @author FrancoSili
*/

public class SisCategoriaResponse implements Payload{
    private Integer idSisCategoria;
    private String descripcion;

    public SisCategoriaResponse(SisCategoria c) {
        this.idSisCategoria = c.getIdSisCategoria();
        this.descripcion = c.getDescripcion();
    }
   
    public Integer getIdSisCategoria() {
        return idSisCategoria;
    }

    public void setIdSisCategoria(Integer idSisCategoria) {
        this.idSisCategoria = idSisCategoria;
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
