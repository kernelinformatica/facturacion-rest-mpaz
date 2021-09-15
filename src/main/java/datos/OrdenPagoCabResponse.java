package datos;
import entidades.SisMonedas;
import entidades.OrdenesPagosPCab;
import entidades.SisTipoOperacion;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *
 * @author DarioQuiroga
 */
public class OrdenPagoCabResponse implements Payload{
    private Integer idOPCab;
    private Integer idCteTipo;
    private long numero;
    private Date fechaEmision;
    private Integer idPadron;
    private String nombre;    
    private String cuit;       
    private String codigoPostal;
    private BigDecimal cotDolar;
    private Integer idSisOperacionComprobantes;
    private Integer idUsuario;
    private Integer idUsuarioAutorizante;
    private Date fechaAutorizacion;
    private Long numeroReciboCaja;
    private Boolean pagoCerrado;
    private List<SisTipoOperacionResponse> idSisTipoOperacion;
    private Integer moneda;
    private List<OrdenPagoDetalleResponse> detalle;
    private List<OrdenPagoFormaPagoResponse> formaPago;
    private List<OrdenPagoPieResponse> pie;
    
    public OrdenPagoCabResponse (OrdenesPagosPCab op) throws ParseException {
        this.idOPCab = op.getIdOPCab();
        this.idCteTipo = op.getIdCteTipo();
        this.numero = op.getNumero();
        this.fechaEmision = op.getFechaEmision();
        this.idPadron = op.getIdPadron();
        this.nombre = op.getNombre();
        this.cuit = op.getCuit();
        this.codigoPostal=op. getCodigoPostal();
        this.cotDolar = op.getCotDolar();       
        this.idSisOperacionComprobantes = op.getIdSisOperacionComprobantes()==null?0:op.getIdSisOperacionComprobantes();;
        this.idUsuario = op.getIdUsuarios()==null?0:op.getIdUsuarios();
        this.idUsuarioAutorizante = op.getIdUsuariosAutorizante()==null?0:op.getIdUsuariosAutorizante();
        this.fechaAutorizacion = op.getFechaAutorizacion()==null?(new SimpleDateFormat("yyyy-MM-dd").parse("1900-01-01")):op.getFechaAutorizacion();
        this.numeroReciboCaja = op.getNumeroReciboCaja()==null?0:op.getNumeroReciboCaja();
        this.pagoCerrado = op.getPagoCerrado();
        //-----Esto hay que modificarlo---
        this.moneda=2;
         //--------------------------------
        this.detalle = new ArrayList<>();
        this.formaPago =new ArrayList<>();
        this.pie = new ArrayList<>();
        //---------------------------------
    }

  
    public OrdenPagoCabResponse(Integer idOPCab, Integer idCteTipo,long numero, Date fechaEmision, Integer idPadron, String nombre, String cuit, BigDecimal cotDolar, SisMonedas moneda, SisTipoOperacion idSisTipoOperacion, Integer idSisOperacionComprobantes, Integer idUsuario, Integer idUsuarioAutorizante, Integer numeroReciboCaja, Boolean pagoCerrado) {
        this.idOPCab  = idOPCab;
        this.idCteTipo = idCteTipo;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.idPadron = idPadron;
        this.nombre = nombre;
        this.cuit = cuit;
        this.cotDolar = cotDolar;
        
        this.idSisTipoOperacion =  new ArrayList<>(); 
         this.idUsuario = idUsuario;
         this.idUsuarioAutorizante = idUsuarioAutorizante;
         this.numeroReciboCaja = (long) numeroReciboCaja;
         this.pagoCerrado = pagoCerrado;
         this.detalle = new ArrayList<>();
         this.formaPago = new ArrayList<>();
         this.pie = new ArrayList<>();
         
       
    }

    public List<SisTipoOperacionResponse> getIdSisTipoOperacion() {
        return idSisTipoOperacion;
    }

    public void setIdSisTipoOperacion(List<SisTipoOperacionResponse> idSisTipoOperacion) {
        this.idSisTipoOperacion = idSisTipoOperacion;
    }
   
   //---------------------------
    public List<OrdenPagoDetalleResponse> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<OrdenPagoDetalleResponse> detalle) {
        this.detalle = detalle;
    }
   
    

   //----------------------------
    public Integer getIdOPCab() {
        return idOPCab;
    }

    public void setIdOPCab(Integer idOPCab) {
        this.idOPCab = idOPCab;
    }

    public Integer getIdCteTipo() {
        return idCteTipo;
    }

    public void setIdCteTipo(Integer idCteTipo) {
        this.idCteTipo = idCteTipo;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Integer getIdPadron() {
        return idPadron;
    }

    public void setIdPadron(Integer idPadron) {
        this.idPadron = idPadron;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public BigDecimal getCotDolar() {
        return cotDolar;
    }

    public void setCotDolar(BigDecimal cotDolar) {
        this.cotDolar = cotDolar;
    }

    public Integer getIdSisOperacionComprobantes() {
        return idSisOperacionComprobantes;
    }

    public void setIdSisOperacionComprobantes(Integer idSisOperacionComprobantes) {
        this.idSisOperacionComprobantes = idSisOperacionComprobantes;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdUsuarioAutorizante() {
        return idUsuarioAutorizante;
    }

    public void setIdUsuarioAutorizante(Integer idUsuarioAutorizante) {
        this.idUsuarioAutorizante = idUsuarioAutorizante;
    }

    public Date getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(Date fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public Long getNumeroReciboCaja() {
        return numeroReciboCaja;
    }

    public void setNumeroReciboCaja(Long numeroReciboCaja) {
        this.numeroReciboCaja = numeroReciboCaja;
    }

    public Boolean getPagoCerrado() {
        return pagoCerrado;
    }

    public void setPagoCerrado(Boolean pagoCerrado) {
        this.pagoCerrado = pagoCerrado;
    }

    public Integer getMoneda() {
        return moneda;
    }

    public void setMoneda(Integer moneda) {
        this.moneda = moneda;
    }

    public List<OrdenPagoFormaPagoResponse> getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(List<OrdenPagoFormaPagoResponse> formaPago) {
        this.formaPago = formaPago;
    }

    public List<OrdenPagoPieResponse> getPie() {
        return pie;
    }

    public void setPie(List<OrdenPagoPieResponse> pie) {
        this.pie = pie;
    }

    
    
    
    
    public void agregarDetalles(List<OrdenPagoDetalleResponse> f) {
        for(OrdenPagoDetalleResponse op: f) {
            this.detalle.add(op);
        }
    }
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}