/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

/**
 *
 * @author usuario
 */
public class ProximoCodigoResponse implements Payload {
    String proximoCodigo;

    public ProximoCodigoResponse(String proximoCodigo) {
        this.proximoCodigo = proximoCodigo;
    }
    
    public String getProximoCodigo() {
        return proximoCodigo;
    }

    public void setProximoCodigo(String proximoCodigo) {
        this.proximoCodigo = proximoCodigo;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
