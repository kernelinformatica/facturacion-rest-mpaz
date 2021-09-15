package datos;

import entidades.CondIva;

/**
 *
 * @author FrancoSili
 */
public class CondIvaResponse {
    private Short condiva;
    private String descripcion;
    private Short codAfip;
    private String descCorta;
    
    public CondIvaResponse(CondIva c) {
        this.condiva = c.getCondiva();
        this.descripcion = c.getDescripcion();
        this.codAfip = c.getCodAfip();
        this.descCorta = c.getDescCorta();
    }

    public Short getCondiva() {
        return condiva;
    }

    public void setCondiva(Short condiva) {
        this.condiva = condiva;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Short getCodAfip() {
        return codAfip;
    }

    public void setCodAfip(Short codAfip) {
        this.codAfip = codAfip;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }
}
