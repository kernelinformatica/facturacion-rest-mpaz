/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import datos.AppCodigo;
import datos.CerealesResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Cereales;
import entidades.Usuario;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import persistencia.CerealesFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("prueba")
public class TesterRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils; 
    @Inject CerealesFacade cerealesFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTester(  
       @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {
           respuesta.setControl(AppCodigo.OK, "Ejecución exitosa");
          return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();

           
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
     public Response postTester(  
        @HeaderParam ("token") String token,
     
       @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {
           respuesta.setControl(AppCodigo.OK, "Ejecución exitosa POST --> "+token);
          return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();

           
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
