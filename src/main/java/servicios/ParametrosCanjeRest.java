/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ParametrosCanjeResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CanjesContratosCereales;
import entidades.Empresa;
import entidades.ParametrosCanjes;
import entidades.Usuario;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import persistencia.EmpresaFacade;
import persistencia.ParametrosCanjesFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("parametros-canje")
public class ParametrosCanjeRest {
    @Inject ParametrosCanjesFacade parametrosCanjesFacade;
    @Inject CanjesContratosCerealesFacade canjesContratosCerealesFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject UsuarioFacade usuarioFacade;
    @Inject EmpresaFacade empresaFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParametrosCanje(
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
            
            List<Payload> parametrosCanjeResponse = new ArrayList<>();
            
            List<ParametrosCanjes> parametrosCanjes = parametrosCanjesFacade.findAllParametrosCanjes();
            
            for(ParametrosCanjes pc : parametrosCanjes) {
                parametrosCanjeResponse.add(new ParametrosCanjeResponse(pc));
            }
            
            respuesta.setArraydatos(parametrosCanjeResponse);
            
            respuesta.setControl(AppCodigo.OK, "Lista de Parámetros Canje");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
            
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Error al buscar todos los parámetros de canje: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
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
            Integer idEmpresa = (Integer) Utils.getKeyFromJsonObject("idEmpresa", jsonBody, "Integer");
            BigDecimal interesDiario = (BigDecimal) Utils.getKeyFromJsonObject("interesDiario", jsonBody, "BigDecimal");
            Integer diasLibres = (Integer) Utils.getKeyFromJsonObject("diasLibres", jsonBody, "Integer");
            String ctaContableSisa = (String) Utils.getKeyFromJsonObject("ctaContableSisa", jsonBody, "String");
            String cerealCodigo = (String) Utils.getKeyFromJsonObject("cerealCodigo", jsonBody, "String");
            Integer idParametrosCanje = (Integer) Utils.getKeyFromJsonObject("idParametrosCanje", jsonBody, "Integer");
            
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
                ParametrosCanjes checkExistente = parametrosCanjesFacade.findParametrosCanjes(idEmpresa, cerealCodigo);
                if(checkExistente != null) {
                    respuesta.setControl(AppCodigo.ERROR, "Un parámetro de canje para ese cereal ya existía previamente");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                ParametrosCanjes parametroCanje = new ParametrosCanjes();
                if(ccc == null) {
                    respuesta.setControl(AppCodigo.ERROR, "El cereal elegido no tiene una cuenta contable relacionada");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                parametroCanje.setCanjeCereal(ccc);
                parametroCanje.setCtaContableSisa(ccc.getCtaContable());
                if(diasLibres == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Ingrese un numero de dias libres válido");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                parametroCanje.setDiasLIbres(diasLibres.shortValue());
                parametroCanje.setIdEmpresa(empresa);
                if(interesDiario == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Ingrese un numero de interés diario válido");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                parametroCanje.setInteresDiario(interesDiario);
                parametroCanje.setVisible(true);
                Boolean persistencia = parametrosCanjesFacade.addParametroCanje(parametroCanje);
                if(persistencia) {
                    respuesta.setControl(AppCodigo.OK, "Parámetro de canje creado");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "Error al crear el parámetro de canje");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            } else if(codigoFuncion == 2) {
                Empresa empresa = empresaFacade.getEmpresaById(idEmpresa);
                ParametrosCanjes parametroCanje = parametrosCanjesFacade.findParametrosCanjesById(idParametrosCanje, empresa.getIdEmpresa());
                if(parametroCanje == null) {
                    respuesta.setControl(AppCodigo.ERROR, "El parámetro de canje no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                Boolean borrado = parametrosCanjesFacade.deleteParametroCanje(parametroCanje);
                if(borrado) {
                    respuesta.setControl(AppCodigo.OK, "Parámetro de canje borrado");
                    return Response.status(Response.Status.ACCEPTED).entity(respuesta.toJson()).build();
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "Error al borrar el parámetro de canje");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            } else if(codigoFuncion == 3) {
                Empresa empresa = empresaFacade.getEmpresaById(idEmpresa);
                ParametrosCanjes parametroCanje = parametrosCanjesFacade.findParametrosCanjesById(idParametrosCanje, empresa.getIdEmpresa());
                if(parametroCanje == null) {
                    respuesta.setControl(AppCodigo.ERROR, "El parámetro de canje no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                CanjesContratosCereales ccc = canjesContratosCerealesFacade.findCuentaPorCereal(empresa, cerealCodigo);
                if(ccc == null) {
                    respuesta.setControl(AppCodigo.ERROR, "El cereal elegido no tiene una cuenta contable relacionada");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                if(diasLibres == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Ingrese un numero de dias libres válido");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                if(interesDiario == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Ingrese un numero de interés diario válido");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                parametroCanje.setCanjeCereal(ccc);
                parametroCanje.setCtaContableSisa(ccc.getCtaContable());
                parametroCanje.setDiasLIbres(diasLibres.shortValue());
                parametroCanje.setIdEmpresa(empresa);
                parametroCanje.setInteresDiario(interesDiario);
                parametroCanje.setVisible(true);
                Boolean modificado = parametrosCanjesFacade.modifyParametroCanje(parametroCanje);
                if(modificado) {
                    respuesta.setControl(AppCodigo.OK, "Parámetro de canje modificado");
                    return Response.status(Response.Status.ACCEPTED).entity(respuesta.toJson()).build();
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "Error al modificar el parámetro de canje");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
            respuesta.setControl(AppCodigo.ERROR, "No existe una función con ese código");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Error al ejecutar el abm de parámetros de canje: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, "Error al ejecutar la función");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
