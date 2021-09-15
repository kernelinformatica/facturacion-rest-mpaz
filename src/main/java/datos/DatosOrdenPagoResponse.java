package datos;

/**
 *
 * @author Dario
 */

public class DatosOrdenPagoResponse implements Payload {
  private Integer idOpCab;

    public DatosOrdenPagoResponse(Integer idOpCab) {
        this.idOpCab = idOpCab;
    }
    
  
    public Integer getIdOrdenPagoCab() {
        return idOpCab;
    }

    public void setIdOrdenPagoCab(Integer idOpCab) {
        this.idOpCab = idOpCab;
    }

  
  
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
}
