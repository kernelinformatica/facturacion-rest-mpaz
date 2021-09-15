/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.ParametrosPesificado;
import entidades.Usuario;
import java.math.BigDecimal;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.EmpresaFacade;
import persistencia.ParametrosPesificadoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("parametros-pesificado")
public class ParametrosPesificadoRest {
    @Inject AccesoFacade accesoFacade;
    @Inject UsuarioFacade usuarioFacade;
    @Inject EmpresaFacade empresaFacade;
    @Inject ParametrosPesificadoFacade parametrosPesificadoFacade;
    
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParametroPesificado(
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            
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
            
            ParametrosPesificado params = parametrosPesificadoFacade.getParametro();

            respuesta.setControl(AppCodigo.OK, params.getInteresMensual().toString());
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Error al ejecutar la búsqueda de parámetros de pesificado: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, "Error al ejecutar la búsqueda de parámetros de pesificado");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarParametroPesificado(
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            BigDecimal interesMensual = (BigDecimal) Utils.getKeyFromJsonObject("interesMensual", jsonBody, "BigDecimal");
            
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
            
            if(interesMensual == null) {
                respuesta.setControl(AppCodigo.ERROR, "Interés mensual inválido");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            ParametrosPesificado params = parametrosPesificadoFacade.getParametro();
            params.setInteresMensual(interesMensual);
            Boolean transaction = parametrosPesificadoFacade.updateParametro(params);
            if(transaction) {
                respuesta.setControl(AppCodigo.OK, "Interés mensual actualizado");
                return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
            } else {
                respuesta.setControl(AppCodigo.ERROR, "Error al intentar actualizar el interés mensual");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Error al ejecutar la actualización de parámetros de pesificado: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, "Error al ejecutar la actualización de parámetros de pesificado");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
