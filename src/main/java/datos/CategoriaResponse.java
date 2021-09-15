package datos;

import entidades.Categoria;

/**
 *
 * @author FrancoSili
 */

public class CategoriaResponse implements Payload{
    private Integer idCategoria;
    private String descripcion;
    private Integer codigo;
    private SisCategoriaResponse sisCategoria;

    public CategoriaResponse(Categoria c) {
        this.idCategoria = c.getIdCategoria();
        this.descripcion = c.getDescripcion();
        this.codigo = c.getCodigo();
        this.sisCategoria = new SisCategoriaResponse(c.getIdSisCategoria());
    }   
    
    public Integer getIdCategoria() {
        return idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public SisCategoriaResponse getSisCategoria() {
        return sisCategoria;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }
    
    

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }  
    
}
