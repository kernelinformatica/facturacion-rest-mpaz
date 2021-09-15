package datos;

import entidades.ModeloCab;
import entidades.ModeloDetalle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author FrancoSili
 */
public class ModeloCabResponse implements Payload {
    private Integer idModeloCab;
    private EmpresaResponse empresa; 
    private String descripcion;
    private List<ModeloDetalleResponse> modeloDetalle;

    public ModeloCabResponse(ModeloCab s) {
        this.idModeloCab = s.getIdModeloCab();
        this.empresa = new EmpresaResponse(s.getIdEmpresa());
        this.descripcion = s.getDescripcion();
        this.modeloDetalle = new ArrayList<>();
    }

    public Integer getIdModeloCab() {
        return idModeloCab;
    }

    public void setIdModeloCab(Integer idModeloCab) {
        this.idModeloCab = idModeloCab;
    }

    public EmpresaResponse getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaResponse empresa) {
        this.empresa = empresa;
    }
    
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<ModeloDetalleResponse> getModeloDetalle() {
        return modeloDetalle;
    }

    public void setModeloDetalle(List<ModeloDetalleResponse> modeloDetalle) {
        this.modeloDetalle = modeloDetalle;
    }

    
    public void agregarModeloDetalle(Collection<ModeloDetalle> lista) {
        for(ModeloDetalle l : lista) {
            ModeloDetalleResponse lr = new ModeloDetalleResponse(l);
            this.modeloDetalle.add(lr);
        }
    }
        
    public void agregarModeloDetalleImputacion(Collection<ModeloDetalle> lista, String cuentaContable) {
        for(ModeloDetalle l : lista) {            
            ModeloDetalleResponse lr = new ModeloDetalleResponse(l);
            if(l.getCtaContable().equals(cuentaContable)){
                lr.setPrioritario(true);
            } else {
                lr.setPrioritario(false);
            }
            this.modeloDetalle.add(lr);
        }
    }
    
    public void agregarModeloDetalleTipo(Collection<ModeloDetalle> lista, Integer idSisTipoModelo, Integer idModulo) {
        for(ModeloDetalle l : lista) {
            if(l.getIdSisTipoModelo().getIdSisTipoModelo().equals(idSisTipoModelo) && l.getIdSisModulo().getIdSisModulos().equals(idModulo)) {
                ModeloDetalleResponse lr = new ModeloDetalleResponse(l);
                this.modeloDetalle.add(lr);
            } else {
                continue;
            }
        }
    }   

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
