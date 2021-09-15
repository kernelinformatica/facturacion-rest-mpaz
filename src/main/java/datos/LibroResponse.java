package datos;

import entidades.Libro;

/**
 *
 * @author FrancoSili
 */
public class LibroResponse implements Payload{
    private Integer idLibro;
    private String columnaNombre;

    public LibroResponse(Libro l) {
        this.idLibro = l.getIdLibros();
        this.columnaNombre = l.getColumnaNombre();
    }

    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public String getColumnaNombre() {
        return columnaNombre;
    }

    public void setColumnaNombre(String columnaNombre) {
        this.columnaNombre = columnaNombre;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
      
}
