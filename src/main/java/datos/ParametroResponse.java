package datos;

import entidades.Parametro;

/**
 *
 * @author FrancoSili
 */
public class ParametroResponse  implements Payload{
    private String descripcion;
    private String valor;
    private String tipoValor;

    public ParametroResponse(Parametro p) {
        this.descripcion = p.getDescripcion();
        this.valor = p.getValor();
        this.tipoValor = p.getTipoValor();
    }

        
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(String tipoValor) {
        this.tipoValor = tipoValor;
    }
    
    

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
