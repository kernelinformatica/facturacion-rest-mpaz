/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.EstadoSisaResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CerealSisaSybase;
import entidades.SisaPorcentaje;
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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.CerealSisaSybaseFacade;
import persistencia.SisaPorcentajeFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("estado-sisa")
public class EstadoSisaRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    @Inject CerealSisaSybaseFacade cerealSisaFacade;
    @Inject SisaPorcentajeFacade sisaPorcentajeFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCanjesContratosCereales(  
        @HeaderParam ("token") String token,
        @QueryParam ("idPadron") Integer idPadron,
        @Context HttpServletRequest request) {
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

            List<Payload> estadoSisa = new ArrayList<>();    
            CerealSisaSybase cerealSisa = cerealSisaFacade.getByCodPadron(idPadron);
            //Busco en la base Mysql el porcentaje de acuerdo al estado
            List<SisaPorcentaje> sisaPorcentajes = sisaPorcentajeFacade.getAll();
            
            for(SisaPorcentaje sisaPorcentaje : sisaPorcentajes) {
                EstadoSisaResponse estadoSisaResponse = new EstadoSisaResponse(sisaPorcentaje);
                if(sisaPorcentaje.getCodEstado() == Integer.parseInt(cerealSisa.getEstado().toString())) {
                    estadoSisaResponse.setIsCurrent(Boolean.TRUE);
                }
                estadoSisa.add(estadoSisaResponse);
            }
            respuesta.setArraydatos(estadoSisa);
            respuesta.setControl(AppCodigo.OK, "Lista de Estados Sisa");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al buscar los estados sisa: " + e.getMessage());
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
