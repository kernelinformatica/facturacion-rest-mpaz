package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.Payload;
import datos.ServicioResponse;
import datos.SisCotDolarResponse;
import entidades.Acceso;
import entidades.Marca;
import entidades.SisCotDolar;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.SisCotDolarFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Dario
 */
@Stateless
@Path("sisCotDolar")
public class SisCotDolarRest {
    @Inject AccesoFacade accesoFacade;
    @Inject UsuarioFacade usuarioFacade;
    @Inject SisCotDolarFacade sisCotDolarFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSitCotDolar(  
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
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
            
            //busco las sitCotDolar de la empresa del usuario
            List<Payload> sitsCotDolar = new ArrayList<>();
            
            for(SisCotDolar p : user.getIdPerfil().getIdSucursal().getIdEmpresa().getSisCotDolarCollection()){
                SisCotDolarResponse pr = new SisCotDolarResponse(p);
                sitsCotDolar.add(pr);
            }
            
            respuesta.setArraydatos(sitsCotDolar);
            
            respuesta.setControl(AppCodigo.OK, "Lista de SisCotDolar");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSitCotDolar(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);

            // Obtengo los atributos del body
            BigDecimal cotizacion = (BigDecimal) Utils.getKeyFromJsonObject("cotizacion", jsonBody, "BigDecimal");
            Date fechaCoti = (Date) Utils.getKeyFromJsonObject("fechaCoti", jsonBody, "Date");

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

            //
            if(cotizacion == null || fechaCoti == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo del body vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            boolean transaccion;
            
            SisCotDolar sisCotDolar = new SisCotDolar();
            sisCotDolar.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            sisCotDolar.setEspecie("dolar");
            sisCotDolar.setCotizacion(cotizacion);
            sisCotDolar.setFechaCotizacion(fechaCoti);

            transaccion = sisCotDolarFacade.setSisCotDolar(sisCotDolar);
            
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la cot dolar");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.CREADO, "Cot dolar creada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editSisCotDolar(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idSisCotDolar = (Integer) Utils.getKeyFromJsonObject("idSisCotDolar", jsonBody, "Integer");
            BigDecimal cotizacion = (BigDecimal) Utils.getKeyFromJsonObject("cotizacion", jsonBody, "BigDecimal");
            Date fechaCoti = (Date) Utils.getKeyFromJsonObject("fechaCoti", jsonBody, "Date");
            
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

            //
            if(cotizacion == null || fechaCoti == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo del body vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisCotDolar sisCotDolar = sisCotDolarFacade.find(idSisCotDolar);
            
            
            if(sisCotDolar == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la cotización dolar");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;     
            sisCotDolar.setEspecie("dolar");
            sisCotDolar.setCotizacion(cotizacion);
            sisCotDolar.setFechaCotizacion(fechaCoti);
            
            transaccion = sisCotDolarFacade.editSisCotDolar(sisCotDolar);
            
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar la cotizacion dolar");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Cotizacion dolar editada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }   
    
    
    @DELETE
    @Path ("/{idSisCotDolar}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteRubro(  
        @HeaderParam ("token") String token,
        @PathParam ("idSisCotDolar") Integer idSisCotDolar,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            //valido que token y el id no sea null
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
            
            //
            if(idSisCotDolar == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar una cotizacion dolar");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            SisCotDolar sisCotDolar = sisCotDolarFacade.find(idSisCotDolar);
            
            //Pregunto si existe
            if(sisCotDolar == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la cotizacion dolar");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = sisCotDolarFacade.deleteSisCotDolar(sisCotDolar);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar la cotizacion dolar");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Cotizacion dolar borrada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }  
    
}
