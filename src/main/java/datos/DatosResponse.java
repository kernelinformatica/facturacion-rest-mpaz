package datos;

/**
 *
 * @author Dario
 */

public class DatosResponse implements Payload {
  private Integer idFactCab;

    public DatosResponse(Integer idFactCab) {
        this.idFactCab = idFactCab;
    }
    
  
    public Integer getIdFactCab() {
        return idFactCab;
    }

    public void setIdFactCab(Integer idFactCab) {
        this.idFactCab = idFactCab;
    }

  
  
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
}
