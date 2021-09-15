package datos;

import entidades.FacComprobante;

/**
 *
 * @author FrancoSili
 */
public class ComprobanteGestAgro implements Payload {
    private String compDescri;
    private String compDescri2;
    private String compCurso;
    private Short compDgi;

    public ComprobanteGestAgro(FacComprobante f) {
        this.compDescri = f.getCompDescri();
        this.compDescri2 = f.getCompDescri2();
        this.compCurso = f.getCompCurso();
        this.compDgi = f.getCompDgi();
    }
    
    public String getCompDescri() {
        return compDescri;
    }

    public void setCompDescri(String compDescri) {
        this.compDescri = compDescri;
    }

    public String getCompDescri2() {
        return compDescri2;
    }

    public void setCompDescri2(String compDescri2) {
        this.compDescri2 = compDescri2;
    }

    public String getCompCurso() {
        return compCurso;
    }

    public void setCompCurso(String compCurso) {
        this.compCurso = compCurso;
    }

    public Short getCompDgi() {
        return compDgi;
    }

    public void setCompDgi(Short compDgi) {
        this.compDgi = compDgi;
    }

    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
