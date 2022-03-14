package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.CteTipoResponse;
import datos.Payload;
import datos.ServicioResponse;
import datos.SisLetraSisCodAfipResponse;
import datos.SisMonedasResponse;
import entidades.Acceso;
import entidades.CteTipo;
import entidades.CteTipoSisLetra;
import entidades.Reporte;
import entidades.SisCodigoAfip;
import entidades.SisComprobante;
import entidades.SisLetra;
import entidades.SisModulo;
import entidades.SisMonedas;
import entidades.SisOperacionComprobante;
import entidades.SisSitIVA;
import entidades.SisSitIVALetras;
import entidades.SisTipoOperacion;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
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
import persistencia.CteTipoFacade;
import persistencia.CteTipoSisLetraFacade;
import persistencia.ReporteFacade;
import persistencia.SisCodigoAfipFacade;
import persistencia.SisComprobanteFacade;
import persistencia.SisLetraFacade;
import persistencia.SisModuloFacade;
import persistencia.SisMonedasFacade;
import persistencia.SisSitIVAFacade;
import persistencia.SisTipoOperacionFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("cteTipo") 
public class CteTipoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject CteTipoFacade cteTipoFacade;
    @Inject SisComprobanteFacade sisComprobanteFacade;
    @Inject SisTipoOperacionFacade sisTipoOperacionFacade;
    @Inject SisCodigoAfipFacade sisCodigoAfipFacade;
    @Inject Utils utils;
    @Inject SisLetraFacade sisLetraFacade;
    @Inject CteTipoSisLetraFacade cteTipoSisLetraFacade; 
    @Inject SisModuloFacade sisModuloFacade;
    @Inject SisSitIVAFacade sisSitIvaFacade;
    @Inject SisMonedasFacade sisMonedasFacade;
    @Inject ReporteFacade reporteFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComprobantes(  
        @HeaderParam ("token") String token,
        @QueryParam("sisModulo") Integer sisModulo,
        @QueryParam("sisComprobante") Integer sisComprobante,
        @QueryParam("idCteTipo") Integer idCteTipo,
        @QueryParam("sisTipoOperacion") Integer sisTipoOperacion,
        @QueryParam("condicion") String condicion,
        @QueryParam("sisSitIva") String sisSitIva,
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
            //valido que tenga comprobantes disponibles
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getCteTipoCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay Tipos de Comprobantes disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }           
            //Armo la respuesta de cteTipos
            List<Payload> cteTipos = new ArrayList<>();
            //devuelvo todos los cteTipo
            if(sisModulo == null && sisComprobante == null && idCteTipo == null && sisTipoOperacion == null && condicion == null && sisSitIva == null) {
                for(CteTipo p : user.getIdPerfil().getIdSucursal().getIdEmpresa().getCteTipoCollection()){
                    CteTipoResponse pr = new CteTipoResponse(p);
                    if(!p.getCteTipoSisLetraCollection().isEmpty() && p.getCteTipoSisLetraCollection() != null) {
                        pr.agregarLetrasCodigos(p.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                    }
                    cteTipos.add(pr);
                }
            //Devuelvo por condicion
            } else if(sisModulo == null && sisComprobante == null && idCteTipo == null && sisTipoOperacion == null && condicion.equals("propio") && sisSitIva == null) {
                for(CteTipo p : user.getIdPerfil().getIdSucursal().getIdEmpresa().getCteTipoCollection()){
                    if(p.getIdSisComprobante().getPropio().equals(1)) {
                        CteTipoResponse pr = new CteTipoResponse(p);
                        if(!p.getCteTipoSisLetraCollection().isEmpty() && p.getCteTipoSisLetraCollection() != null) {
                            pr.agregarLetrasCodigos(p.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                        }
                        cteTipos.add(pr);
                    }
                }    
                
            //Devuelvo cteTipo por tipo de operacion 
            } else if(sisModulo == null && sisComprobante == null && idCteTipo == null && sisTipoOperacion != null && sisSitIva == null) {
                
                List<CteTipo> cteTipoList = new ArrayList<>();
               cteTipoList = cteTipoFacade.getBySisTipoOperacion(sisTipoOperacion);
               
                for(CteTipo p : cteTipoList){
                    CteTipoResponse pr = new CteTipoResponse(p);
                    if(!p.getCteTipoSisLetraCollection().isEmpty() && p.getCteTipoSisLetraCollection() != null) {
                        pr.agregarLetrasCodigos(p.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                    }
                    cteTipos.add(pr);
                } 
               
             
            //Devuelvo cteTipo por modulo
            } else if(sisModulo != null && sisComprobante == null && idCteTipo == null && sisTipoOperacion == null && sisSitIva == null) {
                List<CteTipo> cteTipoList = new ArrayList<>();
                if(sisModulo == 3) {
                    cteTipoList = cteTipoFacade.findAll();
                } else {
                    cteTipoList = cteTipoFacade.getByModulo(user.getIdPerfil().getIdSucursal().getIdEmpresa(), sisModulo); 
                }
                //valido que tenga comprobantes disponibles
                if(cteTipoList.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay Tipos de Comprobantes disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                //Ordeno la lista
                //Collections.sort(cteTipoList, (o1, o2) -> o1.getIdSisComprobante().getOrden().compareTo(o2.getIdSisComprobante().getOrden()));
                
                //Armo la respuesta
                for(CteTipo p : cteTipoList){
                    CteTipoResponse pr = new CteTipoResponse(p);
                    if(!p.getCteTipoSisLetraCollection().isEmpty() && p.getCteTipoSisLetraCollection() != null) {
                        pr.agregarLetrasCodigos(p.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                    }
                    cteTipos.add(pr);
                }                
            //Devuelvo cteTipo por comprobante
            } else if(sisModulo == null && sisComprobante != null && idCteTipo == null && sisTipoOperacion == null && sisSitIva == null) {
                SisComprobante sisComprobanteEncontrado = sisComprobanteFacade.find(sisComprobante);            
                if(sisComprobanteEncontrado == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el sis comprobante");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                List<CteTipo> cteTipo = cteTipoFacade.getBySisComprobanteEmpresa(sisComprobanteEncontrado, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(cteTipo == null || cteTipo.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no hay cte tipo disponibles para ese sis comprobante");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                //Ordeno la lista
                //Collections.sort(cteTipo, (o1, o2) -> o1.getIdSisComprobante().getOrden().compareTo(o2.getIdSisComprobante().getOrden()));
               
                //Armo la respuesta
                for(CteTipo c : cteTipo) {
                    CteTipoResponse cteTipoResponse = new CteTipoResponse(c);
                    if(!c.getCteTipoSisLetraCollection().isEmpty() && c.getCteTipoSisLetraCollection() != null) {
                        cteTipoResponse.agregarLetrasCodigos(c.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                    }
                    cteTipos.add(cteTipoResponse);
                }
            //Devuelvo los numeradores de ese cteTipo   
            } else if(sisModulo == null && sisComprobante == null && idCteTipo != null && sisTipoOperacion == null && sisSitIva == null) {
                CteTipo cteTipo = cteTipoFacade.find(idCteTipo);                
                
                if(cteTipo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe ese tipo de comprobamte");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                if(cteTipo.getIdSisComprobante().getPropio() == 0) {
                    respuesta.setControl(AppCodigo.AVISO, "No existen numeradores para ese tipo de comprobante, debera cargarlo a mano");
                    return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
                }                               
                //Armo la respuesta  
                CteTipoResponse cteTipoResponse = new CteTipoResponse(cteTipo);
                if(!cteTipo.getCteTipoSisLetraCollection().isEmpty() && cteTipo.getCteTipoSisLetraCollection() != null) {
                    cteTipoResponse.agregarLetrasCodigos(cteTipo.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                }
                cteTipos.add(cteTipoResponse); 
            //Devuelvo el comprobante anterior para los relacionados
            } else if(sisModulo != null && sisComprobante == null && idCteTipo != null && sisTipoOperacion != null && sisSitIva == null) {
                CteTipo cteTipo = cteTipoFacade.find(idCteTipo);                
                if(cteTipo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe ese tipo de comprobamte");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                SisTipoOperacion tipoOperacion = sisTipoOperacionFacade.find(sisTipoOperacion);               
                if(tipoOperacion == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el tipo de operacion");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                List<CteTipo> cteTipoAnteriores = cteTipoFacade.getComprobanteAnterior(cteTipo,sisModulo, tipoOperacion);
                if(cteTipoAnteriores.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no hay comprobantes anteriores");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Ordeno la lista
                //Collections.sort(cteTipoAnteriores, (o1, o2) -> o1.getIdSisComprobante().getOrden().compareTo(o2.getIdSisComprobante().getOrden()));
                
                //Armo la respuesta
                for(CteTipo c : cteTipoAnteriores) {
                    CteTipoResponse cteTip = new CteTipoResponse(c);
                    if(!c.getCteTipoSisLetraCollection().isEmpty() && c.getCteTipoSisLetraCollection() != null) {
                        cteTip.agregarLetrasCodigos(c.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                    }
                    cteTipos.add(cteTip);
                }                
            } else if(sisModulo == null && sisComprobante == null && idCteTipo == null && sisTipoOperacion != null && sisSitIva == null) {
                SisTipoOperacion tipoOperacion = sisTipoOperacionFacade.find(sisTipoOperacion);
                
                if(tipoOperacion == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el tipo de operacion");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                if(tipoOperacion.getSisOperacionComprobanteCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no hay tipos de comprobantes para el tipo de operacion seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                List<CteTipo> listaTipos = new ArrayList<>();
                
                for(SisOperacionComprobante p : tipoOperacion.getSisOperacionComprobanteCollection()) {
                    if(p.getIdSisComprobantes().getCteTipoCollection().isEmpty()) {
                        continue;
                    } else {
                        for(CteTipo c : p.getIdSisComprobantes().getCteTipoCollection()) {
                            //filtro por empresa
                            if(c.getIdEmpresa().equals(user.getIdPerfil().getIdSucursal().getIdEmpresa()) && c.getIdEmpresa().getIdEmpresa().equals(p.getIdEmpresa()) ){
                                //Agrego todos los comprobantes de acuerdo a la coleccion de sisOperacionComprobante para asignarles un id y luego filtrarlas
                                if(listaTipos.contains(c)) {
                                    CteTipo cteTipoNuevo = new CteTipo(c);
                                    cteTipoNuevo.setIdSisOperacionComprobante(p.getIdSisOperacionComprobantes());
                                    listaTipos.add(cteTipoNuevo);
                                } else {
                                    c.setIdSisOperacionComprobante(p.getIdSisOperacionComprobantes());                               
                                    listaTipos.add(c);
                                }
                            }
                        }                        
                    }                   
                }
                //Armo la respuesta
                for(CteTipo c : listaTipos) {
                    CteTipoResponse ctr = new CteTipoResponse(c);
                    if(!c.getCteTipoSisLetraCollection().isEmpty() && c.getCteTipoSisLetraCollection() != null) {
                        ctr.agregarLetrasCodigos(c.getCteTipoSisLetraCollection(), user.getIdPerfil().getIdSucursal());
                    }
                    //Agrego los parametros de la tabla SisOperacionComprobante
                    for(SisOperacionComprobante soc : c.getIdSisComprobante().getSisOperacionComprobanteCollection()) {
                        //Aca filtro las que realmente quiero, de acuerdo al SisComprobante, tipo de operacion y idSiscomprobante seteado anteriormente
                        if(soc.getIdSisComprobantes().getIdSisComprobantes().equals(c.getIdSisComprobante().getIdSisComprobantes()) && 
                           soc.getIdSisTipoOperacion().equals(tipoOperacion) &&
                           soc.getIdSisOperacionComprobantes().equals(c.getIdSisOperacionComprobante())) {                              
                                //seteo todos los parametros de acuerdo a la tabla SisOperacionesComprobantes
                                ctr.getComprobante().setIncluyeIva(soc.getIncluyeIva());
                                ctr.getComprobante().setIncluyeNeto(soc.getIncluyeNeto());
                                ctr.getComprobante().setOrden(soc.getOrden());
                                ctr.getComprobante().setDifCotizacion(soc.getDifCotizacion());
                                ctr.getComprobante().setReferencia(soc.getDescripcion());
                                ctr.getComprobante().setIdSisOperacionComprobante(soc.getIdSisOperacionComprobantes());
                                ctr.getComprobante().setRelacionadosMultiples(soc.getRelacionadosMultiples());
                                ctr.getComprobante().setAdmiteRelacionMultiple(soc.getAdmiteRelacionMultiple());
                                ctr.getComprobante().setUsaContrato(soc.getUsaContrato());
                                ctr.getComprobante().setPermiteImporteCero(soc.getPermiteImporteCero());
                                ctr.getComprobante().setUsaRelacion(soc.getUsaRelacion());
                                ctr.getComprobante().setRelacionadoObligatorio(soc.getRelacionadoObligatorio());
                                ctr.getComprobante().setObservaciones(soc.getObservaciones());
                            //Si la moneda en la tabla SisOperacionesComprobantes es nula agrego todas sino, la que esta dada de alta
                            if(soc.getIdSisMoneda() == null) {
                                List<SisMonedas> todas = sisMonedasFacade.findAll();
                                ctr.getComprobante().agregarMonedas(todas);
                            } else {
                                SisMonedas unica = sisMonedasFacade.find(soc.getIdSisMoneda());
                                SisMonedasResponse u = new SisMonedasResponse(unica);
                                ctr.getComprobante().getMonedas().add(u);
                            }
                        }                     
                    }
                    cteTipos.add(ctr);
                }
                //Devuelvo los comprobantes para compra y para venta
            } else if(sisModulo == null && sisComprobante == null && idCteTipo == null && sisTipoOperacion != null && sisSitIva != null){
                
                SisTipoOperacion tipoOperacion = sisTipoOperacionFacade.find(sisTipoOperacion);               
                if(tipoOperacion == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el tipo de operacion");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                SisSitIVA sitIva = sisSitIvaFacade.getByDescCorta(sisSitIva);
                if(sitIva == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe la situacion de iva seleccionada");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                List<CteTipo> listaTipos = new ArrayList<>();
                //Filtro por tipo de operacion, modulo y empresa
                for(SisOperacionComprobante p : tipoOperacion.getSisOperacionComprobanteCollection()) {
                    if(p.getIdSisComprobantes().getCteTipoCollection().isEmpty()) {
                        continue;
                    } else {
                        for(CteTipo c : p.getIdSisComprobantes().getCteTipoCollection()) {
                            //Pregunto si tiene el mismo idEmpresa en cteTipo y en SisOperacionComprobante 
                            if(c.getIdEmpresa().equals(user.getIdPerfil().getIdSucursal().getIdEmpresa()) && c.getIdEmpresa().getIdEmpresa().equals(p.getIdEmpresa()) ){
                                if(listaTipos.contains(c)) {
                                    CteTipo cteTipoNuevo = new CteTipo(c);
                                    cteTipoNuevo.setIdSisOperacionComprobante(p.getIdSisOperacionComprobantes());
                                    listaTipos.add(cteTipoNuevo);
                                } else {
                                    c.setIdSisOperacionComprobante(p.getIdSisOperacionComprobantes());                               
                                    listaTipos.add(c);
                                }
                            }
                        }                        
                    }                   
                }
                List<CteTipoResponse> listaResponse = new ArrayList<>();
                //Filtro las letras por tipo de situacion fiscal del cliente o proveedor
                for(CteTipo c : listaTipos) {
                    CteTipoResponse ctr = new CteTipoResponse(c);
                    //Agrego la condicion para los netos e Ivas
                    if(!c.getIdSisComprobante().getSisOperacionComprobanteCollection().isEmpty()) {
                        for(SisOperacionComprobante soc : c.getIdSisComprobante().getSisOperacionComprobanteCollection()) {
                            if(soc.getIdSisComprobantes().getIdSisComprobantes().equals(c.getIdSisComprobante().getIdSisComprobantes()) && 
                               soc.getIdSisTipoOperacion().equals(tipoOperacion) &&
                               soc.getIdSisOperacionComprobantes().equals(c.getIdSisOperacionComprobante())) {                              
                                    //seteo todos los parametros de acuerdo a la tabla SisOperacionesComprobantes
                                    ctr.getComprobante().setIncluyeIva(soc.getIncluyeIva());
                                    ctr.getComprobante().setIncluyeNeto(soc.getIncluyeNeto());
                                    ctr.getComprobante().setOrden(soc.getOrden());
                                    ctr.getComprobante().setDifCotizacion(soc.getDifCotizacion());
                                    ctr.getComprobante().setReferencia(soc.getDescripcion());
                                    ctr.getComprobante().setIdSisOperacionComprobante(soc.getIdSisOperacionComprobantes());
                                    ctr.getComprobante().setRelacionadosMultiples(soc.getRelacionadosMultiples());
                                    ctr.getComprobante().setAdmiteRelacionMultiple(soc.getAdmiteRelacionMultiple());
                                    ctr.getComprobante().setUsaContrato(soc.getUsaContrato());
                                    ctr.getComprobante().setPermiteImporteCero(soc.getPermiteImporteCero());
                                    ctr.getComprobante().setUsaRelacion(soc.getUsaRelacion());
                                    ctr.getComprobante().setRelacionadoObligatorio(soc.getRelacionadoObligatorio());
                                    ctr.getComprobante().setObservaciones(soc.getObservaciones());
                                //Si la moneda en la tabla SisOperacionesComprobantes es nula agrego todas sino, la que esta dada de alta
                                if(soc.getIdSisMoneda() == null) {
                                    List<SisMonedas> todas = sisMonedasFacade.findAll();
                                    ctr.getComprobante().agregarMonedas(todas);
                                } else {
                                    SisMonedas unica = sisMonedasFacade.find(soc.getIdSisMoneda());
                                    SisMonedasResponse u = new SisMonedasResponse(unica);
                                    ctr.getComprobante().getMonedas().add(u);
                                }
                            }                     
                        }
                    }
                    
                    //Agrego las letras y codigo afip
                    for(CteTipoSisLetra cl : c.getCteTipoSisLetraCollection()) {
                        for(SisSitIVALetras si : sitIva.getSisSitIVALetrasCollection()) {
                            if(si.getIdSisLetras().equals(cl.getIdSisLetra()) && si.getIdSisModulos().equals(c.getIdSisComprobante().getIdSisModulos())) {
                                SisLetraSisCodAfipResponse sisLetCod = new SisLetraSisCodAfipResponse(cl);
                                if(!cl.getCteNumeradorCollection().isEmpty()) {
                                    if(user.getIdPtoVenta() == null) {
                                        sisLetCod.agregarNumeradores(cl.getCteNumeradorCollection(), user.getIdPerfil().getIdSucursal());
                                    } else {
                                        sisLetCod.agregarNumeradores(cl.getCteNumeradorCollection(), user.getIdPtoVenta());
                                    }
                                }
                                ctr.getLetrasCodigos().add(sisLetCod);
                            }
                        }
                    }
                    listaResponse.add(ctr);
                }                           
                if(!listaResponse.isEmpty()) {
                    //Ordeno la lista
                    Collections.sort(listaResponse, (o1, o2) -> o1.getComprobante().getOrden().compareTo(o2.getComprobante().getOrden()));
                    //Armo la respuesta
                    cteTipos.addAll(listaResponse);
                }                                
            } else {
                respuesta.setControl(AppCodigo.ERROR, "No hay Tipos de Comprobantes disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setArraydatos(cteTipos);
            respuesta.setControl(AppCodigo.OK, "Lista de Tipos de Comprobantes");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setComprobante(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer codigoComp = (Integer) Utils.getKeyFromJsonObject("codigoComp", jsonBody, "Integer");
            String descCorta = (String) Utils.getKeyFromJsonObject("descCorta", jsonBody, "String");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            boolean cursoLegal = (boolean) Utils.getKeyFromJsonObject("cursoLegal", jsonBody, "boolean");
            boolean requiereFormaPago = (boolean) Utils.getKeyFromJsonObject("requiereFormaPago", jsonBody, "boolean");            
            String surenu = (String) Utils.getKeyFromJsonObject("surenu", jsonBody, "String");
            String observaciones = (String) Utils.getKeyFromJsonObject("observaciones", jsonBody, "String");
            Integer idSisComprobante = (Integer) Utils.getKeyFromJsonObject("idSisComprobante", jsonBody, "Integer");
            List<JsonElement> letras = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("letras", jsonBody, "ArrayList");
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

            //Me fijo que descCorta, descripcion, surenu, codigoComp y codigoAfip no sean nulos
            if(descCorta == null || descripcion == null || surenu == null || codigoComp == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campos esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisComprobante sisComprobante = sisComprobanteFacade.find(idSisComprobante);
            
            if(sisComprobante == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            String  nombreReporte = "reporteComprobante";
            Reporte reporte = reporteFacade.findBynombreEmpresa(nombreReporte,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
             
            if(reporte == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, dar de alta un registro en la tabla reporte con nombre : reporteComprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            String detalleReporte = "";
            if(cursoLegal) {
                detalleReporte = " ";
            } else {
                detalleReporte = "DOCUMENTO NO VALIDO COMO FACTURA";
            }
           
            boolean transaccion;
            CteTipo newCte = new CteTipo();
            newCte.setCodigoComp(codigoComp);
            newCte.setCursoLegal(cursoLegal);
            newCte.setDescCorta(descCorta);
            newCte.setDescripcion(descripcion);
            newCte.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            newCte.setObservaciones(observaciones);
            newCte.setSurenu(surenu);
            newCte.setIdSisComprobante(sisComprobante);
            newCte.setRequiereFormaPago(requiereFormaPago);
            newCte.setIdReportes(reporte);
            newCte.setDetalleReporte(detalleReporte);
            transaccion = cteTipoFacade.setCteTipoNuevo(newCte, user);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Tipo de Comprobante, Codigo de Comprobante existente");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(letras != null && !letras.isEmpty()){
                for(JsonElement l : letras) {
                    Integer idSisLetra = (Integer) Utils.getKeyFromJsonObject("idSisLetra", l.getAsJsonObject(), "Integer"); 
                    Integer codigoAfip = (Integer) Utils.getKeyFromJsonObject("codigoAfip", l.getAsJsonObject(), "Integer"); 
                    
                    if(idSisLetra == null || codigoAfip == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Tipo de Comprobante,idSisLetra o codigo afip nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    SisLetra sisLetra = sisLetraFacade.find(idSisLetra);
                    
                    if(sisLetra == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Tipo de Comprobante, no existe la letra seleccionada");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    SisCodigoAfip sisCodigoAfip = sisCodigoAfipFacade.find(codigoAfip);
                    if(sisCodigoAfip == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error, no existe el sisCodigoAfip");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
             
                    boolean transaccion2;
                    CteTipoSisLetra cteTipoSisLetra = new CteTipoSisLetra();
                    cteTipoSisLetra.setIdCteTipo(newCte);
                    cteTipoSisLetra.setIdSisLetra(sisLetra);
                    cteTipoSisLetra.setIdSisCodigoAfip(sisCodigoAfip);
                    transaccion2 = cteTipoSisLetraFacade.setCteTipoSisLetraNuevo(cteTipoSisLetra);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Tipo de Comprobante");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            respuesta.setControl(AppCodigo.OK, "Tipo de Comprobante creado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }   
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editComprobante(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idCteTipo = (Integer) Utils.getKeyFromJsonObject("idCteTipo", jsonBody, "Integer");
            Integer codigoComp = (Integer) Utils.getKeyFromJsonObject("codigoComp", jsonBody, "Integer");
            String descCorta = (String) Utils.getKeyFromJsonObject("descCorta", jsonBody, "String");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            boolean cursoLegal = (boolean) Utils.getKeyFromJsonObject("cursoLegal", jsonBody, "boolean");
            boolean requiereFormaPago = (boolean) Utils.getKeyFromJsonObject("requiereFormaPago", jsonBody, "boolean");
            String surenu = (String) Utils.getKeyFromJsonObject("surenu", jsonBody, "String");
            String observaciones = (String) Utils.getKeyFromJsonObject("observaciones", jsonBody, "String");
            Integer idSisComprobante = (Integer) Utils.getKeyFromJsonObject("idSisComprobante", jsonBody, "Integer");
            List<JsonElement> letras = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("letras", jsonBody, "ArrayList");
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

            //Me fijo que descCorta, descripcion, surenu, codigoComp y codigoAfip no sean nulos
            if(idCteTipo == 0 || descCorta == null || descripcion == null || surenu == null || codigoComp == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            CteTipo newCte = cteTipoFacade.find(idCteTipo);
            
            SisComprobante sisComprobante = sisComprobanteFacade.find(idSisComprobante);
            
            if(sisComprobante == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            
            //Pregunto si existe el Cte
            if(newCte == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el tipo de comprobonta");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            newCte.setCodigoComp(codigoComp);
            newCte.setCursoLegal(cursoLegal);
            newCte.setDescCorta(descCorta);
            newCte.setDescripcion(descripcion);
            newCte.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            newCte.setObservaciones(observaciones);
            newCte.setSurenu(surenu);
            newCte.setIdSisComprobante(sisComprobante);
            newCte.setRequiereFormaPago(requiereFormaPago);
            //newCte.getCteTipoSisLetraCollection().clear();
            transaccion = cteTipoFacade.editCteTipo(newCte);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el Tipo de Comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(letras != null && !letras.isEmpty()){
                for(JsonElement l : letras) {
                    Integer idSisLetra = (Integer) Utils.getKeyFromJsonObject("idSisLetra", l.getAsJsonObject(), "Integer"); 
                    Integer codigoAfip = (Integer) Utils.getKeyFromJsonObject("codigoAfip", l.getAsJsonObject(), "Integer"); 
                    
                    if(idSisLetra == null || codigoAfip == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Tipo de Comprobante,idSisLetra o codigo afip nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    SisLetra sisLetra = sisLetraFacade.find(idSisLetra);
                    
                    if(sisLetra == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Tipo de Comprobante, no existe la letra seleccionada");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }                    
                    
                    SisCodigoAfip sisCodigoAfip = sisCodigoAfipFacade.find(codigoAfip);
                    if(sisCodigoAfip == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error, no existe el sisCodigoAfip");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    boolean transaccion2;
                    CteTipoSisLetra cteTipoSisLetra = new CteTipoSisLetra();
                    cteTipoSisLetra.setIdCteTipo(newCte);
                    cteTipoSisLetra.setIdSisLetra(sisLetra);
                    cteTipoSisLetra.setIdSisCodigoAfip(sisCodigoAfip);
                    transaccion2 = cteTipoSisLetraFacade.setCteTipoSisLetraNuevo(cteTipoSisLetra);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Tipo de Comprobante");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            respuesta.setControl(AppCodigo.OK, "Tipo de Comprobante editado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }   

    @DELETE
    @Path ("/{idComprobante}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteComprobante(  
        @HeaderParam ("token") String token,
        @PathParam ("idComprobante") int idComprobante,
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
            
            //busco el cteTipo a borrar
            CteTipo cteTipo = cteTipoFacade.find(idComprobante);
            if(cteTipo == null) {
                respuesta.setControl(AppCodigo.ERROR, "El Tipo de Comprobante no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = cteTipoFacade.deleteCteTipo(cteTipo);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar el Tipo de Comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Tipo de Comprobante borrado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
