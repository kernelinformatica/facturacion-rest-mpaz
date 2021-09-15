package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.Payload;
import datos.ProductoResponse;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Cultivo;
import entidades.ListaPrecio;
import entidades.ListaPrecioDet;
import entidades.Marca;
import entidades.ModeloCab;
import entidades.ProdCultivo;
import entidades.Producto;
import entidades.SisIVA;
import entidades.SisMonedas;
import entidades.SisUnidad;
import entidades.SubRubro;
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
import persistencia.CultivoFacade;
import persistencia.ListaPrecioFacade;
import persistencia.MarcaFacade;
import persistencia.ModeloCabFacade;
import persistencia.ProdCultivoFacade;
import persistencia.ProductoFacade;
import persistencia.ProdumoFacade;
import persistencia.SisIVAFacade;
import persistencia.SisMonedasFacade;
import persistencia.SisUnidadFacade;
import persistencia.SubRubroFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */
@Stateless
@Path("productos")
public class ProductoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject ProductoFacade productoFacade;
    @Inject SubRubroFacade subRubroFacade;
    @Inject SisUnidadFacade sisUnidadFacade;
    @Inject SisIVAFacade sisIVAFacade;
    @Inject ModeloCabFacade modeloCabFacade;
    @Inject MarcaFacade marcaFacade;
    @Inject ListaPrecioFacade listaPrecioFacade;
    @Inject CultivoFacade cultivoFacade;
    @Inject ProdCultivoFacade prodCultivoFacade;
    @Inject SisMonedasFacade sisMonedasFacade;
    @Inject ProdumoFacade produmoFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductos(  
        @HeaderParam ("token") String token,
        @QueryParam ("tipo") String tipo,
        @QueryParam ("listaPrecio") Integer idListaPrecio,
        @QueryParam("aptoCanje") boolean aptoCanje,
        @QueryParam("idDeposito") Integer idDeposito,
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
            
            //Busco los productos de la empresa
            List<Producto> productos = productoFacade.getProductosByEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            
            //Valido que haya productos para esa empresa.
            if(productos.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay Productos disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            List<Payload> productosResponse = new ArrayList<>();
            //Devuelvo productos para compra
            if(tipo != null && tipo.equals("reducida") && idListaPrecio == null && idDeposito == null) {
                for(Producto s : productos) {
                    ProductoResponse sr = new ProductoResponse(s.getIdProductos(),s.getDescripcion(),s.getCodProducto());
                    productosResponse.add(sr);                       
                }
            //devuelvo productos para venta
            } else if(tipo != null && tipo.equals("reducida") && idListaPrecio != null && idDeposito != null) {
                //Busco la lista de precio
                ListaPrecio lista = listaPrecioFacade.find(idListaPrecio);
                //Pregunto si existe
                if(lista == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe la lista de precios");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                //Pregunto si tiene detalles
                if(lista.getListaPrecioDetCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos disponibles para la lista de precios seleccionada");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                //Armo la respuesta
                for(ListaPrecioDet d : lista.getListaPrecioDetCollection()) {
                    //Filtro por aptoCanje
                    /*if(aptoCanje && d.getIdProductos().getAptoCanje()) {
                        if((!d.getIdProductos().getProdumoCollection().isEmpty()  && idDeposito != null) || !d.getIdProductos().getStock()) {
                            if(produmoFacade.vigenciaEnDeposito(idDeposito,d.getIdProductos()) || !d.getIdProductos().getStock()) {
                                ProductoResponse sr = new ProductoResponse(d.getIdProductos().getIdProductos(),d.getIdProductos().getDescripcion(),d.getIdProductos().getCodProducto());
                                productosResponse.add(sr);
                            }
                        }
                    } else if(!aptoCanje) {
                        if(!d.getIdProductos().getProdumoCollection().isEmpty() || !d.getIdProductos().getStock()) {
                            if(produmoFacade.vigenciaEnDeposito(idDeposito,d.getIdProductos())|| !d.getIdProductos().getStock()) {
                                ProductoResponse sr = new ProductoResponse(d.getIdProductos().getIdProductos(),d.getIdProductos().getDescripcion(),d.getIdProductos().getCodProducto());
                                productosResponse.add(sr);
                            }
                        }
                    }*/
                    ProductoResponse sr = new ProductoResponse(d.getIdProductos().getIdProductos(),d.getIdProductos().getDescripcion(),d.getIdProductos().getCodProducto());
                    productosResponse.add(sr);
                }               
            } else if(tipo != null && tipo.equals("reducida") && idListaPrecio == null && idDeposito != null) {
                //Armo la respuesta

                for(Producto s : productos) {
                    //Filtro por existencia en deposito
                    //if((!s.getProdumoCollection().isEmpty()) || !s.getStock()) {
                     //   if(produmoFacade.vigenciaEnDeposito(idDeposito,s) || !s.getStock()) {
                            ProductoResponse sr = new ProductoResponse(s.getIdProductos(),s.getDescripcion(),s.getCodProducto());
                            productosResponse.add(sr);
                       // }
                   // }

                }               
            } else {
                for(Producto s : productos) {
                    ProductoResponse sr = new ProductoResponse(s);
                    sr.getModeloCab().agregarModeloDetalle(s.getIdModeloCab().getModeloDetalleCollection());
                    if(!s.getProdCultivoCollection().isEmpty()) {
                        sr.agregarCultivos(s.getProdCultivoCollection());
                    }                 
    //                if(!s.getLoteCollection().isEmpty()) {
    //                    sr.setEditar(false);
    //                }
                    productosResponse.add(sr);
                }
            }
            respuesta.setArraydatos(productosResponse);
            respuesta.setControl(AppCodigo.OK, "Lista de Productos");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setProducto(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            String codProducto = (String) Utils.getKeyFromJsonObject("codProducto", jsonBody, "String");
            String codigoBarra = (String) Utils.getKeyFromJsonObject("codigoBarra", jsonBody, "String");
            String descripcionCorta = (String) Utils.getKeyFromJsonObject("descripcionCorta", jsonBody, "String");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            Integer modeloImputacion = (Integer) Utils.getKeyFromJsonObject("modeloImputacion", jsonBody, "Integer");
            boolean aptoCanje = (boolean) Utils.getKeyFromJsonObject("aptoCanje", jsonBody, "boolean");
            boolean stock = (boolean) Utils.getKeyFromJsonObject("stock", jsonBody, "boolean");
            boolean trazable = (boolean) Utils.getKeyFromJsonObject("trazable", jsonBody, "boolean");
            boolean traReceta = (boolean) Utils.getKeyFromJsonObject("traReceta", jsonBody, "boolean");
            boolean traInforma = (boolean) Utils.getKeyFromJsonObject("traInforma", jsonBody, "boolean");
            String gtin = (String) Utils.getKeyFromJsonObject("gtin", jsonBody, "String");
            BigDecimal puntoPedido = (BigDecimal) Utils.getKeyFromJsonObject("puntoPedido", jsonBody, "BigDecimal");
            BigDecimal costoReposicion = (BigDecimal) Utils.getKeyFromJsonObject("costoReposicion", jsonBody, "BigDecimal");
            BigDecimal precioVentaProv = (BigDecimal) Utils.getKeyFromJsonObject("precioVentaProv", jsonBody, "BigDecimal");
            String observaciones = (String) Utils.getKeyFromJsonObject("observaciones", jsonBody, "String");
            Integer idSubRubro = (Integer) Utils.getKeyFromJsonObject("idSubRubro", jsonBody, "Integer");
            Integer idIva = (Integer) Utils.getKeyFromJsonObject("idIva", jsonBody, "Integer");
            Integer idUnidadCompra = (Integer) Utils.getKeyFromJsonObject("idUnidadCompra", jsonBody, "Integer");
            Integer idUnidadVenta = (Integer) Utils.getKeyFromJsonObject("idUnidadVenta", jsonBody, "Integer");
            Integer idMarca = (Integer) Utils.getKeyFromJsonObject("idMarca", jsonBody, "Integer");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            List<JsonElement> cultivos = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("cultivos", jsonBody, "ArrayList");
            
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

            if(costoReposicion == BigDecimal.ZERO) {
                respuesta.setControl(AppCodigo.ERROR, "Error, el costo de reposicion debe ser distinto de 0");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            //Me fijo que  descripcion, idRubro e idEmpresa no sean nulos
            if(codProducto == null || idSubRubro == null || idUnidadCompra == null || idUnidadVenta == null || idIva == null || idMoneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisMonedas moneda = sisMonedasFacade.find(idMoneda);
            if(moneda == null){
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe la moneda seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Me fijo si existe un producto con el mismo codigo y la misma empresa
            Producto prod = productoFacade.getByCodigoProdEmpresa(codProducto, user.getIdPerfil().getIdSucursal().getIdEmpresa());
            if(prod != null) {
                respuesta.setControl(AppCodigo.ERROR, "Ya existe ese producto con el mismo codigo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            List<Cultivo> listaCultivos = new ArrayList<>();
            if(cultivos != null && !cultivos.isEmpty()) {
                for(JsonElement j : cultivos) {
                    //Obtengo los atributos del body
                    Integer idCultivo = (Integer) Utils.getKeyFromJsonObject("idCultivo", j.getAsJsonObject(), "Integer");
                    
                    if(idCultivo == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Cultivo nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    Cultivo cultivo = cultivoFacade.find(idCultivo);
                    if(cultivo == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error cultivo no encontrado");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    listaCultivos.add(cultivo);
                }
            }
            
            SubRubro subrubro = subRubroFacade.find(idSubRubro);
            SisUnidad sisUniudadCompra = sisUnidadFacade.find(idUnidadCompra);
            SisUnidad sisUnidadVenta = sisUnidadFacade.find(idUnidadVenta);
            SisIVA sisIVA = sisIVAFacade.find(idIva);
            ModeloCab modeloCab = modeloCabFacade.find(modeloImputacion);
            Marca marca = null;
            if(idMarca != null) {
                marca = marcaFacade.find(idMarca);
            } 
            //Pregunto si existe el SubRubro
            if(subrubro == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el SubRubro");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe el SubRubro
            if(sisUniudadCompra == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Unidad de Compra");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe el SubRubro
            if(sisUnidadVenta == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Unidad de Venta");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe el SubRubro
            if(sisIVA == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el IVA");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe el SubRubro
            if(modeloCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el modelo de imputacion");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            try {
                boolean transaccion;
                Producto producto = new Producto();
                producto.setCodProducto(codProducto);
                producto.setCodigoBarra(codigoBarra);
                producto.setDescripcionCorta(descripcionCorta);
                producto.setDescripcion(descripcion);
                producto.setIdModeloCab(modeloCab);
                producto.setAptoCanje(aptoCanje);
                producto.setStock(stock);
                producto.setTraReceta(traReceta);
                producto.setTraInforma(traInforma);
                producto.setTrazable(trazable);
                producto.setGtin(gtin);
                producto.setCostoReposicion(costoReposicion);
                producto.setPrecioVentaProv(precioVentaProv);
                producto.setPuntoPedido(puntoPedido);
                producto.setObservaciones(observaciones);
                producto.setIdSubRubros(subrubro);
                producto.setIdIVA(sisIVA);
                producto.setUnidadCompra(sisUniudadCompra);
                producto.setUnidadVenta(sisUnidadVenta);
                producto.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                producto.setIdMarca(marca);
                producto.setIdMoneda(moneda);
                transaccion = productoFacade.setProductoNuevo(producto);
                if(!transaccion) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Producto, clave primaria repetida");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                if(!listaCultivos.isEmpty()) {
                    for(Cultivo c : listaCultivos) {
                        boolean transaccion2;
                        ProdCultivo prodCultivo = new ProdCultivo();
                        prodCultivo.setIdCultivo(c);
                        prodCultivo.setIdProductos(producto);
                        transaccion2 = prodCultivoFacade.setProdCultivoNuevo(prodCultivo);
                        if(!transaccion2) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la relacion Cultivo Producto:" + c.getDescripcion() + "y" + producto.getDescripcion());
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                    }
                }
                respuesta.setControl(AppCodigo.CREADO, "Producto creado con exito");
                return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
            } catch (Exception ex) { 
                respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)    
    public Response editProducto(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", jsonBody, "Integer");
            String codProducto = (String) Utils.getKeyFromJsonObject("codProducto", jsonBody, "String");
            String codigoBarra = (String) Utils.getKeyFromJsonObject("codigoBarra", jsonBody, "String");
            String descripcionCorta = (String) Utils.getKeyFromJsonObject("descripcionCorta", jsonBody, "String");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            Integer modeloImputacion = (Integer) Utils.getKeyFromJsonObject("modeloImputacion", jsonBody, "Integer");
            boolean aptoCanje = (boolean) Utils.getKeyFromJsonObject("aptoCanje", jsonBody, "boolean");
            boolean stock = (boolean) Utils.getKeyFromJsonObject("stock", jsonBody, "boolean");
            boolean trazable = (boolean) Utils.getKeyFromJsonObject("trazable", jsonBody, "boolean");
            boolean traReceta = (boolean) Utils.getKeyFromJsonObject("traReceta", jsonBody, "boolean");
            boolean traInforma = (boolean) Utils.getKeyFromJsonObject("traInforma", jsonBody, "boolean");
            String gtin = (String) Utils.getKeyFromJsonObject("gtin", jsonBody, "String");
            BigDecimal puntoPedido = (BigDecimal) Utils.getKeyFromJsonObject("puntoPedido", jsonBody, "BigDecimal");
            BigDecimal costoReposicion = (BigDecimal) Utils.getKeyFromJsonObject("costoReposicion", jsonBody, "BigDecimal");
            BigDecimal precioVentaProv = (BigDecimal) Utils.getKeyFromJsonObject("precioVentaProv", jsonBody, "BigDecimal");
            String observaciones = (String) Utils.getKeyFromJsonObject("observaciones", jsonBody, "String");
            Integer idSubRubro = (Integer) Utils.getKeyFromJsonObject("idSubRubro", jsonBody, "Integer");
            Integer idIva = (Integer) Utils.getKeyFromJsonObject("idIva", jsonBody, "Integer");
            Integer idUnidadCompra = (Integer) Utils.getKeyFromJsonObject("idUnidadCompra", jsonBody, "Integer");
            Integer idUnidadVenta = (Integer) Utils.getKeyFromJsonObject("idUnidadVenta", jsonBody, "Integer");
            Integer idMarca = (Integer) Utils.getKeyFromJsonObject("idMarca", jsonBody, "Integer");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            List<JsonElement> cultivos = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("cultivos", jsonBody, "ArrayList");
            
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
            if(codProducto == null || idSubRubro == null || idUnidadCompra == null || idUnidadVenta == null || idIva == null || idProducto == null || modeloImputacion == null || idMoneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, campos vacios");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            List<Cultivo> listaCultivos = new ArrayList<>();
            if(cultivos != null && !cultivos.isEmpty()) {
                for(JsonElement j : cultivos) {
                    //Obtengo los atributos del body
                    Integer idCultivo = (Integer) Utils.getKeyFromJsonObject("idCultivo", j.getAsJsonObject(), "Integer");
                    
                    if(idCultivo == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Cultivo nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    Cultivo cultivo = cultivoFacade.find(idCultivo);
                    if(cultivo == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error cultivo no encontrado");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    listaCultivos.add(cultivo);
                }
            }
            
            SisMonedas moneda = sisMonedasFacade.find(idMoneda);
            if(moneda == null){
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe la moneda seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            
            Producto producto = productoFacade.find(idProducto);
            SubRubro subrubro = subRubroFacade.find(idSubRubro);
            SisUnidad sisUniudadCompra = sisUnidadFacade.find(idUnidadCompra);
            SisUnidad sisUnidadVenta = sisUnidadFacade.find(idUnidadVenta);
            SisIVA sisIVA = sisIVAFacade.find(idIva); 
            ModeloCab modeloCab = modeloCabFacade.find(modeloImputacion);
            Marca marca = null;
            if(idMarca != null) {
                marca = marcaFacade.find(idMarca);
            } 
                        
            //Pregunto si existe el SubRubro
            if(subrubro == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el SubRubro");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe la Unidad de Compra
            if(sisUniudadCompra == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Unidad de Compra");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe la Unidad de Venta
            if(sisUnidadVenta == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Unidad de Venta");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe el IVA
            if(sisIVA == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el IVA");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si existe el SubRubro
            if(modeloCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el modelo de imputacion");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            
            //Pregunto si existe el Producto
            if(producto == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el Producto");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            producto.setCodProducto(codProducto);
            producto.setCodigoBarra(codigoBarra);
            producto.setDescripcionCorta(descripcionCorta);
            producto.setDescripcion(descripcion);
            producto.setIdModeloCab(modeloCab);
            producto.setAptoCanje(aptoCanje);
            producto.setStock(stock);
            producto.setTraReceta(traReceta);
            producto.setTraInforma(traInforma);
            producto.setTrazable(trazable);
            producto.setGtin(gtin);
            producto.setCostoReposicion(costoReposicion);
            producto.setPrecioVentaProv(precioVentaProv);
            producto.setPuntoPedido(puntoPedido);
            producto.setObservaciones(observaciones);
            producto.setIdSubRubros(subrubro);
            producto.setIdIVA(sisIVA);
            producto.setUnidadCompra(sisUniudadCompra);
            producto.setUnidadVenta(sisUnidadVenta);
            producto.setIdMarca(marca);
            producto.setIdMoneda(moneda);
            producto.getProdCultivoCollection().clear();
            transaccion = productoFacade.editProducto(producto);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el Producto, clave primaria repetida");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(!listaCultivos.isEmpty()) {
                    for(Cultivo c : listaCultivos) {
                        boolean transaccion2;
                        ProdCultivo prodCultivo = new ProdCultivo();
                        prodCultivo.setIdCultivo(c);
                        prodCultivo.setIdProductos(producto);
                        transaccion2 = prodCultivoFacade.editProdCultivo(prodCultivo);
                        if(!transaccion2) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo editar la relacion Cultivo Producto:" + c.getDescripcion() + "y" + producto.getDescripcion());
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                    }
            }
            respuesta.setControl(AppCodigo.GUARDADO, "Producto editado con exito");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @DELETE
    @Path ("/{idProducto}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteProducto(  
        @HeaderParam ("token") String token,
        @PathParam ("idProducto") int idProducto,
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
            if(idProducto == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            Producto producto = productoFacade.find(idProducto);
            
            //Pregunto si existe el Producto
            if(producto == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el Producto");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = productoFacade.deleteProducto(producto);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar el Producto");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.BORRADO, "Producto borrado con exito");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
    
}
