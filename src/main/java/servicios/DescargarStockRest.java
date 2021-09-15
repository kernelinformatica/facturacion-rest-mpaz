package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Usuario;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
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
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("descargarStock")
public class DescargarStockRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    
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
            Date fechaHasta = (Date)Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");
            Integer idProductoDesde = (Integer) Utils.getKeyFromJsonObject("idProductoDesde", jsonBody, "Integer");
            Integer idProductoHasta = (Integer) Utils.getKeyFromJsonObject("idProductoHasta", jsonBody, "Integer");
            Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", jsonBody, "Integer");
            Integer idDeposito = (Integer) Utils.getKeyFromJsonObject("idDeposito", jsonBody, "Integer");
            Integer idCteTipo = (Integer) Utils.getKeyFromJsonObject("idCteTipo", jsonBody, "Integer");
            Integer idRubro= (Integer) Utils.getKeyFromJsonObject("idRubro", jsonBody, "Integer");
            Integer idSubRubro = (Integer) Utils.getKeyFromJsonObject("idSubRubro", jsonBody, "Integer");
            Integer tipoEstado = (Integer) Utils.getKeyFromJsonObject("tipoEstado", jsonBody, "Integer");
            
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
            
            if(tipo.equals("producto")) {
                nombreReporte = "stockProducto";
            } else if(tipo.equals("general")) {
                nombreReporte = "stockGeneral";
            } else {
                respuesta.setControl(AppCodigo.ERROR, "Tipo de listado no encontrado");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }
            
            String tituloReporte = "Consulta de Stock";                         
            
            HashMap hm = new HashMap();
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            hm.put("empresa",user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());                        
            hm.put("fechaHasta", sqlFechaHasta);
            hm.put("producto", idProducto);
            hm.put("productoDesde", idProductoDesde);
            hm.put("productoHasta", idProductoHasta);
            hm.put("deposito", idDeposito);
            hm.put("cteTipo",idCteTipo);
            hm.put("estado", tipoEstado);
            hm.put("rubro", idRubro);
            hm.put("subRubro",idSubRubro);
            hm.put("titulo", tituloReporte);
            
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
