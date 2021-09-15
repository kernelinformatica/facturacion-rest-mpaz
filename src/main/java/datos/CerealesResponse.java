/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import entidades.Cereales;

/**
 *
 * @author administrador
 */
public class CerealesResponse implements Payload {

    private String cerealCodigo;
    private String nombre;
    
    public CerealesResponse(Cereales ccc) {
        this.cerealCodigo = ccc.getCerealCodigo();
        this.nombre = ccc.getNombre();
    }
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
