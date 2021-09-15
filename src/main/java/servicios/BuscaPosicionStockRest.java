/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.Payload;
import datos.PosicionStockGralResponse;
import datos.PosicionStockResponse;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Producto;
import entidades.Usuario;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.ProductoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("posicion-stock")
public class BuscaPosicionStockRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    @Inject ProductoFacade productoFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPosicionesStockGral(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Date fechaDesde = (Date) Utils.getKeyFromJsonObject("fechaDesde", jsonBody, "Date");
            Date fechaHasta = (Date) Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");
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
            String nombreSP = "call s_buscaPosicionStockGral(?,?,?,?,?)";

            
            //invoco al store
            CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, nombreSP);
            
            //valido que el Procedimiento Almacenado no sea null
            if(callableStatement == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            
            //Seteo los parametros para la cabecera 
            callableStatement.setInt(1, user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatement.setDate(2, sqlFechaDesde);
            callableStatement.setDate(3, sqlFechaHasta);
            callableStatement.setString(4, codProductoDesde);
            callableStatement.setString(5, codProductoHasta);
            
            
            //Reccorro los resultados para la cabecera
            ResultSet rs = callableStatement.executeQuery();
            List<PosicionStockGralResponse> posicionStockResponse = new ArrayList<>();
            
            while (rs.next()) {
                PosicionStockGralResponse posicionStock = new PosicionStockGralResponse(
                        rs.getString("CodProducto"),
                        rs.getString("descripcion"),
                        rs.getBigDecimal("Anterior"),
                        rs.getBigDecimal("Facturas"),
                        rs.getBigDecimal("Remitos"));
                posicionStockResponse.add(posicionStock);
            }
            
            
            List<Payload> posicionesStock = new ArrayList<>();
//            if(imputados.equals("imputados") && !factCabResponses.isEmpty()) {
//                for(FactCabResponse c : factCabResponses) {
//                     
//                }
//            } else {
                for(PosicionStockGralResponse c : posicionStockResponse) {
                    posicionesStock.add(c);
                }
            //}
            callableStatement.getConnection().close();
            respuesta.setArraydatos(posicionesStock);
            respuesta.setControl(AppCodigo.OK, "Posiciones de Stock Generales");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Path("/particular")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPosicionesStock(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Date fechaDesde = (Date) Utils.getKeyFromJsonObject("fechaDesde", jsonBody, "Date");
            Date fechaHasta = (Date) Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");
            String codProducto = (String) Utils.getKeyFromJsonObject("codProducto", jsonBody, "String");
            
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
            
            Producto producto = productoFacade.getByCodigoProdEmpresa(codProducto, user.getIdPerfil().getIdSucursal().getIdEmpresa());
            
            if(producto == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el producto buscado no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
                  
            //seteo el nombre del store cabecera
            String nombreSP = "call s_buscaPosicionStock(?,?,?,?)";

            
            //invoco al store
            CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, nombreSP);
            
            //valido que el Procedimiento Almacenado no sea null
            if(callableStatement == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            
            //Seteo los parametros para la cabecera 
            callableStatement.setInt(1, user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatement.setDate(2, sqlFechaDesde);
            callableStatement.setDate(3, sqlFechaHasta);
            callableStatement.setInt(4, producto.getIdProductos());
            
            
            //Reccorro los resultados para la cabecera
            ResultSet rs = callableStatement.executeQuery();
            List<PosicionStockResponse> posicionStockResponse = new ArrayList<>();
            
            while (rs.next()) {
                PosicionStockResponse posicionStock = new PosicionStockResponse(
                        rs.getDate("fechaEmision"),
                        rs.getString("descripcion"),
                        rs.getLong("numero"),
                        rs.getBigDecimal("Factura"),
                        rs.getBigDecimal("Remito"),
                        rs.getString("Operacion"));
                posicionStockResponse.add(posicionStock);
            }
            
            
            List<Payload> posicionesStock = new ArrayList<>();
//            if(imputados.equals("imputados") && !factCabResponses.isEmpty()) {
//                for(FactCabResponse c : factCabResponses) {
//                     
//                }
//            } else {
                for(PosicionStockResponse c : posicionStockResponse) {
                    posicionesStock.add(c);
                }
            //}
            callableStatement.getConnection().close();
            respuesta.setArraydatos(posicionesStock);
            respuesta.setControl(AppCodigo.OK, "Posiciones de Stock de Producto");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Path("/particular/imprimir")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response imprimirPosicionesStock(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Date fechaDesde = (Date) Utils.getKeyFromJsonObject("fechaDesde", jsonBody, "Date");
            Date fechaHasta = (Date) Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");
            String codProducto = (String) Utils.getKeyFromJsonObject("codProducto", jsonBody, "String");
            
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
            
            Producto producto = productoFacade.getByCodigoProdEmpresa(codProducto, user.getIdPerfil().getIdSucursal().getIdEmpresa());
            
            if(producto == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el producto buscado no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            String nombreReporte = "reportePosStock";
            
            HashMap hm = new HashMap();
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            
            hm.put("empresa",user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            hm.put("fechaDesde", sqlFechaDesde);
            hm.put("fechaHasta", sqlFechaHasta);
            hm.put("idProducto", producto.getIdProductos());
            hm.put("nombreProducto", producto.getCodProducto() + " - " + producto.getDescripcion());
                  
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bytes = utils.generateJasperReportPDF(request, nombreReporte, hm, user, outputStream, 1, false, null, null);
            String nomeRelatorio = nombreReporte + ".pdf";
            return Response.ok(bytes).type("application/pdf").header("Content-Disposition", "filename=\"" + nomeRelatorio + "\"").build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
