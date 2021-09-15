package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.CteNumeradorResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CteNumerador;
import entidades.PtoVenta;
import entidades.CteTipo;
import entidades.CteTipoSisLetra;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
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
import persistencia.CteNumeradorFacade;
import persistencia.PtoVentaFacade;
import persistencia.CteTipoFacade;
import persistencia.CteTipoSisLetraFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("cteNumerador")
public class NumeradorRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject CteTipoSisLetraFacade cteTipoSisLetraFacade;
    @Inject PtoVentaFacade ptoVentaFacade;
    @Inject CteNumeradorFacade cteNumeradorFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumerador(  
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
            
            //valido que tenga Rubros disponibles
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getCteTipoCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay Tipos de Comprobantes disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //busco los numeradores de la empresa del usuario
            List<Payload> numeradores = new ArrayList<>();
            for(CteTipo p : user.getIdPerfil().getIdSucursal().getIdEmpresa().getCteTipoCollection()){
                for(CteTipoSisLetra c : p.getCteTipoSisLetraCollection()) {
                    for(CteNumerador n : c.getCteNumeradorCollection()) {
                    CteNumeradorResponse t = new CteNumeradorResponse(n);
                    numeradores.add(t);}
                }               
            }
            
            respuesta.setArraydatos(numeradores);
            respuesta.setControl(AppCodigo.OK, "Lista de Numeradores");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setNumerador(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body                       
            Integer numerador = (Integer) Utils.getKeyFromJsonObject("numerador", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            Date fechaApertura = (Date) Utils.getKeyFromJsonObject("fechaApertura", jsonBody, "Date");
            Date fechaCierre = (Date) Utils.getKeyFromJsonObject("fechaCierre", jsonBody, "Date");
            Integer idCteTipoSisLetra = (Integer) Utils.getKeyFromJsonObject("idCteTipoSisLetra", jsonBody, "Integer");
            Integer idPtoVenta = (Integer) Utils.getKeyFromJsonObject("idPtoVenta", jsonBody, "Integer");
            Integer ptoVenta = (Integer) Utils.getKeyFromJsonObject("ptoVenta", jsonBody, "Integer");
            Date vtoCai = (Date) Utils.getKeyFromJsonObject("vtoCai", jsonBody, "Date");
            String cai = (String) Utils.getKeyFromJsonObject("cai", jsonBody, "String");
            boolean electronico = (Boolean) Utils.getKeyFromJsonObject("electronico", jsonBody, "boolean");
            
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

            //Me fijo que  los campos no sean nulos
            if(fechaApertura == null || fechaCierre == null || idCteTipoSisLetra == null || numerador == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(fechaApertura.after(fechaCierre)) {
                respuesta.setControl(AppCodigo.ERROR, "Fecha hasta debe ser mayor o igual a la fecha desde");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            CteTipoSisLetra cteTipoSisLetra = cteTipoSisLetraFacade.find(idCteTipoSisLetra);
            if(cteTipoSisLetra == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el tipo de comprobante seleccionado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            boolean transaccion1;
            if(idPtoVenta == null && ptoVenta != null) {            
                //Alta de un nuevo Pto de venta
                PtoVenta valido = ptoVentaFacade.findByEmpresaSucursalPto(user.getIdPerfil().getIdSucursal().getIdEmpresa(),user.getIdPerfil().getIdSucursal(),ptoVenta);
                if(valido != null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no se puede dar de alta el pto de venta: "+ ptoVenta + "ya existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                PtoVenta ptoVta = new PtoVenta();                
                ptoVta.setPtoVenta(ptoVenta);
                ptoVta.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                ptoVta.setIdSucursal(user.getIdPerfil().getIdSucursal());
                
                CteNumerador cteNumerador = new CteNumerador();
                cteNumerador.setDescripcion(descripcion);
                cteNumerador.setFechaApertura(fechaApertura);
                cteNumerador.setFechaCierre(fechaCierre);
                cteNumerador.setIdCteTipoSisLetra(cteTipoSisLetra);
                cteNumerador.setIdPtoVenta(ptoVta);
                cteNumerador.setNumerador(numerador);
                cteNumerador.setCai(cai);
                cteNumerador.setElectronico(electronico);
                if(vtoCai != null) {
                    cteNumerador.setVtoCai(vtoCai);
                } else {
                    cteNumerador.setVtoCai(fechaCierre);
                }
                try {
                    transaccion = ptoVentaFacade.setPtoVentaNuevo(ptoVta);
                    transaccion1 = cteNumeradorFacade.setCteNumeradorNuevo(cteNumerador);
                    if(!transaccion || !transaccion1) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    respuesta.setControl(AppCodigo.CREADO, "Numerador creado con exito");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                } catch (Exception ex) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            } else if(idPtoVenta != null){
                PtoVenta ptoVta = ptoVentaFacade.find(idPtoVenta);
                if(ptoVta == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Punto de Venta seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                CteNumerador cteNumerador = new CteNumerador();
                cteNumerador.setDescripcion(descripcion);
                cteNumerador.setFechaApertura(fechaApertura);
                cteNumerador.setFechaCierre(fechaCierre);
                cteNumerador.setIdCteTipoSisLetra(cteTipoSisLetra);
                cteNumerador.setIdPtoVenta(ptoVta);
                cteNumerador.setNumerador(numerador);
                cteNumerador.setCai(cai);
                cteNumerador.setElectronico(electronico);
                if(vtoCai != null) {
                    cteNumerador.setVtoCai(vtoCai);
                } else {
                    cteNumerador.setVtoCai(fechaCierre);
                }
                transaccion1 = cteNumeradorFacade.setCteNumeradorNuevo(cteNumerador);
                if(!transaccion1) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                respuesta.setControl(AppCodigo.CREADO, "Numerador creado con exito");
                return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editNumerador(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idCteNumerador = (Integer) Utils.getKeyFromJsonObject("idCteNumerador", jsonBody, "Integer");            
            Integer numerador = (Integer) Utils.getKeyFromJsonObject("numerador", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            Date fechaApertura = (Date) Utils.getKeyFromJsonObject("fechaApertura", jsonBody, "Date");
            Date fechaCierre = (Date) Utils.getKeyFromJsonObject("fechaCierre", jsonBody, "Date");
            Integer idCteTipoSisLetra = (Integer) Utils.getKeyFromJsonObject("idCteTipoSisLetra", jsonBody, "Integer");
            Integer idPtoVenta = (Integer) Utils.getKeyFromJsonObject("idPtoVenta", jsonBody, "Integer");
            Integer ptoVenta = (Integer) Utils.getKeyFromJsonObject("ptoVenta", jsonBody, "Integer");
            Date vtoCai = (Date) Utils.getKeyFromJsonObject("vtoCai", jsonBody, "Date");
            String cai = (String) Utils.getKeyFromJsonObject("cai", jsonBody, "String");
            boolean electronico = (Boolean) Utils.getKeyFromJsonObject("electronico", jsonBody, "boolean");
            
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

            //Me fijo que  los campos no sean nulos
            if(fechaApertura == null || fechaCierre == null || idCteTipoSisLetra == null || idCteNumerador == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(fechaApertura.after(fechaCierre)) {
                respuesta.setControl(AppCodigo.ERROR, "Fecha hasta debe ser mayor o igual a la fecha desde");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            CteNumerador cteNumerador = cteNumeradorFacade.find(idCteNumerador);
            if(cteNumerador == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Numerador seleccionado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            CteTipoSisLetra cteTipoSisLetra = cteTipoSisLetraFacade.find(idCteTipoSisLetra);
            if(cteTipoSisLetra == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el tipo de comprobante seleccionado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            boolean transaccion;
            boolean transaccion1;
            if(idPtoVenta == null && ptoVenta != null) {            
                //Alta de un nuevo Pto de venta
                PtoVenta valido = ptoVentaFacade.findByEmpresaSucursalPto(user.getIdPerfil().getIdSucursal().getIdEmpresa(),user.getIdPerfil().getIdSucursal(),ptoVenta);
                if(valido != null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no se puede dar de alta el pto de venta: "+ ptoVenta + "ya existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                PtoVenta ptoVta = new PtoVenta();                
                ptoVta.setPtoVenta(ptoVenta);
                ptoVta.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                ptoVta.setIdSucursal(user.getIdPerfil().getIdSucursal());

                cteNumerador.setDescripcion(descripcion);
                cteNumerador.setFechaApertura(fechaApertura);
                cteNumerador.setFechaCierre(fechaCierre);
                cteNumerador.setIdCteTipoSisLetra(cteTipoSisLetra);
                cteNumerador.setIdPtoVenta(ptoVta);
                cteNumerador.setNumerador(numerador);
                cteNumerador.setCai(cai);
                cteNumerador.setElectronico(electronico);
                if(vtoCai != null) {
                    cteNumerador.setVtoCai(vtoCai);
                } else {
                    cteNumerador.setVtoCai(fechaCierre);
                }
                try {
                    transaccion = ptoVentaFacade.setPtoVentaNuevo(ptoVta);
                    transaccion1 = cteNumeradorFacade.editCteNumerador(cteNumerador);
                    if(!transaccion || !transaccion1) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    respuesta.setControl(AppCodigo.CREADO, "Numerador creado con exito");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                } catch (Exception ex) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            } else if(idPtoVenta != null){
                PtoVenta ptoVta = ptoVentaFacade.find(idPtoVenta);
                if(ptoVta == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Punto de Venta seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                cteNumerador.setDescripcion(descripcion);
                cteNumerador.setFechaApertura(fechaApertura);
                cteNumerador.setFechaCierre(fechaCierre);
                cteNumerador.setIdCteTipoSisLetra(cteTipoSisLetra);
                cteNumerador.setIdPtoVenta(ptoVta);
                cteNumerador.setNumerador(numerador);
                cteNumerador.setCai(cai);
                cteNumerador.setElectronico(electronico);
                if(vtoCai != null) {
                    cteNumerador.setVtoCai(vtoCai);
                } else {
                    cteNumerador.setVtoCai(fechaCierre);
                }
                transaccion1 = cteNumeradorFacade.editCteNumerador(cteNumerador);
                if(!transaccion1) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                respuesta.setControl(AppCodigo.GUARDADO, "Numerador creado con exito");
                return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Numerador");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }   
    
    
    @DELETE
    @Path ("/{idNumerador}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteNumerador(  
        @HeaderParam ("token") String token,
        @PathParam ("idNumerador") Integer idNumerador,
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
            if(idNumerador == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar un Numerador");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            CteNumerador cteNumerador = cteNumeradorFacade.find(idNumerador);
            
            //Pregunto si existe el Numerador
            if(cteNumerador == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Marca");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
                try {
                    transaccion = cteNumeradorFacade.deleteCteNumerador(cteNumerador);
                    if(!transaccion) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar el Numerador");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                } catch (Exception e) {
                    respuesta.setControl(AppCodigo.ERROR, e.getMessage());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }                           
            respuesta.setControl(AppCodigo.OK, "Numerador borrado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
