package datos;

import entidades.PtoVenta;
import entidades.UsuarioListaPrecio;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Franco Sili
 */
public class UsuarioResponse implements Payload{
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private PerfilResponse perfil;
    private String usuario;
    private String clave;
    private String observaciones;
    private String usuarioSybase;
    private List<PtoVentaResponse> ptoVentas;
    private List<ListaPreciosResponse> listaPrecios;
    
    
    

    public UsuarioResponse(entidades.Usuario u) {
        this.id = u.getIdUsuarios();
        this.email = u.getMail();
        this.nombre = u.getNombre();
        this.apellido = u.getApellido();
        this.perfil = new PerfilResponse(u.getIdPerfil());
        this.telefono = u.getTelefono();
        this.usuario = u.getUsuario();
        this.clave = u.getClave();
        this.observaciones = u.getObservaciones();
        this.usuarioSybase = u.getUsuarioSybase();
        this.listaPrecios = new ArrayList<>();
        this.ptoVentas = new ArrayList<>();
        
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public PerfilResponse getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilResponse perfil) {
        this.perfil = perfil;
    }
    
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public List<ListaPreciosResponse> getListaPrecios() {
        return listaPrecios;
    }

    public void setListaPrecios(List<ListaPreciosResponse> listaPrecios) {
        this.listaPrecios = listaPrecios;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public String getUsuarioSybase() {
        return usuarioSybase;
    }

    public void setUsuarioSybase(String usuarioSybase) {
        this.usuarioSybase = usuarioSybase;
    }
    public void agregarListaPrecios(Collection<UsuarioListaPrecio> lista) {
        for(UsuarioListaPrecio l : lista) {
            ListaPreciosResponse lr  = new ListaPreciosResponse(l.getIdListaPrecios());
            this.listaPrecios.add(lr);
        }
    }

    public List<PtoVentaResponse> getPtoVentas() {
        return ptoVentas;
    }

    public void setPtoVentas(List<PtoVentaResponse> ptoVentas) {
        this.ptoVentas = ptoVentas;
    }

    public void agregarPtoVentas(Collection<PtoVenta> ptoVentas) {
        for(PtoVenta p : ptoVentas) {
            PtoVentaResponse r = new PtoVentaResponse(p);
            this.getPtoVentas().add(r);
        }
    }
    
     @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}
