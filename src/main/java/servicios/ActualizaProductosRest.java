/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Empresa;
import entidades.FacArticulosSybase;
import entidades.FacPreciosSybase;
import entidades.Marca;
import entidades.ModeloCab;
import entidades.Producto;
import entidades.SisIVA;
import entidades.SisMonedas;
import entidades.SisUnidad;
import entidades.SubRubro;
import entidades.Usuario;
import java.math.BigDecimal;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.EmpresaFacade;
import persistencia.FacArticulosSybaseFacade;
import persistencia.FacPreciosSybaseFacade;
import persistencia.MarcaFacade;
import persistencia.ModeloCabFacade;
import persistencia.ProductoFacade;
import persistencia.SisIVAFacade;
import persistencia.SisMonedasFacade;
import persistencia.SisUnidadFacade;
import persistencia.SubRubroFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */
@Stateless
@Path("actualiza-productos")
public class ActualizaProductosRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    @Inject FacArticulosSybaseFacade facArticulosSybaseFacade;
    @Inject FacPreciosSybaseFacade facPreciosSybaseFacade;
    @Inject ProductoFacade productoFacade;
    @Inject EmpresaFacade empresaFacade;
    @Inject SubRubroFacade subRubroFacade;
    @Inject SisUnidadFacade sisUnidadFacade;
    @Inject SisIVAFacade sisIvaFacade;
    @Inject ModeloCabFacade modeloCabFacade;
    @Inject MarcaFacade marcaFacade;
    @Inject SisMonedasFacade sisMonedasFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductos(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            String codigoProductoDesde = (String) Utils.getKeyFromJsonObject("codigoProductoDesde", jsonBody, "String");
            String codigoProductoHasta = (String) Utils.getKeyFromJsonObject("codigoProductoHasta", jsonBody, "String");
            
            if(codigoProductoDesde == null || codigoProductoHasta == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, codigo de producto inválido");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            BigDecimal numeroProdDesde = new BigDecimal(codigoProductoDesde);
            BigDecimal numeroProdHasta = new BigDecimal(codigoProductoHasta);

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
            
            for(Integer i = numeroProdDesde.intValue(); i <= numeroProdHasta.intValue(); i++) {
                Empresa empresa = empresaFacade.getEmpresaById(2);
                FacArticulosSybase articuloSybase = facArticulosSybaseFacade.getArticuloSybaseByCod(padLeft(i, 5, "0"));
                if(articuloSybase == null) {
                    continue;
                }
                FacPreciosSybase precioSybase = facPreciosSybaseFacade.getPreciosByCod(articuloSybase.getArtCodigo());
                if(precioSybase == null) {
                    continue;
                }
                
                Producto producto = productoFacade.getByCodigoProdEmpresa(articuloSybase.getArtCodigo(), empresa);
                if(producto != null) {
                    producto.setDescripcionCorta("a");
                    if(articuloSybase.getArtDescri() != null && articuloSybase.getArtDescri().length() > 0) {
                        producto.setDescripcion(articuloSybase.getArtDescri());
                    } else {
                        producto.setDescripcion("-");
                    }
                    producto.setCostoReposicion(BigDecimal.ZERO);
                    producto.setCodigoBarra("a");
                    productoFacade.editProducto(producto);
                } else {
                    Producto nuevoProducto = new Producto();
                    nuevoProducto.setCodProducto(articuloSybase.getArtCodigo());
                    nuevoProducto.setCodigoBarra("a");
                    nuevoProducto.setDescripcionCorta("a");
                    if(articuloSybase.getArtDescri() != null && articuloSybase.getArtDescri().length() > 0) {
                        nuevoProducto.setDescripcion(articuloSybase.getArtDescri());
                    } else {
                        nuevoProducto.setDescripcion("-");
                    }
                    SubRubro subRubro = subRubroFacade.getByIdSubRubro(11);
                    nuevoProducto.setIdSubRubros(subRubro);
                    SisUnidad unidad = sisUnidadFacade.getUnidadById(1);
                    nuevoProducto.setUnidadCompra(unidad);
                    nuevoProducto.setUnidadVenta(unidad);
                    nuevoProducto.setAptoCanje(false);
                    nuevoProducto.setStock(false);
                    nuevoProducto.setTrazable(false);
                    nuevoProducto.setTraReceta(false);
                    nuevoProducto.setTraInforma(false);
                    nuevoProducto.setGtin("a");
                    nuevoProducto.setPuntoPedido(articuloSybase.getArtPtoPedido());
                    nuevoProducto.setCostoReposicion(BigDecimal.ZERO);
                    nuevoProducto.setPrecioVentaProv(precioSybase.getPrePrecio().multiply(new BigDecimal(100)));
                    
                    if(articuloSybase.getArtIvaRi().equals('S')) {
                        SisIVA iva = sisIvaFacade.getIvaById(1);
                        nuevoProducto.setIdIVA(iva);
                    } else if(articuloSybase.getArtIvaRi().equals('N')) {
                        SisIVA iva = sisIvaFacade.getIvaById(2);
                        nuevoProducto.setIdIVA(iva);
                    } else {
                        SisIVA iva = sisIvaFacade.getIvaById(1);
                        nuevoProducto.setIdIVA(iva);
                    }
                    if(articuloSybase.getArtRubro() != null && articuloSybase.getArtRubro().length() > 0) {
                        nuevoProducto.setObservaciones(articuloSybase.getArtRubro());
                    } else {
                        nuevoProducto.setObservaciones("-");
                    }
                    nuevoProducto.setIdEmpresa(2);
                    nuevoProducto.setPadCod(articuloSybase.getPadronCodigo());
                    ModeloCab modeloCab = modeloCabFacade.getModeloCabById(6);
                    nuevoProducto.setIdModeloCab(modeloCab);
                    Marca marca = marcaFacade.getMarcaById(2);
                    nuevoProducto.setIdMarca(marca);
                    SisMonedas moneda = sisMonedasFacade.getSisMonedasById(1);
                    nuevoProducto.setIdMoneda(moneda);
                    productoFacade.setProductoNuevo(nuevoProducto);
                }
                
            }
            
            respuesta.setControl(AppCodigo.OK, "Productos actualizados correctamente");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    public String padLeft(Integer s, Integer n, String r) {
        if(s.toString().length() < 5) {
            return String.format("%" + n + "s", s.toString()).replace(" ", r);
        } else {
            return s.toString();
        }
    }
}
