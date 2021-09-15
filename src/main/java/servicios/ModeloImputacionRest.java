package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ContPlanCuentaResponse;
import datos.ModeloCabResponse;
import datos.ModeloDetalleResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.ContPlanCuenta;
import entidades.Libro;
import entidades.ModeloCab;
import entidades.ModeloDetalle;
import entidades.SisModulo;
import entidades.SisTipoModelo;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.ContPlanCuentaFacade;
import persistencia.LibroFacade;
import persistencia.ModeloCabFacade;
import persistencia.ModeloDetalleFacade;
import persistencia.SisModuloFacade;
import persistencia.SisTipoModeloFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */
@Stateless
@Path("modeloImputacion")
public class ModeloImputacionRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject ModeloDetalleFacade modeloDetalleFacade;
    @Inject SisTipoModeloFacade sisTipoModeloFacade;
    @Inject ModeloCabFacade modeloCabFacade; 
    @Inject ContPlanCuentaFacade  contPlanCuentaFacade;
    @Inject SisModuloFacade sisModuloFacade;
    @Inject LibroFacade libroFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModeloImputacion(  
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
            
            //Valido que haya Modelos de imputacion esa empresa.
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getModeloCabCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay Modelos de Imputacion disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //busco los SubRubros de la empresa del usuario
            List<ModeloCabResponse> modeloCabResponse = new ArrayList<>();
            List<Payload> modeloCabRespons = new ArrayList<>();
            for(ModeloCab s : user.getIdPerfil().getIdSucursal().getIdEmpresa().getModeloCabCollection()) {
                ModeloCabResponse mcr = new ModeloCabResponse(s);
                mcr.agregarModeloDetalle(s.getModeloDetalleCollection());
                modeloCabResponse.add(mcr);
            }
            
            if(!modeloCabResponse.isEmpty()) {
                for(ModeloCabResponse modeloCab : modeloCabResponse) {
                    for(ModeloDetalleResponse modeloDetalle : modeloCab.getModeloDetalle()) {
                        if(modeloDetalle.getCtaContable() == null) {
                            continue;
                        }
                        ContPlanCuenta cont = contPlanCuentaFacade.getCuentaContable(Integer.parseInt(modeloDetalle.getCtaContable()));
                        if(cont != null){
                            modeloDetalle.setPlanCuenta(new ContPlanCuentaResponse(cont));
                        } else {
                            ContPlanCuentaResponse conta = new ContPlanCuentaResponse(Integer.parseInt(modeloDetalle.getCtaContable()),modeloDetalle.getDescripcion());
                            modeloDetalle.setPlanCuenta(conta);
                        }
                    }
                    modeloCabRespons.add(modeloCab);
                }
            }
            
            respuesta.setArraydatos(modeloCabRespons);
            respuesta.setControl(AppCodigo.OK, "Modelos de Imputacion");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setModeloImputacion(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            List<JsonElement> modeloDetalleList = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("modeloDetalle", jsonBody, "ArrayList");           
            
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

            //Me fijo que  descripcion no sea nulo
            if(descripcion == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta en nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            ModeloCab modeloCab = new ModeloCab();
            modeloCab.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            modeloCab.setDescripcion(descripcion);
            transaccion = modeloCabFacade.setModeloCabNuevo(modeloCab);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Modelo Cabecera, clave primaria repetida");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(modeloDetalleList.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Lista de detalles Vacia");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            for(JsonElement j : modeloDetalleList) {
                boolean transaccion2;
                ModeloDetalle modeloDetalle = new ModeloDetalle();
                String ctaContable = (String) Utils.getKeyFromJsonObject("ctaContable", j.getAsJsonObject(), "String");
                Integer orden = (Integer) Utils.getKeyFromJsonObject("orden", j.getAsJsonObject(), "Integer");
                String descripcionDetalle = (String) Utils.getKeyFromJsonObject("descripcionDetalle", j.getAsJsonObject(), "String");
                String dh = (String) Utils.getKeyFromJsonObject("dh", j.getAsJsonObject(), "String");
                boolean prioritario = (Boolean) Utils.getKeyFromJsonObject("prioritario", j.getAsJsonObject(), "boolean");
                BigDecimal valor = (BigDecimal) Utils.getKeyFromJsonObject("valor", j.getAsJsonObject(), "BigDecimal");
                String operador = (String) Utils.getKeyFromJsonObject("operador", j.getAsJsonObject(), "String");
                Integer idSisTipoModelo = (Integer) Utils.getKeyFromJsonObject("idSisTipoModelo", j.getAsJsonObject(), "Integer");
                Integer modulo = (Integer) Utils.getKeyFromJsonObject("modulo", j.getAsJsonObject(), "Integer");
                Integer idLibro = (Integer) Utils.getKeyFromJsonObject("idLibro", j.getAsJsonObject(), "Integer");
                
                if(idSisTipoModelo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                if(idLibro == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, idLibro nulo");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                Libro libro = libroFacade.find(idLibro);
                if(libro == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, libro no encontrado");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                if(valor == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, valor tiene que ser distinto de nulo");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                SisTipoModelo sisTipoModelo = sisTipoModeloFacade.find(idSisTipoModelo);
                if(sisTipoModelo == null) {
                    modeloCabFacade.deleteModeloCab(modeloCab);
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, el tipo de modelo con id " + sisTipoModelo + " no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                if(modulo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, modulo nulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            
                SisModulo mod = sisModuloFacade.find(modulo);
            
                if(mod == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                modeloDetalle.setCtaContable(ctaContable);
                modeloDetalle.setDescripcion(descripcionDetalle);
                modeloDetalle.setDh(dh);
                modeloDetalle.setIdModeloCab(modeloCab);
                modeloDetalle.setIdSisTipoModelo(sisTipoModelo);
                modeloDetalle.setOperador(operador);
                modeloDetalle.setOrden(orden);
                modeloDetalle.setPrioritario(prioritario);
                modeloDetalle.setValor(valor);
                modeloDetalle.setIdSisModulo(mod);
                modeloDetalle.setIdLibro(libro);
                transaccion2 = modeloDetalleFacade.setModeloDetalleNuevo(modeloDetalle);
                if(!transaccion2) {
                    modeloCabFacade.deleteModeloCab(modeloCab);
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Detalle, clave primaria repetida");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
            respuesta.setControl(AppCodigo.OK, "Modelo de Imputacion creado con exito, con detalles");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editModeloImputacion(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idModeloCab = (Integer) Utils.getKeyFromJsonObject("idModeloCab", jsonBody, "Integer");                      
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            List<JsonElement> modeloDetalleList = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("modeloDetalle", jsonBody, "ArrayList");           
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

            //Me fijo que  descripcion, idRubro e idEmpresa no sean nulos
            if(idModeloCab == null || descripcion == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta en nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            ModeloCab modeloCab = modeloCabFacade.find(idModeloCab);
            
            //valido que exista la lista de precio
            if(modeloCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modelo de Imputacion");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            modeloCab.setDescripcion(descripcion);
            modeloCab.getModeloDetalleCollection().clear();
            transaccion = modeloCabFacade.editModeloCab(modeloCab);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar la Lsita de Precios");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(modeloDetalleList.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Modelo de imputacion sin detalles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            for(JsonElement j : modeloDetalleList) {
                boolean transaccion2;
                String ctaContable = (String) Utils.getKeyFromJsonObject("ctaContable", j.getAsJsonObject(), "String");
                Integer orden = (Integer) Utils.getKeyFromJsonObject("orden", j.getAsJsonObject(), "Integer");
                String descripcionDetalle = (String) Utils.getKeyFromJsonObject("descripcionDetalle", j.getAsJsonObject(), "String");
                String dh = (String) Utils.getKeyFromJsonObject("dh", j.getAsJsonObject(), "String");
                boolean prioritario = (Boolean) Utils.getKeyFromJsonObject("prioritario", j.getAsJsonObject(), "boolean");
                BigDecimal valor = (BigDecimal) Utils.getKeyFromJsonObject("valor", j.getAsJsonObject(), "BigDecimal");
                String operador = (String) Utils.getKeyFromJsonObject("operador", j.getAsJsonObject(), "String");
                Integer idSisTipoModelo = (Integer) Utils.getKeyFromJsonObject("idSisTipoModelo", j.getAsJsonObject(), "Integer");
                Integer modulo = (Integer) Utils.getKeyFromJsonObject("modulo", j.getAsJsonObject(), "Integer");
                Integer idLibro = (Integer) Utils.getKeyFromJsonObject("idLibro", j.getAsJsonObject(), "Integer");
                
                if(idSisTipoModelo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                SisTipoModelo sisTipoModelo = sisTipoModeloFacade.find(idSisTipoModelo);
                if(sisTipoModelo == null) {
                    modeloCabFacade.deleteModeloCab(modeloCab);
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, el tipo de modelo con id " + sisTipoModelo + " no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                if(idLibro == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, idLibro nulo");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                Libro libro = libroFacade.find(idLibro);
                if(libro == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, libro no encontrado");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                
                if(modulo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, modulo nulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            
                SisModulo mod = sisModuloFacade.find(modulo);
            
                if(mod == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                ModeloDetalle modeloDetalle = new ModeloDetalle();
                modeloDetalle.setCtaContable(ctaContable);
                modeloDetalle.setDescripcion(descripcionDetalle);
                modeloDetalle.setDh(dh);
                modeloDetalle.setIdModeloCab(modeloCab);
                modeloDetalle.setIdSisTipoModelo(sisTipoModelo);
                modeloDetalle.setOperador(operador);
                modeloDetalle.setOrden(orden);
                modeloDetalle.setPrioritario(prioritario);
                modeloDetalle.setValor(valor);
                modeloDetalle.setIdSisModulo(mod);
                modeloDetalle.setIdLibro(libro);
                transaccion2 = modeloDetalleFacade.setModeloDetalleNuevo(modeloDetalle);
                
                if(!transaccion2) {
                    modeloCabFacade.deleteModeloCab(modeloCab);
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Detalle, clave primaria repetida");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
            respuesta.setControl(AppCodigo.OK, "Modelos de imputacion y detalles editado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @DELETE
    @Path ("/{idModeloCab}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteModeloImputacion(  
        @HeaderParam ("token") String token,
        @PathParam ("idModeloCab") Integer idModeloCab,
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
            
            //Me fijo que descCorta y idRubro no sean nulos
            if(idModeloCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            ModeloCab modeloCab = modeloCabFacade.find(idModeloCab);
            
            //valido que exista el modelo de imputacion
            if(modeloCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modelo de Imputacion");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            transaccion = modeloCabFacade.deleteModeloCab(modeloCab);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar el Modelo de Imputacion");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            respuesta.setControl(AppCodigo.OK, "Modelo de imputacion borrado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
