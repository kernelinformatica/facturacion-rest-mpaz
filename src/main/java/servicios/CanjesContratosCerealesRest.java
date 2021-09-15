/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.CanjesContratosCerealesResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CanjesContratosCereales;
import entidades.Cereales;
import entidades.ParametrosCanjes;
import entidades.Usuario;
import entidades.Empresa;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import persistencia.CanjesContratosCerealesFacade;
import persistencia.CerealesFacade;
import persistencia.EmpresaFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("contratos-cereales")
public class CanjesContratosCerealesRest {
    
    @Inject AccesoFacade accesoFacade;
    @Inject UsuarioFacade usuarioFacade;
    @Inject CanjesContratosCerealesFacade canjesContratosCerealesFacade;
    @Inject EmpresaFacade empresaFacade;
    @Inject CerealesFacade cerealesFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCanjesContratosCereales(  
        @HeaderParam ("token") String token,
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
            
            List<Payload> cereales = new ArrayList<>();            
            Collection<CanjesContratosCereales> ccc = canjesContratosCerealesFacade.getAllCanjesContratosCereales();
            for(CanjesContratosCereales canjesContratosCereales : ccc) {
                CanjesContratosCerealesResponse cccResponse = new CanjesContratosCerealesResponse(canjesContratosCereales);
                cereales.add(cccResponse);
            }
            respuesta.setArraydatos(cereales);
            respuesta.setControl(AppCodigo.OK, "Lista de Categorias");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response abmParametrosCanje(
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) throws Exception {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer codigoFuncion = (Integer) Utils.getKeyFromJsonObject("codigoFuncion", jsonBody, "Integer");
            String ctaContable = (String) Utils.getKeyFromJsonObject("ctaContable", jsonBody, "String");
            String cerealCodigo = (String) Utils.getKeyFromJsonObject("cerealCodigo", jsonBody, "String");
            Integer idEmpresa = (Integer) Utils.getKeyFromJsonObject("idEmpresa", jsonBody, "Integer");
            Integer idCanjeContratoCereal = (Integer) Utils.getKeyFromJsonObject("idParametrosCanje", jsonBody, "Integer");
            
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
            
            if(codigoFuncion == null) {
                respuesta.setControl(AppCodigo.ERROR, "Código de función inválido");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(codigoFuncion == 1) {
                Empresa empresa = empresaFacade.getEmpresaById(idEmpresa);
                CanjesContratosCereales ccc = canjesContratosCerealesFacade.findCuentaPorCereal(empresa, cerealCodigo);
                if(ccc != null) {
                    respuesta.setControl(AppCodigo.ERROR, "Ya existe una cuenta contable asociada a ese cereal");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                CanjesContratosCereales canjesContratosCereales = new CanjesContratosCereales();
                Cereales cereal = cerealesFacade.findCerealPorCodigo(cerealCodigo);
                if(cereal == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el cereal");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                if(ctaContable == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Ingrese un número de cuenta contable válido");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                canjesContratosCereales.setCerealCodigo(cereal);
                canjesContratosCereales.setCtaContable(ctaContable);
                canjesContratosCereales.setIdEmpresa(empresa);
                canjesContratosCereales.setVisible(true);
                Boolean persistencia = canjesContratosCerealesFacade.addCanjesContratosCereales(canjesContratosCereales);
                if(persistencia) {
                    respuesta.setControl(AppCodigo.OK, "Relaciòn de cereal y cuenta contable creada");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "Error al crear la relación de cereal y cuenta contable");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            } else if(codigoFuncion == 2) {
                CanjesContratosCereales ccc = canjesContratosCerealesFacade.findById(idCanjeContratoCereal);
                if(ccc == null) {
                    respuesta.setControl(AppCodigo.ERROR, "La relación de cereal y cuenta contable no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                Boolean borrado = canjesContratosCerealesFacade.deleteCanjesContratosCereales(ccc);
                if(borrado) {
                    respuesta.setControl(AppCodigo.OK, "Relación de cereal y cuenta contable borrada");
                    return Response.status(Response.Status.ACCEPTED).entity(respuesta.toJson()).build();
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "Error al borrar la relación de cereal y cuenta contable");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            } else if(codigoFuncion == 3) {
                Empresa empresa = empresaFacade.getEmpresaById(idEmpresa);
                CanjesContratosCereales ccc = canjesContratosCerealesFacade.findById(idCanjeContratoCereal);
                if(ccc == null) {
                    respuesta.setControl(AppCodigo.ERROR, "La relación de cereal y cuenta contable no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                Cereales cereal = cerealesFacade.findCerealPorCodigo(cerealCodigo);
                if(cereal == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el cereal");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                CanjesContratosCereales existeRelacion = canjesContratosCerealesFacade.findCuentaPorCereal(empresa, cereal.getCerealCodigo());
                if(existeRelacion != null && existeRelacion.getIdCanjeContratoCereal() != ccc.getIdCanjeContratoCereal()) {
                    respuesta.setControl(AppCodigo.ERROR, "Ese cereal tiene una relaciòn preexistente");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                if(ctaContable == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Ingrese un número de cuenta contable válido");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                ccc.setCerealCodigo(cereal);
                ccc.setCtaContable(ctaContable);
                ccc.setVisible(true);
                Boolean modificado = canjesContratosCerealesFacade.modifyCanjesContratosCereales(ccc);
                if(modificado) {
                    respuesta.setControl(AppCodigo.OK, "La relación de cereal y cuenta contable fue modificada");
                    return Response.status(Response.Status.ACCEPTED).entity(respuesta.toJson()).build();
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "Error al modificar la relación de cereal y cuenta contable");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
            respuesta.setControl(AppCodigo.ERROR, "No existe una función con ese código");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Error al ejecutar el abm de relación de cereal y cuenta contable: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, "Error al ejecutar la función");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
