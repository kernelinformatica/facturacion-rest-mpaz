package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.Payload;
import datos.RelacionesCanjeResponse;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.RelacionesCanje;
import entidades.Rubro;
import entidades.SisCanje;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.RelacionesCanjeFacade;
import persistencia.SisCanjeFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("relacionesCanje")
public class RelacionesCanjeRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject SisCanjeFacade sisCanjeFacade;
    @Inject RelacionesCanjeFacade relacionesCanjeFacade;
        
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRelacionesCanje(  
        @HeaderParam ("token") String token,
        @QueryParam ("idSisCanje") Integer idSisCanje,
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
            
            List<Payload> relaciones = new ArrayList<>();
            if(idSisCanje == null) {
                if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getRelacionesCanjeCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, la empresa no tiene dada de alta ninguna relacion");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                for(RelacionesCanje r : user.getIdPerfil().getIdSucursal().getIdEmpresa().getRelacionesCanjeCollection()){
                    RelacionesCanjeResponse rcr = new RelacionesCanjeResponse(r);
                    relaciones.add(rcr);
                }
            } else {
                SisCanje sisCanje = sisCanjeFacade.find(idSisCanje);
                if(sisCanje == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el canje seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getRelacionesCanjeCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, la empresa no tiene dada de alta ninguna relacion");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                for(RelacionesCanje r : sisCanje.getRelacionesCanjeCollection()) {
                    if(r.getIdEmpresa().getIdEmpresa().equals(user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa())) {
                       RelacionesCanjeResponse rcr = new RelacionesCanjeResponse(r);
                        relaciones.add(rcr);
                    }
                }
            }
            respuesta.setArraydatos(relaciones);
            respuesta.setControl(AppCodigo.OK, "Lista de Relaciones para Canje");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setRelacionesCanje(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer codigoCosecha = (Integer) Utils.getKeyFromJsonObject("codigoCosecha", jsonBody, "Integer");
            Integer codigoClase = (Integer) Utils.getKeyFromJsonObject("codigoClase", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            BigDecimal factor = (BigDecimal) Utils.getKeyFromJsonObject("factor", jsonBody, "BigDecimal");
            Integer idSisCanje = (Integer) Utils.getKeyFromJsonObject("idSisCanje", jsonBody, "Integer");

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

            //Me fijo que no sean nulos
            if(codigoCosecha == null || codigoClase == null || idSisCanje == null ) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo es nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisCanje sisCanje = sisCanjeFacade.find(idSisCanje);
            if(sisCanje == null ) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el canje seleccionado no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(factor == null) {
                factor =  BigDecimal.ZERO;
            }
            
            boolean transaccion;
            RelacionesCanje relacionesCanje = new RelacionesCanje();
            relacionesCanje.setDescripcion(descripcion);
            relacionesCanje.setCodigoClase(codigoClase);
            relacionesCanje.setCodigoCosecha(codigoCosecha);
            relacionesCanje.setFactor(factor);
            relacionesCanje.setIdSisCanje(sisCanje);
            relacionesCanje.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            transaccion = relacionesCanjeFacade.setRelacionesCanjeNuevo(relacionesCanje);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la relacion para el canje");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Relacion para canje creada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editRelacionesCanje(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idRelacionesCanje = (Integer) Utils.getKeyFromJsonObject("idRelacionesCanje", jsonBody, "Integer");
            Integer codigoCosecha = (Integer) Utils.getKeyFromJsonObject("codigoCosecha", jsonBody, "Integer");
            Integer codigoClase = (Integer) Utils.getKeyFromJsonObject("codigoClase", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            BigDecimal factor = (BigDecimal) Utils.getKeyFromJsonObject("factor", jsonBody, "BigDecimal");
            Integer idSisCanje = (Integer) Utils.getKeyFromJsonObject("idSisCanje", jsonBody, "Integer");

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

            //Me fijo que no sean nulos
            if(codigoCosecha == null || codigoClase == null || idSisCanje == null ) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo es nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisCanje sisCanje = sisCanjeFacade.find(idSisCanje);
            if(sisCanje == null ) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el canje seleccionado no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(factor == null) {
                factor =  BigDecimal.ZERO;
            }
            
            RelacionesCanje relacionesCanje = relacionesCanjeFacade.find(idRelacionesCanje);
            if(relacionesCanje == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, la relacion seleccionada no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            relacionesCanje.setDescripcion(descripcion);
            relacionesCanje.setCodigoClase(codigoClase);
            relacionesCanje.setCodigoCosecha(codigoCosecha);
            relacionesCanje.setFactor(factor);
            relacionesCanje.setIdSisCanje(sisCanje);
            relacionesCanje.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            transaccion = relacionesCanjeFacade.editRelacionesCanje(relacionesCanje);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar la relacion para el canje");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.GUARDADO, "Relacion para canje editada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @DELETE
    @Path ("/{idRelacionesCanje}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteRelacionesCanje(  
        @HeaderParam ("token") String token,
        @PathParam ("idRelacionesCanje") Integer idRelacionesCanje,
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
            
            //Me fijo que no sean nulos
            if(idRelacionesCanje == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el id no puede ser nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            RelacionesCanje relacionesCanje = relacionesCanjeFacade.find(idRelacionesCanje);
            if(relacionesCanje == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, la relacion seleccionada no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            boolean transaccion;
            transaccion = relacionesCanjeFacade.deleteRelacionesCanje(relacionesCanje);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar la Relacion para el canje");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.BORRADO, "Relacion borrada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
