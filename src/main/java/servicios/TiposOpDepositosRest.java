/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import datos.AppCodigo;
import entidades.Acceso;
import datos.Payload;
import datos.ServicioResponse;
import datos.TiposOpDepositosResponse;
import entidades.TiposOpDepositos;
import entidades.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.TiposOpDepositosFacade;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import persistencia.AccesoFacade;
import persistencia.UsuarioFacade;


/**
 *
 * @author Usuario
 */
@Stateless
@Path("tiposop")
public class TiposOpDepositosRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject TiposOpDepositosFacade tiposOpDepositosFacade;
    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public Response getTiposOpDepositos(@HeaderParam ("token") String token,  
        @Context HttpServletRequest request) {
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
            System.out.println("aca llegue al menos");
            List<TiposOpDepositos> depositosList = tiposOpDepositosFacade.getTiposOpDepositos();
            List<Payload> depositos = new ArrayList<>();
            for(TiposOpDepositos dep : depositosList) {
                TiposOpDepositosResponse tempObj = new TiposOpDepositosResponse(dep.getIdTipoOpDeposito(), dep.getIdSisTipoOperacion().getIdSisTipoOperacion(), dep.getIdDepositos().getIdDepositos());
                depositos.add(tempObj);
            }
            respuesta.setArraydatos(depositos);
            respuesta.setControl(AppCodigo.OK, "Lista de Depositos");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
