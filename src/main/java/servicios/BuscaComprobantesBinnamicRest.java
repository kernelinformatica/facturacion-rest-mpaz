package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.FactCabResponse;
import datos.FactDetalleResponse;
import datos.MasterResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.FactCab;
import entidades.FactDetalle;
import entidades.Master;
import entidades.Usuario;
import entidades.Parametro;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.ParametroFacade;
import persistencia.AccesoFacade;
import persistencia.FactCabFacade;
import persistencia.FactDetalleFacade;
import persistencia.MasterFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Kernel Informatica
 */
@Stateless
@Path("buscaComprobantesBinnamics")
public class BuscaComprobantesBinnamicRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils; 
    @Inject FactCabFacade factCabFacade;
    @Inject FactDetalleFacade factDetalleFacade;
    @Inject MasterFacade masterFacade;
    @Inject ParametroFacade parametrosFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductos(
        @HeaderParam ("token") String token,
        @HeaderParam ("clave") String clave,
        //@QueryParam("imputados") String imputados,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // redefino la varibble del tocken  luego se la paso a cabvello para que la pse por header
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            // Obtengo los atributos del body
            Integer comprobanteModulo = (Integer) Utils.getKeyFromJsonObject("comprobanteModulo", jsonBody, "Integer");
            Integer comprobanteTipo = (Integer) Utils.getKeyFromJsonObject("comprobanteTipo", jsonBody, "Integer");
            BigDecimal comprobanteNumero = (BigDecimal) Utils.getKeyFromJsonObject("comprobanteNumero", jsonBody, "BigDecimal");
            Date fechaDesde = (Date) Utils.getKeyFromJsonObject("fechaDesde", jsonBody, "Date");
            Date fechaHasta = (Date) Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");
            Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", jsonBody, "Integer");
            Integer padCodigo = (Integer) Utils.getKeyFromJsonObject("padCodigo", jsonBody, "Integer");
            Integer idDeposito = (Integer) Utils.getKeyFromJsonObject("idDeposito", jsonBody, "Integer");
            Integer idEstado = (Integer) Utils.getKeyFromJsonObject("idEstado", jsonBody, "Integer");
            Integer idVendedor = (Integer) Utils.getKeyFromJsonObject("idVendedor", jsonBody, "Integer");
            Integer idSisTipoOperacion = (Integer) Utils.getKeyFromJsonObject("idSisTipoOperacion", jsonBody, "Integer");
            String autorizada = (String) Utils.getKeyFromJsonObject("autorizada", jsonBody, "String");
            Integer contratoRelacionado = (Integer) Utils.getKeyFromJsonObject("contratoRelacionado", jsonBody, "Integer");
            String codProductoDesde = (String) Utils.getKeyFromJsonObject("codProductoDesde", jsonBody, "String");
            String codProductoHasta = (String) Utils.getKeyFromJsonObject("codProductoHasta", jsonBody, "String");
            
            //valido que token no sea null
            if(token == null || token.trim().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Error, token vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
           
            // tipo comprobantes permitidos 69,73
            // tipo operaciones permitidas (1, 13) 
           if (comprobanteTipo ==  69 ) {
             System.out.println("::: Comprobante Permitido 69 Remito de Compra :::");
                
           }else if(comprobanteTipo == 73  ){
               System.out.println("::: Comprobante Permitido 73 Cacturas de Compra :::");
           }else{
                 respuesta.setControl(AppCodigo.ERROR, "Tipo de comprobante inválido, solo se admiten facturas de compra y remitos de compra");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            //Busco el token
            Acceso userToken = accesoFacade.findByToken(token);
            
            //valido que Acceso no sea null
            if(userToken == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Acceso nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
          
            
            //Busco el usuario
            Usuario user = usuarioFacade.getByToken(userToken);
            
            
            //valido que el Usuario no sea null
            if(user == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Usuario nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //valido vencimiento token
            if(!accesoFacade.validarToken(userToken, user)) {
                respuesta.setControl(AppCodigo.ERROR, "Credenciales incorrectas");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }
            
             // valido clave de ejecucion del metodo
            Parametro param = parametrosFacade.getParametro("claveBinnamics", userToken.getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());    
            if (!param.getValor().equals(clave)){
                 respuesta.setControl(AppCodigo.ERROR, "Credenciales incorrectas, no tiene autorización para ejecutar el metodo.");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }

            //Valido fechas
            if(fechaDesde.after(fechaHasta)) {
                respuesta.setControl(AppCodigo.ERROR, "Error, la fecha desde debe ser menor que la fecha hasta");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
                  
            //seteo el nombre del store cabecera
            String nombreSP = "call s_buscaComprobantesCabecera(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            //seteo el nombre del store detalle
            String nombreSPDetalle = "call s_buscaComprobantesDetalles(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            //invoco al store
            CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, nombreSP);
            CallableStatement callableStatementDetalle = this.utils.procedimientoAlmacenado(user, nombreSPDetalle);
            
            //valido que el Procedimiento Almacenado no sea null
            if(callableStatement == null || callableStatementDetalle == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(contratoRelacionado == null) {
                contratoRelacionado = 0;
            }
            
            //Seteo los parametros para la cabecera 
            callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatement.setInt(2,comprobanteModulo);
            callableStatement.setInt(3, comprobanteTipo);
            callableStatement.setLong(4,comprobanteNumero.longValue());
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            
            callableStatement.setDate(5, sqlFechaDesde);
            callableStatement.setDate(6, sqlFechaHasta);
            callableStatement.setInt(7, idProducto);
            callableStatement.setInt(8, padCodigo);
            callableStatement.setInt(9, idDeposito);
            callableStatement.setInt(10, idEstado);
            callableStatement.setInt(11, idVendedor);
            callableStatement.setInt(12, idSisTipoOperacion);
            callableStatement.setString(13, autorizada);
            callableStatement.setInt(14, contratoRelacionado);
            callableStatement.setString(15, codProductoDesde);
            callableStatement.setString(16, codProductoHasta);
            
            
            //Seteo los parametros para los detalle
            callableStatementDetalle.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatementDetalle.setInt(2,comprobanteModulo);
            callableStatementDetalle.setInt(3, comprobanteTipo);
            callableStatementDetalle.setLong(4,comprobanteNumero.longValue());
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesdeDetalle = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHastaDetalle = new java.sql.Date(fechaHasta.getTime());
            
            callableStatementDetalle.setDate(5, sqlFechaDesdeDetalle);
            callableStatementDetalle.setDate(6, sqlFechaHastaDetalle);
            callableStatementDetalle.setInt(7, idProducto);
            callableStatementDetalle.setInt(8, padCodigo);
            callableStatementDetalle.setInt(9, idDeposito);
            callableStatementDetalle.setInt(10, idEstado);
            callableStatementDetalle.setInt(11, idVendedor);
            callableStatementDetalle.setInt(12, idSisTipoOperacion);
            callableStatementDetalle.setString(13, codProductoDesde);
            callableStatementDetalle.setString(14, codProductoHasta);
            
            //Reccorro los resultados para la cabecera
            ResultSet rs = callableStatement.executeQuery();
            List<FactCabResponse> factCabResponses = new ArrayList<>();
            
            List<FactDetalleResponse> factDetResponses = new ArrayList<>();
            while (rs.next()) {
                FactCabResponse factCab = new FactCabResponse(
                        rs.getInt("idFactCab"),
                        rs.getString("comprobante"),
                        rs.getLong("numero"),
                        rs.getDate("fechaEmision"),
                        rs.getDate("fechaVence"),
                        rs.getInt("codigoPad"),
                        rs.getString("nombre"),
                        rs.getString("cuit"),
                        rs.getBigDecimal("dolar"),
                        rs.getString("moneda"),
                        rs.getString("imputada"),
                        rs.getString("modulo"),
                        rs.getString("vendedor"),
                        rs.getInt("idCteTipo"),
                        rs.getBigDecimal("importeNeto"),
                        rs.getBigDecimal("importeTotal"),
                        rs.getString("tipoOperacion"),
                        rs.getString("autorizada"),
                        rs.getString("permiteBorrado"),
                        rs.getInt("kilosCanje"),
                        rs.getBoolean("pesificado"),
                        rs.getBoolean("dolarizadoAlVto"),
                        rs.getBigDecimal("interesMensualCompra"),
                        rs.getBoolean("canjeInsumos"),
                        rs.getString("tipoCambio") );
                factCabResponses.add(factCab);
            }
            
            //Recorro los resultados para los detalles
            ResultSet rsd = callableStatementDetalle.executeQuery();
            while (rsd.next()) {
                FactDetalleResponse factDet = new FactDetalleResponse(
                        rsd.getString("comprobante"),
                        rsd.getLong("numero"),
                        rsd.getDate("fechaEmision"),
                        rsd.getString("codProducto"),
                        rsd.getString("articulo"),
                        rsd.getBigDecimal("original"),
                        rsd.getBigDecimal("pendiente"),
                        rsd.getBigDecimal("precio"),
                        rsd.getBigDecimal("dolar"),
                        rsd.getString("moneda"),
                        rsd.getBigDecimal("porCalc"),
                        rsd.getBigDecimal("ivaPorc"),
                        rsd.getInt("deposito"),
                        rsd.getBigDecimal("importe"),
                        rsd.getInt("idFactCab"),
                        rsd.getString("vendedor"),
                        rsd.getString("descuento"),
                        rsd.getBigDecimal("precioDesc"),
                        rsd.getString("unidadDescuento"));
                factDetResponses.add(factDet);
            }
            List<Payload> comprobantes = new ArrayList<>();
//            if(imputados.equals("imputados") && !factCabResponses.isEmpty()) {
//                for(FactCabResponse c : factCabResponses) {
//                     
//                }
//            } else {
                for(FactCabResponse c : factCabResponses) {
                    for(FactDetalleResponse d : factDetResponses) {
                        if(c.getIdFactCab()== d.getFactCab()) {
                            c.getDetalle().add(d);
                        }
                    }
                    
                    comprobantes.add(c);
                }
            //}
            callableStatement.getConnection().close();
            callableStatementDetalle.getConnection().close();
            respuesta.setArraydatos(comprobantes);
            respuesta.setControl(AppCodigo.OK, "Comprobantes");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
   
    
    
    
    
}
