/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.FactCabResponse;
import datos.FactDetalleResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Usuario;
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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.FactCabFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("buscaComprobantesAnticipados")
public class BuscaComprobanteAnticipadoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils; 
    @Inject FactCabFacade factCabFacade;
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductos(  
        @HeaderParam ("token") String token,
        @QueryParam("imputados") String imputados,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer comprobanteModulo = (Integer) Utils.getKeyFromJsonObject("comprobanteModulo", jsonBody, "Integer");
            Integer comprobanteTipo = (Integer) Utils.getKeyFromJsonObject("comprobanteTipo", jsonBody, "Integer");
            Integer comprobanteTipo2 = (Integer) Utils.getKeyFromJsonObject("comprobanteTipo2", jsonBody, "Integer");
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
            
            //Valido fechas
            if(fechaDesde.after(fechaHasta)) {
                respuesta.setControl(AppCodigo.ERROR, "Error, la fecha desde debe ser menor que la fecha hasta");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
                  
            //seteo el nombre del store cabecera
            String nombreSP = "call s_buscaComprobantesCabeceraAnticipado(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            //seteo el nombre del store detalle
            String nombreSPDetalle = "call s_buscaComprobantesDetallesAnticipado(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
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
            callableStatement.setInt(4, comprobanteTipo2);
            callableStatement.setLong(5,comprobanteNumero.longValue());
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            
            callableStatement.setDate(6, sqlFechaDesde);
            callableStatement.setDate(7, sqlFechaHasta);
            callableStatement.setInt(8, idProducto);
            callableStatement.setInt(9, padCodigo);
            callableStatement.setInt(10, idDeposito);
            callableStatement.setInt(11, idEstado);
            callableStatement.setInt(12, idVendedor);
            callableStatement.setInt(13, idSisTipoOperacion);
            callableStatement.setString(14, autorizada);
            callableStatement.setInt(15, contratoRelacionado);
            callableStatement.setString(16, codProductoDesde);
            callableStatement.setString(17, codProductoHasta);
            
            
            //Seteo los parametros para los detalle
            callableStatementDetalle.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatementDetalle.setInt(2,comprobanteModulo);
            callableStatementDetalle.setInt(3, comprobanteTipo);
            callableStatementDetalle.setInt(4, comprobanteTipo2);
            callableStatementDetalle.setLong(5,comprobanteNumero.longValue());
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesdeDetalle = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHastaDetalle = new java.sql.Date(fechaHasta.getTime());
            
            callableStatementDetalle.setDate(6, sqlFechaDesdeDetalle);
            callableStatementDetalle.setDate(7, sqlFechaHastaDetalle);
            callableStatementDetalle.setInt(8, idProducto);
            callableStatementDetalle.setInt(9, padCodigo);
            callableStatementDetalle.setInt(10, idDeposito);
            callableStatementDetalle.setInt(11, idEstado);
            callableStatementDetalle.setInt(12, idVendedor);
            callableStatementDetalle.setInt(13, idSisTipoOperacion);
            callableStatementDetalle.setString(14, codProductoDesde);
            callableStatementDetalle.setString(15, codProductoHasta);
            
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
                        rs.getString("tipoCambio"));
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
