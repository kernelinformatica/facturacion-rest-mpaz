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
@Path("descargarLibroIva")
public class DescargarLibroIvaRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    @Inject MarcaFacade marcaFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPDF(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer modulo = (Integer) Utils.getKeyFromJsonObject("modulo", jsonBody, "Integer");          
            Date fechaDesde = (Date) Utils.getKeyFromJsonObject("fechaDesde", jsonBody, "Date");
            Date fechaHasta = (Date) Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");           
            
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

            
            
            
            String nombreReporte = "libro";
                   
            HashMap hm = new HashMap();
            hm.put("empresa",user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            hm.put("modulo",modulo);
            
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            
            hm.put("fechaDesde", sqlFechaDesde);
            hm.put("fechaHasta", sqlFechaHasta);
                      
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
