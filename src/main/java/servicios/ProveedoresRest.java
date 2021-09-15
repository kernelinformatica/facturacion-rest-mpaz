package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.Payload;
import datos.ProveedorResponse;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Categoria;
import entidades.PadronGral;
import entidades.PadronProveedor;
import entidades.SisSitIVA;
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
import persistencia.CategoriaFacade;
import persistencia.PadronFacade;
import persistencia.PadronGralFacade;
import persistencia.PadronProveedorFacade;
import persistencia.SisSitIVAFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("proveedores")
public class ProveedoresRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject PadronFacade padronFacade;
    @Inject PadronProveedorFacade padronProveedorFacade;
    @Inject PadronGralFacade padronGralFacade;
    @Inject SisSitIVAFacade sisSitIVAFacade;
    @Inject CategoriaFacade categoriaFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProveedores(  
        @HeaderParam ("token") String token,
        @QueryParam("codProveedor") Integer codProveedor,
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
            
               List<Payload> proveedoresResp = new ArrayList<>();
            if(codProveedor == null) {
                List<PadronProveedor> proveedores = padronProveedorFacade.findByEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(proveedores.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no hay proveedores disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                for(PadronProveedor p : proveedores) {
                    ProveedorResponse pr = new ProveedorResponse(p);
                    proveedoresResp.add(pr);
                }
            } else {
                //Busco en el padron general el proveedor
                PadronGral padronGral = padronGralFacade.find(codProveedor);
                if(padronGral == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe en el padron general");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Me fijo si tiene la relacion en la Tabla PadronProveedor
                if(padronGral.getPadronProveedorCollection().isEmpty()) {
                   respuesta.setControl(AppCodigo.ERROR, "Error, no hay proveedores disponibles con el codigo ingresado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Eligo el primero de la coleccion(es una relacion de uno a uno)
                PadronProveedor prov = padronGral.getPadronProveedorCollection().iterator().next();
                if(prov == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, el padron general no tiene asignado un proveedor");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                ProveedorResponse pr = new ProveedorResponse(prov);
                proveedoresResp.add(pr);
            }
            respuesta.setArraydatos(proveedoresResp);
            respuesta.setControl(AppCodigo.OK, "Proveedores");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setProveedores(  
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idPadron = (Integer) Utils.getKeyFromJsonObject("idPadron", jsonBody, "Integer");
            String nombre = (String) Utils.getKeyFromJsonObject("nombre", jsonBody, "String");
            String apellido = (String) Utils.getKeyFromJsonObject("apellido", jsonBody, "String");
            String cuit = (String) Utils.getKeyFromJsonObject("cuit", jsonBody, "String");
            String domicilio = (String) Utils.getKeyFromJsonObject("domicilio", jsonBody, "String");
            String nro = (String) Utils.getKeyFromJsonObject("nro", jsonBody, "String");
            String localidad = (String) Utils.getKeyFromJsonObject("localidad", jsonBody, "String");
            Integer idCategoria = (Integer) Utils.getKeyFromJsonObject("idCategoria", jsonBody, "Integer");
            Integer idSisSitIVA = (Integer) Utils.getKeyFromJsonObject("idSisSitIVA", jsonBody, "Integer");
            BigDecimal iibbRet = (BigDecimal) Utils.getKeyFromJsonObject("iibbRet", jsonBody, "BigDecimal");
            BigDecimal iibbPer = (BigDecimal) Utils.getKeyFromJsonObject("iibbPer", jsonBody, "BigDecimal");
            

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
            
            if(idPadron == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar un proveedor");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idCategoria == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar una categoria");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idSisSitIVA == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar una situacion de iva");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            Categoria categoria = categoriaFacade.find(idCategoria);
            if(categoria == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe la categoria seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisSitIVA sisSitIva = sisSitIVAFacade.find(idSisSitIVA);
            if(sisSitIva == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error,no existe la situacion de iva seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            PadronGral padronGralEncontrado = padronGralFacade.findByIdPadronGralEmpresa(idPadron, user.getIdPerfil().getIdSucursal().getIdEmpresa());
            
            if(padronGralEncontrado != null) {
                if(!padronGralEncontrado.getPadronProveedorCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, el proveedor ya se encuentra dado de alta");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                } else {
                    //Nuevo Padron Proveedor
                    PadronProveedor padronProveedor = new PadronProveedor();
                    padronProveedor.setIdPadronGral(padronGralEncontrado);
                    padronProveedor.setIibbPer(iibbPer);
                    padronProveedor.setIibbRet(iibbRet);
                    boolean transaccion;
                    transaccion = padronProveedorFacade.setPadronProveedorNueva(padronProveedor);
                    if(!transaccion) {
                        respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo dar de alta el proveedor");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            } else {
                //Nuevo Padron General y Padron Proveedor
                PadronGral padronGral = new PadronGral();
                padronGral.setApellido(apellido);
                padronGral.setCuit(cuit);
                padronGral.setDomicilio(domicilio);           
                padronGral.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                padronGral.setIdPadronGral(idPadron);               
                padronGral.setLocalidad(localidad);
                padronGral.setNombre(nombre);
                padronGral.setNro(nro);
                padronGral.setIdSisSitIVA(sisSitIva);
                padronGral.setIdCategoria(categoria);
                
                //Nuevo Padron Proveedor
                PadronProveedor padronProveedor = new PadronProveedor();
                padronProveedor.setIdPadronGral(padronGral);
                padronProveedor.setIibbPer(iibbPer);
                padronProveedor.setIibbRet(iibbRet);
                try {                    
                    boolean transaccion;
                    boolean transaccion1;
                    transaccion1 = padronGralFacade.setPadronGralNuevo(padronGral);
                    if(!transaccion1) {
                        respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo dar de alta el padron general");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    transaccion = padronProveedorFacade.setPadronProveedorNueva(padronProveedor);
                    if(!transaccion) {
                        respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo dar de alta el proveedor con el padron general");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }                
                } catch (Exception e) {
                    respuesta.setControl(AppCodigo.ERROR, e.getMessage());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }               
            }           
            respuesta.setControl(AppCodigo.OK, "Proveedor creado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();       
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProveedores(  
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idPadronProveedor = (Integer) Utils.getKeyFromJsonObject("idPadronProveedor", jsonBody, "Integer");
            String nombre = (String) Utils.getKeyFromJsonObject("nombre", jsonBody, "String");
            String apellido = (String) Utils.getKeyFromJsonObject("apellido", jsonBody, "String");
            String cuit = (String) Utils.getKeyFromJsonObject("cuit", jsonBody, "String");
            String domicilio = (String) Utils.getKeyFromJsonObject("domicilio", jsonBody, "String");
            String nro = (String) Utils.getKeyFromJsonObject("nro", jsonBody, "String");
            String localidad = (String) Utils.getKeyFromJsonObject("localidad", jsonBody, "String");
            Integer idCategoria = (Integer) Utils.getKeyFromJsonObject("idCategoria", jsonBody, "Integer");
            Integer idSisSitIVA = (Integer) Utils.getKeyFromJsonObject("idSisSitIVA", jsonBody, "Integer");
            BigDecimal iibbRet = (BigDecimal) Utils.getKeyFromJsonObject("iibbRet", jsonBody, "BigDecimal");
            BigDecimal iibbPer = (BigDecimal) Utils.getKeyFromJsonObject("iibbPer", jsonBody, "BigDecimal");
            

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
            
            if(idPadronProveedor == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar un proveedor");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idCategoria == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar una categoria");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idSisSitIVA == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, debe seleccionar una situacion de iva");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            Categoria categoria = categoriaFacade.find(idCategoria);
            if(categoria == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe la categoria seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisSitIVA sisSitIva = sisSitIVAFacade.find(idSisSitIVA);
            if(sisSitIva == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error,no existe la situacion de iva seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            PadronProveedor padronProveedor = padronProveedorFacade.find(idPadronProveedor);
            
            if(padronProveedor == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el proveedor seleccionado no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            } 
            
            //Nuevo Padron Proveedor
            padronProveedor.setIibbPer(iibbPer);
            padronProveedor.setIibbRet(iibbRet);
            padronProveedor.getIdPadronGral().setApellido(apellido);
            padronProveedor.getIdPadronGral().setCuit(cuit);
            padronProveedor.getIdPadronGral().setDomicilio(domicilio);           
            padronProveedor.getIdPadronGral().setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());               
            padronProveedor.getIdPadronGral().setLocalidad(localidad);
            padronProveedor.getIdPadronGral().setNombre(nombre);
            padronProveedor.getIdPadronGral().setNro(nro);
            padronProveedor.getIdPadronGral().setIdSisSitIVA(sisSitIva);
            padronProveedor.getIdPadronGral().setIdCategoria(categoria);
            boolean transaccion;
            transaccion = padronProveedorFacade.editPadronProveedor(padronProveedor);
            
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo editar el proveedor");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            respuesta.setControl(AppCodigo.GUARDADO, "Proveedor editado con exito");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();       
        
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @DELETE
    @Path ("/{idPadronProveedor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deletePadronProveedor(  
        @HeaderParam ("token") String token,
        @PathParam ("idPadronProveedor") Integer idPadronProveedor,
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
            if(idPadronProveedor == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, idPadronProveedor no puede ser nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            PadronProveedor padronProveedor = padronProveedorFacade.find(idPadronProveedor);
            
            boolean transaccion;
            transaccion = padronProveedorFacade.deletePadronProveedor(padronProveedor);      
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no se borrar el Proveedor");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            respuesta.setControl(AppCodigo.BORRADO, "Proveedor borrado con exito");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
