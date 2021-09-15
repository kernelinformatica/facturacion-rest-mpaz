/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;


/**
 *
 * @author Usuario
 */
public class TiposOpDepositosResponse implements Payload {
    private Integer idTipoOpDeposito;
    private Integer idSisTipoOperacion;
    private Integer idDepositos;
    
    public TiposOpDepositosResponse (Integer idTipoOpDeposito, Integer idSisTipoOperacion, Integer idDepositos){
      this.idTipoOpDeposito = idTipoOpDeposito;
      this.idSisTipoOperacion = idSisTipoOperacion;
      this.idDepositos = idDepositos;
    }
    
    public Integer getIdTipoOpDeposito() {
        return idTipoOpDeposito;
    }

    public void setIdTipoOpDeposito(Integer idTipoOpDeposito) {
        this.idTipoOpDeposito = idTipoOpDeposito;
    }
    
    public Integer getIdSisTipoOperacion() {
        return idSisTipoOperacion;
    }

    public void setIdSisTipoOperacion(Integer idSisTipoOperacion) {
        this.idSisTipoOperacion = idSisTipoOperacion;
    }
    
    public Integer getIdDepositos() {
        return idDepositos;
    }

    public void setIdDepositos(Integer idDepositos) {
        this.idDepositos = idDepositos;
    }
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
