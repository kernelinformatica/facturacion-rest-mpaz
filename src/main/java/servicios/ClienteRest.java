package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ClienteResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Categoria;
import entidades.Padron;
import entidades.PadronClientes;
import entidades.PadronGral;
import entidades.PadronVendedor;
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
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.CategoriaFacade;
import persistencia.PadronClientesFacade;
import persistencia.PadronFacade;
import persistencia.PadronGralFacade;
import persistencia.PadronVendedorFacade;
import persistencia.SisSitIVAFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("cliente")
public class ClienteRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject PadronClientesFacade padronClientesFacade;
    @Inject PadronGralFacade padronGralFacade;
    @Inject PadronFacade padronFacade;
    @Inject SisSitIVAFacade sisSitIVAFacade;
    @Inject PadronVendedorFacade padronVendedorFacade;
    @Inject CategoriaFacade categoriaFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientes(  
        @HeaderParam ("token") String token,
        @QueryParam("codCliente") Integer codCliente,    
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
            List<Payload> clientesRespuesta = new ArrayList<>();
            if(codCliente == null) {         
                List<PadronClientes> clientes = padronClientesFacade.findByEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());

                //Valido que la lista de SisFormaPago no este vacia.
                if(clientes.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay clientes disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //busco los SubRubros de la empresa del usuario
                for(PadronClientes s : clientes) {
                    ClienteResponse sr = new ClienteResponse(s);
                    clientesRespuesta.add(sr);
                }
            } else {
                PadronClientes cliente = padronClientesFacade.findByEmpresaCodigo(user.getIdPerfil().getIdSucursal().getIdEmpresa(), codCliente);
                if(cliente == null) {
                    respuesta.setControl(AppCodigo.AVISO, "Cliente y Vendedor no asignados, desea asignarlos ahora?");
                    return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
                }
                ClienteResponse sr = new ClienteResponse(cliente);
                clientesRespuesta.add(sr);
            }
            respuesta.setArraydatos(clientesRespuesta);
            respuesta.setControl(AppCodigo.OK, "Lista de Clientes");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setClienteVendedor(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer padronCodigoVendedor = (Integer) Utils.getKeyFromJsonObject("padronCodigoVendedor", jsonBody, "Integer");
            Integer padronCodigoCliente = (Integer) Utils.getKeyFromJsonObject("padronCodigoCliente", jsonBody, "Integer");
            Integer idCategoriaVendedor = (Integer) Utils.getKeyFromJsonObject("idCategoriaVendedor", jsonBody, "Integer");
            Integer idCategoriaCliente = (Integer) Utils.getKeyFromJsonObject("idCategoriaCliente", jsonBody, "Integer");
            BigDecimal porcentaje = (BigDecimal) Utils.getKeyFromJsonObject("porcentaje", jsonBody, "BigDecimal");
            
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
            if(padronCodigoCliente == null || idCategoriaCliente == null || idCategoriaVendedor == null || padronCodigoVendedor == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            List<PadronGral> listaPersonas = new ArrayList<>();
            //Busco la categoria cliente
            Categoria catCliente = categoriaFacade.find(idCategoriaCliente);
            if(catCliente == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, categoria del Cliente no encontrada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            //Busco la categoria vendedor
            Categoria catVendedor = categoriaFacade.find(idCategoriaVendedor);
            if(catVendedor == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, categoria del Vendedor no encontrada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Alta de cliente en PadronGral si no existe.
            PadronGral padronGeneralCli = padronGralFacade.findByIdPadronGralEmpresa(padronCodigoCliente,user.getIdPerfil().getIdSucursal().getIdEmpresa());
            PadronGral padronGeneralVen = padronGralFacade.findByIdPadronGralEmpresa(padronCodigoVendedor,user.getIdPerfil().getIdSucursal().getIdEmpresa());
            
            //Pregunto si no esta dado de alta en la base mysql
            if(padronGeneralCli == null) {
                //Lo busco en la base de gestagro
                Padron padronCliente = padronFacade.getPadronByCodigo(padronCodigoCliente);
                //si no existe salgo del metodo
                if(padronCliente == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Persona no registrada en el padron de GestAgro");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }                
                //lo doy de alta con los datos de gestagro
                PadronGral padronGeneralCliNuevo = new PadronGral();
                padronGeneralCliNuevo.setApellido(padronCliente.getPadronApelli());
                if(padronCliente.getPadronCuit11() == null) {
                    padronGeneralCliNuevo.setCuit(padronCliente.getPadronCuil11().toString());
                } else {
                    padronGeneralCliNuevo.setCuit(padronCliente.getPadronCuit11().toString());
                }                
                padronGeneralCliNuevo.setDomicilio(padronCliente.getPadronDomici());
                padronGeneralCliNuevo.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                padronGeneralCliNuevo.setIdPadronGral(padronCliente.getPadronCodigo());
                padronGeneralCliNuevo.setIdCategoria(catCliente);
                //Busco la condicion de iva de acuerdo a la descripcion corta de gestagro, si no la encuentro le pongo por defecto la primera
                if(padronCliente.getCondIva() != null && padronCliente.getCondIva().getDescCorta() != null) {
                    SisSitIVA sisSitIVA = sisSitIVAFacade.getByDescCorta(padronCliente.getCondIva().getDescCorta());
                    if(sisSitIVA == null) {
                        sisSitIVA = sisSitIVAFacade.find(1);
                    }
                    padronGeneralCliNuevo.setIdSisSitIVA(sisSitIVA);
                } else {
                    SisSitIVA sisSitIVA = sisSitIVAFacade.find(1);
                    padronGeneralCliNuevo.setIdSisSitIVA(sisSitIVA);
                } 
                padronGeneralCliNuevo.setLocalidad(padronCliente.getCodigoPostal().toString());
                padronGeneralCliNuevo.setNombre(padronCliente.getPadronNombre());
                padronGeneralCliNuevo.setNro(padronCliente.getPadronDocnro().toString());
                listaPersonas.add(padronGeneralCliNuevo);
                padronGeneralCli = padronGeneralCliNuevo;
            }
            
             //Pregunto si no esta dado de alta en la base mysql
            if(padronGeneralVen == null) {
                //Lo busco en la base de gestagro
                Padron padronVendedor = padronFacade.getPadronByCodigo(padronCodigoVendedor);
                //si no existe salgo del metodo
                if(padronVendedor == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Persona no registrada en el padron de GestAgro");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }                
                //lo doy de alta con los datos de gestagro
                PadronGral padronGeneralVendedorNuevo = new PadronGral();
                padronGeneralVendedorNuevo.setApellido(padronVendedor.getPadronApelli());
                padronGeneralVendedorNuevo.setCuit(padronVendedor.getPadronCuit11().toString());
                padronGeneralVendedorNuevo.setDomicilio(padronVendedor.getPadronDomici());
                padronGeneralVendedorNuevo.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                padronGeneralVendedorNuevo.setIdPadronGral(padronVendedor.getPadronCodigo());
                padronGeneralVendedorNuevo.setIdCategoria(catVendedor);
                //Busco la condicion de iva de acuerdo a la descripcion corta de gestagro, si no la encuentro le pongo por defecto la primera
                if(padronVendedor.getCondIva() != null && padronVendedor.getCondIva().getDescCorta() != null) {
                    SisSitIVA sisSitIVA = sisSitIVAFacade.getByDescCorta(padronVendedor.getCondIva().getDescCorta());
                    if(sisSitIVA == null) {
                        sisSitIVA = sisSitIVAFacade.find(1);
                    }
                    padronGeneralVendedorNuevo.setIdSisSitIVA(sisSitIVA);
                } else {
                    SisSitIVA sisSitIVA = sisSitIVAFacade.find(1);
                    padronGeneralVendedorNuevo.setIdSisSitIVA(sisSitIVA);
                } 
                padronGeneralVendedorNuevo.setLocalidad(padronVendedor.getCodigoPostal().toString());
                padronGeneralVendedorNuevo.setNombre(padronVendedor.getPadronNombre());
                padronGeneralVendedorNuevo.setNro(padronVendedor.getPadronDocnro().toString()); 
                listaPersonas.add(padronGeneralVendedorNuevo);
                padronGeneralVen = padronGeneralVendedorNuevo;
            }
            if(!listaPersonas.isEmpty()) {                
                try {
                    for(PadronGral p : listaPersonas) {
                        boolean transaccion;
                        transaccion = padronGralFacade.setPadronGralNuevo(p);
                        if(!transaccion) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la persona:"+ p.getNombre());
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                    }
                } catch(Exception ex) {
                    respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
            if(padronGeneralVen != null && padronGeneralCli != null) {
                if(padronGeneralCli.getPadronClientesCollection().isEmpty()) {
                    PadronClientes paCli = new PadronClientes();
                    paCli.setIdPadronGral(padronGeneralCli);                   
                    if(padronGeneralVen.getPadronVendedorCollection().isEmpty()) {
                        PadronVendedor paVen = new PadronVendedor();
                        paVen.setIdPadronGral(padronGeneralVen);
                        paVen.setPorcentaje(porcentaje);
                        paCli.setIdVendedor(paVen);
                        boolean transaccion1;
                        transaccion1 = padronVendedorFacade.setPadronVendedorNuevo(paVen);
                        if(!transaccion1) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Vendedor");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                    } else {
                        PadronVendedor pv = padronGeneralVen.getPadronVendedorCollection().iterator().next();
                        pv.setPorcentaje(porcentaje);
                        pv.getIdPadronGral().setIdCategoria(catVendedor);
                        boolean transaccion1;
                        transaccion1 = padronVendedorFacade.editPadronVendedor(pv);
                        if(!transaccion1) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el Vendedor");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                        paCli.setIdVendedor(pv);
                    }
                    boolean transaccion2;
                    transaccion2 = padronClientesFacade.setPadronClientesNuevo(paCli);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Cliente");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                } else if(padronGeneralVen.getPadronVendedorCollection().isEmpty()) {
                    PadronClientes pc = padronGeneralCli.getPadronClientesCollection().iterator().next();
                    PadronVendedor paVen = new PadronVendedor();
                    paVen.setIdPadronGral(padronGeneralVen);
                    paVen.setPorcentaje(porcentaje);
                    pc.setIdVendedor(paVen);
                    boolean transaccion1;
                    transaccion1 = padronVendedorFacade.setPadronVendedorNuevo(paVen);
                    if(!transaccion1) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Vendedor");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    boolean transaccion2;
                    pc.getIdPadronGral().setIdCategoria(catCliente);
                    transaccion2 = padronClientesFacade.editPadronClientes(pc);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el cliente");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                } else if(!padronGeneralVen.getPadronVendedorCollection().isEmpty() && !padronGeneralCli.getPadronClientesCollection().isEmpty()) {
                    PadronClientes pc = padronGeneralCli.getPadronClientesCollection().iterator().next();
                    PadronVendedor pv = padronGeneralVen.getPadronVendedorCollection().iterator().next();
                    pv.setPorcentaje(porcentaje);
                    pv.getIdPadronGral().setIdCategoria(catVendedor);
                        boolean transaccion1;
                        transaccion1 = padronVendedorFacade.editPadronVendedor(pv);
                        if(!transaccion1) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el Vendedor");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                    pc.setIdVendedor(pv);
                    pc.getIdPadronGral().setIdCategoria(catCliente);
                    boolean transaccion2;
                    transaccion2 = padronClientesFacade.editPadronClientes(pc);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el cliente");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            } else {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo relacionar cliente con vendedor");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.CREADO, "Padron creado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
}
