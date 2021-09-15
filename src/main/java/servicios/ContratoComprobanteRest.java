package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.FactCabResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Contrato;
import entidades.ContratoDet;
import entidades.FactCab;
import entidades.Marca;
import entidades.SisComprobante;
import entidades.SisOperacionComprobante;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
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
import persistencia.ContratoDetFacade;
import persistencia.ContratoFacade;
import persistencia.FactCabFacade;
import persistencia.SisOperacionComprobanteFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("relacionContrato")
public class ContratoComprobanteRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject FactCabFacade factCabFacade;
    @Inject ContratoFacade contratoFacade;
    @Inject SisOperacionComprobanteFacade sisOperacionComprobanteFacade;
    @Inject ContratoDetFacade contratoDetFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContratosPendientes(
        @HeaderParam ("token") String token,
        @QueryParam("codPadron") Integer codPadron,
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

            List<Payload> factResp = new ArrayList<>();
            
            List<SisOperacionComprobante> sisOpCom = sisOperacionComprobanteFacade.findIdByEmpresaUsaContrato(user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            if(sisOpCom.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existen comprobantes a los cuales se les deba asignar un contrato");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }                

            //saco los siscomprobantes repetidos
            List<SisComprobante> sisComprobantes = new ArrayList<>();
            for(SisOperacionComprobante s : sisOpCom){
                if(sisComprobantes.contains(s.getIdSisComprobantes())) {
                    continue;
                } else {
                    sisComprobantes.add(s.getIdSisComprobantes());
                }
            }

            System.out.println(sisComprobantes.size());
            List<FactCab> comprobantesEncontrados = new ArrayList<>();
            for(SisComprobante s : sisComprobantes) {
                if(!factCabFacade.findPendientesContratos(user.getIdPerfil().getIdSucursal().getIdEmpresa(),s).isEmpty() && factCabFacade.findPendientesContratos(user.getIdPerfil().getIdSucursal().getIdEmpresa(),s) != null) {
                    comprobantesEncontrados.addAll(factCabFacade.findPendientesContratos(user.getIdPerfil().getIdSucursal().getIdEmpresa(), s));
                }
            }

            if(comprobantesEncontrados.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no se encontraron comprobantes");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
               
            if(codPadron == null) {                   
               List<FactCab> comprobantesFiltrados = new ArrayList<>();
                for(FactCab f : comprobantesEncontrados) {
                    if(f.getContratoDetCollection().isEmpty()) {
                        SisOperacionComprobante sop = sisOperacionComprobanteFacade.find(f.getIdSisOperacionComprobantes());
                        if(sop != null) {
                            if(sop.getUsaContrato()) {
                                comprobantesFiltrados.add(f);
                            }
                        }
                    }
                }
                
                if(comprobantesFiltrados.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no se encontraron comprobantes sin contratos relacionados");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                for(FactCab f : comprobantesFiltrados) {
                    FactCabResponse fr = new FactCabResponse(f);
                    factResp.add(fr);
                }
            } else {
                List<FactCab> comprobantesFiltrados = new ArrayList<>();
                for(FactCab f : comprobantesEncontrados) {
                    if(f.getContratoDetCollection().isEmpty() && f.getIdPadron() == codPadron) {
                        SisOperacionComprobante sop = sisOperacionComprobanteFacade.find(f.getIdSisOperacionComprobantes());
                        if(sop != null) {
                            if(sop.getUsaContrato()) {
                                comprobantesFiltrados.add(f);
                            }
                        }
                    }
                }
                
                if(comprobantesFiltrados.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no se encontraron comprobantes sin contratos relacionados");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                for(FactCab f : comprobantesFiltrados) {
                    FactCabResponse fr = new FactCabResponse(f);
                    factResp.add(fr);
                }
            }
            respuesta.setArraydatos(factResp);
            respuesta.setControl(AppCodigo.OK, "Comprobantes sin contratos");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setContratoComp(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idComprobante = (Integer) Utils.getKeyFromJsonObject("idComprobante", jsonBody, "Integer");
            Integer idContrato = (Integer) Utils.getKeyFromJsonObject("idContrato", jsonBody, "Integer");
            
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
            if(idContrato == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el idContrato nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idComprobante == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el idComprobante nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            Contrato contrato = contratoFacade.find(idContrato);
            if(contrato == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el contrato seleccionado no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            FactCab factCab = factCabFacade.find(idComprobante);
            if(factCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el comprobante seleccionado no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(factCab.getKilosCanje() == null && factCab.getKilosCanje() == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el comprobante seleccionado no tiene importe cargado o es igual a 0");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }           
            Integer kilosCanje = factCab.getKilosCanje();
            
            //Busco el contrato si es que lleva
            ContratoDet contratoDet = new ContratoDet();
            if(kilosCanje > contrato.getKilos()) {
                respuesta.setControl(AppCodigo.ERROR, "Error, los kilos sobrepasan a lo estipulado en el contrato");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            if(!contrato.getContratoDetCollection().isEmpty()) {
                Integer sumatoriaCantidad = 0;
                for(ContratoDet d : contrato.getContratoDetCollection()) {
                    sumatoriaCantidad = sumatoriaCantidad + d.getKilos();
                }
                sumatoriaCantidad = kilosCanje + sumatoriaCantidad;
                if(sumatoriaCantidad > contrato.getKilos()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, los kilos sobrepasan a lo estipulado en el contrato");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
                
            contratoDet.setIdContratos(contrato);
            contratoDet.setIdFactCab(factCab);
            contratoDet.setImporte(factCab.getPrecioReferenciaCanje());
            contratoDet.setKilos(kilosCanje);
            contratoDet.setObservaciones(factCab.getObservaciones());
        
            boolean transaccion;
            transaccion = contratoDetFacade.setContratoDetNuevo(contratoDet);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la relacion");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.CREADO, "Relacion creada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
}
