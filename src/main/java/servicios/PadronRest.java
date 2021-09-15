package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.PadronResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Categoria;
import entidades.Padron;
import entidades.PadronGral;
import entidades.Parametro;
import entidades.SisSitIVA;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import persistencia.PadronFacade;
import persistencia.PadronGralFacade;
import persistencia.PadronVendedorFacade;
import persistencia.ParametroFacade;
import persistencia.SisSitIVAFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Dario Quiroga
 */

@Stateless
@Path("padron")
public class PadronRest {

    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject PadronFacade padronFacade;
    @Inject ParametroFacade parametroFacade;
    @Inject PadronGralFacade padronGralFacade;
    @Inject SisSitIVAFacade sisSitIVAFacade;
    @Inject CategoriaFacade categoriaFacade;

 
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPadron(  
        @HeaderParam ("token") String token,
        @QueryParam ("grupo") Integer grupo,
        @QueryParam ("elementos") String elementos,
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
            List<Payload> clientes = new ArrayList<>();
            
            if(grupo == null && elementos != null) {
                List<Padron> listaPadron = padronFacade.findAll();

                //valido que tenga campos disponibles
                if(listaPadron.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay registros en el Padron");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                List<Padron> padornes = new ArrayList<>();
                padornes = listaPadron.stream()
                    .filter(
                       a -> 
                       a.getPadronApelli().contains(elementos) || 
                       a.getPadronCodigo().toString().contains(elementos)
                    )
                    .collect(Collectors.toList());
                for(Padron p: padornes) {
                    PadronResponse fp = new PadronResponse(p);
                    clientes.add(fp);
                }
            } else if(grupo != null && elementos != null){
                List<Parametro> listaParametros = parametroFacade.getByGrupoEmpresa(grupo, user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                
                if(listaParametros.isEmpty() || listaParametros == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el grupo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                List<Padron> listaPadron = new ArrayList();
                
                List<Short> ids = new ArrayList<Short>(); 
                for(Parametro p : listaParametros) {
                    ids.add(Short.parseShort(p.getValor()));
                }
                
                listaPadron.addAll(padronFacade.getPadronByCategoria(ids));
                
                if(listaPadron.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Padron no disponible");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                List<Padron> padornes = new ArrayList<>();
                padornes = listaPadron.stream()
                    .filter(
                       a -> 
                       a.getPadronApelli().toLowerCase().contains(elementos.toLowerCase()) || 
                       a.getPadronCodigo().toString().contains(elementos))    
                      .collect(Collectors.toList()); 
                
                for(Padron p: padornes) {
                    PadronResponse fp = new PadronResponse(p);
                    clientes.add(fp);
                }
            } else if(grupo != null && elementos == null){
                List<Parametro> listaParametros = parametroFacade.getByGrupoEmpresa(grupo, user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                
                if(listaParametros.isEmpty() || listaParametros == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el grupo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                List<Padron> listaPadron = new ArrayList();
                
                List<Short> ids = new ArrayList<Short>(); 
                for(Parametro p : listaParametros) {
                    ids.add(Short.parseShort(p.getValor()));
                }
                
                listaPadron.addAll(padronFacade.getPadronByCategoria(ids));
                
                if(listaPadron.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Padron no disponible");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                for(Padron p: listaPadron) {
                    PadronResponse fp = new PadronResponse(p);
                    clientes.add(fp);
                }
            }
            respuesta.setArraydatos(clientes);
            respuesta.setControl(AppCodigo.OK, "Padron");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPadron(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws Exception {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer padronCodigo = (Integer) Utils.getKeyFromJsonObject("padronCodigo", jsonBody, "Integer");
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
            if(padronCodigo == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            PadronGral padronGeneral = padronGralFacade.findByIdPadronGralEmpresa(padronCodigo,user.getIdPerfil().getIdSucursal().getIdEmpresa());
            
            if(padronGeneral == null) {
                //Lo busco en la base de gestagro
                Padron padronCliente = padronFacade.getPadronByCodigo(padronCodigo);
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
                Categoria catCliente = categoriaFacade.find(padronCliente.getPadronCatego().intValue());
                    if(catCliente == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error, categoria del Cliente no encontrada");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
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
                boolean transaccion;
                transaccion = padronGralFacade.setPadronGralNuevo(padronGeneralCliNuevo);
                if(!transaccion) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la persona:"+ padronGeneralCliNuevo.getNombre());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                respuesta.setControl(AppCodigo.CREADO, "Se dio de alta a la persona:"+ padronGeneralCliNuevo.getNombre());
                return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
            } else {
                respuesta.setControl(AppCodigo.ERROR, "Ya estaba de alta");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Error al dar de alta en padron general: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, "Se rompio todo");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }  
}
