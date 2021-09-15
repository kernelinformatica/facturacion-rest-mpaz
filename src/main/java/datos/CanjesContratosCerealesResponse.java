/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import entidades.CanjesContratosCereales;
import entidades.Cereales;
import entidades.Empresa;

/**
 *
 * @author administrador
 */
public class CanjesContratosCerealesResponse implements Payload {
    private Integer idCanjeContratoCereal;
    private CerealesResponse cerealCodigo;
    private String ctaContable;
    private Boolean visible;
    
    public CanjesContratosCerealesResponse(CanjesContratosCereales ccc) {
        this.idCanjeContratoCereal = ccc.getIdCanjeContratoCereal();
        this.ctaContable = ccc.getCtaContable();
        this.visible = ccc.getVisible();
        this.cerealCodigo = new CerealesResponse(ccc.getCerealCodigo());
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
