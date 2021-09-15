package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Usuario;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
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
import persistencia.MarcaFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("descargarListado")
public class DescargarListadoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    @Inject MarcaFacade marcaFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPDF(  
        @HeaderParam ("token") String token,
        @QueryParam("tipo") String tipo,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
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
            String productoDesde = (String) Utils.getKeyFromJsonObject("productoDesde", jsonBody, "String");
            String productoHasta = (String) Utils.getKeyFromJsonObject("productoHasta", jsonBody, "String");

            
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
            if(tipo == null) {
                respuesta.setControl(AppCodigo.ERROR, "Tipo de listado vacio");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }
            
            String nombreReporte = "";
            
            if(tipo.equals("cabecera")) {
                nombreReporte = "listadoCabecera";
            } else if(tipo.equals("detalle")) {
                nombreReporte = "listadoDetalles";
            } else {
                respuesta.setControl(AppCodigo.ERROR, "Tipo de listado no encontrado");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }
            
            String tituloReporte = "Consulta de Comprobantes";
                   
            HashMap hm = new HashMap();
            hm.put("titulo", tituloReporte);
            hm.put("empresa",user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            hm.put("modulo",comprobanteModulo);
            hm.put("tipo", comprobanteTipo);
            hm.put("numero",comprobanteNumero.longValue());
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            
            hm.put("fechaDesde", sqlFechaDesde);
            hm.put("fechaHasta", sqlFechaHasta);
            hm.put("producto", idProducto);
            hm.put("padCodigo", padCodigo);
            hm.put("deposito", idDeposito);
            hm.put("estado", idEstado);
            hm.put("idVendedor", idVendedor);
            hm.put("idSisTipoOperacion", idSisTipoOperacion);
            hm.put("autorizada", autorizada);
            hm.put("productoDesde", productoDesde);
            hm.put("productoHasta", productoHasta);
            
            
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
