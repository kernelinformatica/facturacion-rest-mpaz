/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import entidades.OrdenesPagosPCab;
/**
 *
 * @author mertw
 */
public class OrdenesPagoResponse implements Payload{
  
    private Integer idOPCab;
    private Integer idCteTipo;
    private long numero;
    private Date fechaEmision;
    private Date fechaAutorizacion;
    private String nombre;
    private String cuit;
    private BigDecimal IngresosBrutos;
    private BigDecimal TotalCompra;
    private BigDecimal TotDifCotizacion;
    private BigDecimal TotIvaDifCotizacion;
    private BigDecimal SubtOp;
    private List<OrdenPagoDetalleResponse> detalle;
    private List<OrdenPagoFormaPagoResponse> formaPago;
    private List<OrdenPagoPieResponse> pie;
    private List<OrdenPagoImputaResponse> imputa;
    private List<MasterResponse> master;
    
    
    //***********Create*********************
 
    public OrdenesPagoResponse( 
            Integer idOPCab,
            Integer idCteTipo,
            long numero,
            String nombre,
            String cuit,
            Date fechaEmision, 
            Date fechaAutorizacion, 
            BigDecimal IngresosBrutos ,
            BigDecimal TotalCompra, 
            BigDecimal TotDifCotizacion, 
            BigDecimal TotIvaDifCotizacion,
            BigDecimal SubtOp
            
           
            
           // List<OrdenPagoDetalleResponse> detalle
         ) {
        this.idOPCab = idOPCab;
        this.idCteTipo = idCteTipo;
        this.numero = numero;
        this.nombre=nombre;
        this.cuit=cuit;
        this.fechaEmision = fechaEmision;
        this.fechaAutorizacion = fechaAutorizacion;
        this.IngresosBrutos = IngresosBrutos;
        this.TotalCompra = TotalCompra;
        this.TotDifCotizacion = TotDifCotizacion;
        this.TotIvaDifCotizacion = TotIvaDifCotizacion;
        this.SubtOp = SubtOp;
        this.detalle = new ArrayList<>();
        this.formaPago = new ArrayList<>();
        this.pie =  new ArrayList<>();
        this.imputa = new ArrayList<>();
        this.master = new ArrayList<>();
       
        
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

    /*public OrdenePagoResponse(FactDetalle d) {
    this.idOPCab = idOPCab;
    this.numero = numero;
    this.fechaEmision = fechaEmision;
    this.fechaAutorizacion = fechaAutorizacion;
    this.TotalCompra = TotalCompra;
    this.TotDifCotizacion = TotDifCotizacion;
    this.TotIvaDifCotizacion = TotIvaDifCotizacion;
    this.SubtOp = SubtOp;
    }*/
    public void setPie(List<OrdenPagoPieResponse> pie) {
        this.pie = pie;
    }

    public Integer getIdCteTipo() {
        return idCteTipo;
    }

    public void setIdCteTipo(Integer idCteTipo) {
        this.idCteTipo = idCteTipo;
    }

    public Date getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(Date fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    
    //***********Propiedades********************
    public Integer getIdOPCab() {
        return idOPCab;
    }

    public void setIdOPCab(Integer idOPCab) {
        this.idOPCab = idOPCab;
    }
    //-------
    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }
    //-------
    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    //-------
    public Date getfechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setfechaAutorizacion(Date fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }
    //-------
    public BigDecimal getTotalCompra() {
        return TotalCompra;
    }

    public void setTotalCompra(BigDecimal TotalCompra) {
        this.TotalCompra = TotalCompra;
    }
    //-------
    public BigDecimal getTotDifCotizacion() {
        return TotDifCotizacion;
    }

    public void setTotDifCotizacion(BigDecimal TotDifCotizacion) {
        this.TotDifCotizacion = TotDifCotizacion;
    }
    //-------
    public BigDecimal getTotIvaDifCotizacion() {
        return TotIvaDifCotizacion;
    }

    public void setTotIvaDifCotizacion(BigDecimal TotIvaDifCotizacion) {
        this.TotIvaDifCotizacion = TotIvaDifCotizacion;
    }
    //-------
    public BigDecimal getSubtOp() {
        return this.SubtOp;
    }

    public void setSubtOp(BigDecimal SubtOp) {
        this.SubtOp = SubtOp;
    }
    //-------
    public BigDecimal getIngresosBrutos() {
        return this.IngresosBrutos;
    }

    public void setIngresosBrutos(BigDecimal IngresosBrutos) {
        this.IngresosBrutos = IngresosBrutos;
    }
    //-------
    public String getNombre() {
        return nombre;
    }

    public void String(String nombre) {
        this.nombre = nombre;
    }
    //-------
    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public List<OrdenPagoDetalleResponse> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<OrdenPagoDetalleResponse> detalle) {
        this.detalle = detalle;
    }

    public List<OrdenPagoImputaResponse> getImputa() {
        return imputa;
    }

    
    public void agregarMaster(List<MasterResponse> f) {
        for(MasterResponse fd: f) {
            this.master.add(fd);
        }
    }

   
  
    public List<MasterResponse> getMaster() {
        return master;
    }

    public void setMaster(List<MasterResponse> master) {
        this.master = master;
    }
    
    //-------
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
