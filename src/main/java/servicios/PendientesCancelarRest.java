package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ModeloDetalleResponse;
import datos.Payload;
import datos.PendientesCancelarResponse;
import datos.ProductoResponse;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CteTipo;
import entidades.FactCab;
import entidades.FactDetalle;
import entidades.FactPie;
import entidades.ListaPrecio;
import entidades.ListaPrecioDet;
import entidades.ModeloDetalle;
import entidades.Parametro;
import entidades.ParametrosCanjes;
import entidades.ParametrosPesificado;
import entidades.Producto;
import entidades.SisCotDolar;
import entidades.SisModulo;
import entidades.SisMonedas;
import entidades.SisOperacionComprobante;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.CteTipoFacade;
import persistencia.FactCabFacade;
import persistencia.ListaPrecioDetFacade;
import persistencia.ListaPrecioFacade;
import persistencia.ParametroFacade;
import persistencia.ParametrosCanjesFacade;
import persistencia.ParametrosPesificadoFacade;
import persistencia.ProductoFacade;
import persistencia.SisCotDolarFacade;
import persistencia.SisModuloFacade;
import persistencia.SisMonedasFacade;
import persistencia.SisOperacionComprobanteFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Dario Quiroga
 */

@Stateless
@Path("buscaPendientes")
public class PendientesCancelarRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils; 
    @Inject ProductoFacade productoFacade;
    @Inject SisModuloFacade sisModuloFacade;
    @Inject ListaPrecioFacade listaPrecioFacade;
    @Inject SisMonedasFacade sisMonedasFacade;
    @Inject SisCotDolarFacade sisCotDolarFacade;
    @Inject SisOperacionComprobanteFacade sisOperacionComprobanteFacade;
    @Inject FactCabFacade factCabFacade;
    @Inject CteTipoFacade cteTipoFacade;
    @Inject ParametroFacade parametroFacade;
    @Inject ParametrosCanjesFacade parametrosCanjesFacade;
    @Inject ListaPrecioDetFacade listaPreciosDetFacade;
    @Inject ParametrosPesificadoFacade parametrosPesificadoFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendientesCancelar(  
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer cteTipo = (Integer) Utils.getKeyFromJsonObject("cteTipo", jsonBody, "Integer");
            BigDecimal facNumero = (BigDecimal) Utils.getKeyFromJsonObject("facNumero", jsonBody, "BigDecimal");
            Integer codigoProv = (Integer) Utils.getKeyFromJsonObject("codigoProv", jsonBody, "Integer");
            Integer pendiente = (Integer) Utils.getKeyFromJsonObject("pendiente", jsonBody, "Integer");
            Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", jsonBody, "Integer");
            Integer idDeposito = (Integer) Utils.getKeyFromJsonObject("idDeposito", jsonBody, "Integer");
            String despacho = (String) Utils.getKeyFromJsonObject("despacho", jsonBody, "String");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            Integer idSisOperacionComprobante = (Integer) Utils.getKeyFromJsonObject("idSisOperacionComprobante", jsonBody, "Integer");
            Integer idSisTipoOperacion = (Integer) Utils.getKeyFromJsonObject("idSisTipoOperacion", jsonBody, "Integer");
            String letra = (String) Utils.getKeyFromJsonObject("letra", jsonBody, "String");
            Integer idListaPrecio = (Integer) Utils.getKeyFromJsonObject("idListaPrecio", jsonBody, "Integer");
            Integer modulo = (Integer) Utils.getKeyFromJsonObject("modulo", jsonBody, "Integer");
            String diferenciaFechas = (String) Utils.getKeyFromJsonObject("diferenciaFechas", jsonBody, "String");
            String codigoCereal = (String) Utils.getKeyFromJsonObject("codigoCereal", jsonBody, "String");
            Boolean esCanje = (Boolean) Utils.getKeyFromJsonObject("esCanje", jsonBody, "boolean");
            Boolean esPesificado = (Boolean) Utils.getKeyFromJsonObject("esPesificado", jsonBody, "boolean");
            
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
            
            //valido que el Usuario no sea null
            if(cteTipo == null) {
                respuesta.setControl(AppCodigo.ERROR, "Debe seleccionar un Tipo de Comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idMoneda == null) {
                idMoneda = 1;
            }
            SisMonedas moneda = sisMonedasFacade.find(idMoneda);
            if(moneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no eciste la moneda seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(modulo == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, modulo nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idListaPrecio == null) {
                idListaPrecio = 0;
            }
            
            if(esCanje == null) {
                esCanje = false;
            }
            
            if(esPesificado == null) {
                esPesificado = false;
            }
                   
            //seteo el nombre del store
            String noombreSP = "call s_comprobantesPendientes(?,?,?,?,?,?,?,?,?,?,?)";
            
            //invoco al store
            CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, noombreSP);
            
            //valido que el Procedimiento Almacenado no sea null
            if(callableStatement == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatement.setInt(2,cteTipo);
            callableStatement.setBigDecimal(3, facNumero);
            callableStatement.setInt(4, codigoProv);
            callableStatement.setInt(5, pendiente);
            callableStatement.setInt(6, idProducto);
            callableStatement.setInt(7,idDeposito);
            callableStatement.setString(8,despacho);
            callableStatement.setInt(9,idMoneda);
            callableStatement.setInt(10,idSisTipoOperacion);
            callableStatement.setInt(11,idListaPrecio);
            
            ResultSet rs = callableStatement.executeQuery();
            List<Payload> pendientes = new ArrayList<>();
                while (rs.next()) {
                    if(!rs.getBoolean("autorizado") && modulo == 2) {
                        continue;
                    }
                    
                    Producto prod = productoFacade.find(rs.getInt("idProductos"));
                    if(prod == null) {
                        break;
                    }
                    
                    ProductoResponse producto = new ProductoResponse(prod);
                    if(moneda.getDescripcion().equals("$AR") && rs.getString("moneda").equals("u$s")) {
                        producto.setCostoReposicion(producto.getCostoReposicion().multiply(rs.getBigDecimal("dolar")));
                    } else if((moneda.getDescripcion().equals("u$s") && rs.getString("moneda").equals("$AR"))) {
                        producto.setCostoReposicion(producto.getCostoReposicion().divide(rs.getBigDecimal("dolar"),2, RoundingMode.HALF_UP));
                    }
                    
                    producto.getModeloCab().agregarModeloDetalleImputacion(prod.getIdModeloCab().getModeloDetalleCollection(),rs.getString("imputacion") );
                    PendientesCancelarResponse pendientesCancelar = new PendientesCancelarResponse(
                            rs.getString("comprobante"),
                            rs.getString("numero"),
                            rs.getBigDecimal("original"),
                            rs.getBigDecimal("pendiente"),
                            rs.getBigDecimal("precio"),
                            rs.getBigDecimal("dolar"),
                            rs.getString("moneda"),
                            rs.getBigDecimal("porCalc"),
                            rs.getBigDecimal("ivaPorc"),
                            rs.getInt("deposito"),
                            rs.getInt("idFactDetalleImputa"),
                            rs.getInt("idFactCabImputa"),
                            rs.getBigDecimal("descuento"),
                            rs.getString("tipoDescuento"),
                            rs.getBigDecimal("cantBultos"),
                            rs.getString("despacho"),
                            rs.getString("observaciones"),
                            rs.getInt("itemImputa"),
                            rs.getBigDecimal("importe"),
                            rs.getInt("idListaPrecios"),
                            rs.getString("condiciones"),
                            rs.getString("letra"),
                            rs.getInt("idFactDetalle"),
                            producto);
                    if(idListaPrecio != null && idListaPrecio != 0) {
                        ListaPrecioDet det = listaPreciosDetFacade.getListaPrecioDet(pendientesCancelar.getIdListaPrecio(), pendientesCancelar.getProducto().getIdProductos());
                        pendientesCancelar.setCotaInferior(det.getCotaInf());
                        pendientesCancelar.setCotaSuperior(det.getCotaSup());
                    }
                    if(idSisTipoOperacion != null && esCanje && cteTipo != null && diferenciaFechas != null) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate vencimientoDate = LocalDate.parse(diferenciaFechas, dtf);
                        LocalDate currentDate = LocalDate.now();
                        long daysBetween = Duration.between(currentDate.atStartOfDay(), vencimientoDate.atStartOfDay()).toDays();
                        Integer diasPorMedio = new Long(daysBetween).intValue();
                        ParametrosCanjes params = parametrosCanjesFacade.findParametrosCanjes(2, codigoCereal);
                        Integer diasTotales = 0;
                        BigDecimal recargo = BigDecimal.ZERO;
                        BigDecimal interesDiario = BigDecimal.ZERO;
                        Integer diasLibres = 0;
                        if(params != null) {
                            diasLibres = new Short(params.getDiasLIbres()).intValue();
                        }
                        if(diasPorMedio > diasLibres) {
                            diasTotales = diasPorMedio - diasLibres;
                            if(params != null) {
                                interesDiario = params.getInteresDiario();
                                recargo = interesDiario.multiply(new BigDecimal(diasTotales));
                            }
                            BigDecimal nuevoPrecio = BigDecimal.ZERO;
                            if(rs.getString("moneda").equals("u$s")) {
                                nuevoPrecio = pendientesCancelar.getPrecio().add(pendientesCancelar.getPrecio().multiply(recargo).divide(new BigDecimal(100)));
                                pendientesCancelar.setDiferenciaPrecio(pendientesCancelar.getPrecio());
                                pendientesCancelar.setDiasLibres(diasLibres);
                                pendientesCancelar.setDiasResultantes(diasTotales);
                                pendientesCancelar.setRecargoTotal(recargo);
                                pendientesCancelar.setPrecio(nuevoPrecio);
                                pendientesCancelar.setRecargo(interesDiario);
                            } else {
                                nuevoPrecio = pendientesCancelar.getPrecio().divide(rs.getBigDecimal("dolar"),2, RoundingMode.HALF_UP).add(pendientesCancelar.getPrecio().divide(rs.getBigDecimal("dolar"),2, RoundingMode.HALF_UP).multiply(recargo).divide(new BigDecimal(100))).multiply(rs.getBigDecimal("dolar"));
                                pendientesCancelar.setDiferenciaPrecio(pendientesCancelar.getPrecio());
                                pendientesCancelar.setDiasLibres(diasLibres);
                                pendientesCancelar.setDiasResultantes(diasTotales);
                                pendientesCancelar.setRecargoTotal(recargo);
                                pendientesCancelar.setPrecio(nuevoPrecio);
                                pendientesCancelar.setRecargo(interesDiario);
                            }
                        }
                    }
                    if(idSisTipoOperacion != null && esPesificado && cteTipo != null && diferenciaFechas != null) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate vencimientoDate = LocalDate.parse(diferenciaFechas, dtf);
                        LocalDate currentDate = LocalDate.now();
                        long daysBetween = Duration.between(currentDate.atStartOfDay(), vencimientoDate.atStartOfDay()).toDays();
                        Integer diasPorMedio = new Long(daysBetween).intValue();
                        ParametrosPesificado params = parametrosPesificadoFacade.getParametro();
                        Integer diasTotales = diasPorMedio;
                        BigDecimal recargo = BigDecimal.ZERO;
                        BigDecimal interesDiario = BigDecimal.ZERO;
                        Integer diasLibres = 0;
                        if(params != null) {
                            interesDiario = params.getInteresMensual().multiply(new BigDecimal(12)).divide(new BigDecimal(360), 4, RoundingMode.HALF_UP);
                            recargo = interesDiario.multiply(new BigDecimal(diasTotales));
                        }
                        BigDecimal nuevoPrecio = BigDecimal.ZERO;
                        if(rs.getString("moneda").equals("u$s")) {
                            nuevoPrecio = pendientesCancelar.getPrecio().multiply(rs.getBigDecimal("dolar")).add(pendientesCancelar.getPrecio().multiply(rs.getBigDecimal("dolar")).multiply(recargo).divide(new BigDecimal(100))).divide(rs.getBigDecimal("dolar"),2, RoundingMode.HALF_UP);
                            pendientesCancelar.setDiferenciaPrecio(pendientesCancelar.getPrecio());
                            pendientesCancelar.setDiasLibres(diasLibres);
                            pendientesCancelar.setDiasResultantes(diasTotales);
                            pendientesCancelar.setRecargoTotal(recargo);
                            pendientesCancelar.setPrecio(nuevoPrecio);
                            pendientesCancelar.setRecargo(interesDiario);
                        } else {
                            nuevoPrecio = pendientesCancelar.getPrecio().add(pendientesCancelar.getPrecio().multiply(recargo).divide(new BigDecimal(100)));
                            pendientesCancelar.setDiferenciaPrecio(pendientesCancelar.getPrecio());
                            pendientesCancelar.setDiasLibres(diasLibres);
                            pendientesCancelar.setDiasResultantes(diasTotales);
                            pendientesCancelar.setRecargoTotal(recargo);
                            pendientesCancelar.setPrecio(nuevoPrecio);
                            pendientesCancelar.setRecargo(interesDiario);
                        }
                        
                    }
                    if(moneda.getDescripcion().equals("$AR") && rs.getString("moneda").equals("u$s")) {
                        pendientesCancelar.setPrecio(pendientesCancelar.getPrecio().multiply(rs.getBigDecimal("dolar")));
                        if(pendientesCancelar.getDiferenciaPrecio() != null) {
                            pendientesCancelar.setDiferenciaPrecio(pendientesCancelar.getDiferenciaPrecio().multiply(rs.getBigDecimal("dolar")));
                        }
                        pendientesCancelar.setImporte(pendientesCancelar.getPrecio().multiply(pendientesCancelar.getPendiente()));
                    } else if((moneda.getDescripcion().equals("u$s") && rs.getString("moneda").equals("$AR"))) {
                        pendientesCancelar.setPrecio(pendientesCancelar.getPrecio().divide(rs.getBigDecimal("dolar"),2, RoundingMode.HALF_UP));
                        if(pendientesCancelar.getDiferenciaPrecio() != null) {
                            pendientesCancelar.setDiferenciaPrecio(pendientesCancelar.getDiferenciaPrecio().divide(rs.getBigDecimal("dolar"),2, RoundingMode.HALF_UP));
                        }
                        pendientesCancelar.setImporte(pendientesCancelar.getPrecio().multiply(pendientesCancelar.getPendiente()));
                    }
                    pendientes.add(pendientesCancelar);
                }
            //Cierro la conexion    
            callableStatement.getConnection().close();
          
            CteTipo cteTipoSeleccionado = cteTipoFacade.find(cteTipo);
            if(cteTipoSeleccionado == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el CteTipo seleccionado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idSisTipoOperacion != 0 && idSisTipoOperacion != null) {
                if(idSisOperacionComprobante == null) {
                    idSisOperacionComprobante = sisOperacionComprobanteFacade.findIdByEmpresaSisCompSisOp(user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa(), cteTipoSeleccionado.getIdSisComprobante().getIdSisComprobantes(),idSisTipoOperacion);
                }
            
                SisOperacionComprobante operacionComp = sisOperacionComprobanteFacade.find(idSisOperacionComprobante);
                if(operacionComp == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe la relacion para el comprobante seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            
                if(pendientes.isEmpty() && !operacionComp.getDifCotizacion()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay Comprobantes Pendientes");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                } else if(operacionComp.getDifCotizacion() && facNumero != null) {
                    if(!pendientes.isEmpty()) {
                        pendientes.clear();
                    }
                    //Aca fijarse para devolver only por sucursal no empresa!!!!
                    FactCab factCab = factCabFacade.getByNumeroEmpresa(facNumero, cteTipoSeleccionado,user.getIdPerfil().getIdSucursal().getIdEmpresa(),letra);
                    if(factCab == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No existe el comprobante, por favor corrobore el nro ingresado");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();

                    BigDecimal total = new BigDecimal(0);
                    //Sumo los totales de los productos
                    for(FactDetalle d : factCab.getFactDetalleCollection()) {
                        total = total.add(d.getImporte());
                    }
                    //Sumo los totales del pie del comprobante
                    for(FactPie p : factCab.getFactPieCollection()) {
                        total = total.add(p.getImporte());
                    }

                    BigDecimal diferencia = new BigDecimal(0);
                    if(factCab.getCotDolar().compareTo(cotDolar.getCotizacion()) < 0) {
                        //Nota de devito, subio el dolar :(
                        if(factCab.getIdmoneda().getDescripcion().equals("$AR")) {
                            diferencia = total.divide(factCab.getCotDolar(),2, RoundingMode.HALF_UP);
                            diferencia = diferencia.subtract(total.divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP));
                            diferencia = diferencia.multiply(cotDolar.getCotizacion());
                        } else if(factCab.getIdmoneda().getDescripcion().equals("u$s")) {
                            diferencia = total.multiply(cotDolar.getCotizacion());
                            diferencia = diferencia.subtract(total.multiply(factCab.getCotDolar()));
                        }
                    } else if(factCab.getCotDolar().compareTo(cotDolar.getCotizacion()) > 0) {
                        //Nota de credito, bajo el dolar!!
                        if(factCab.getIdmoneda().getDescripcion().equals("$AR")) {
                            diferencia = total.divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP);
                            diferencia = diferencia.subtract(total.divide(factCab.getCotDolar(),2, RoundingMode.HALF_UP));
                            diferencia = diferencia.multiply(cotDolar.getCotizacion());
                        } else if(factCab.getIdmoneda().getDescripcion().equals("u$s")) {
                            diferencia = total.multiply(cotDolar.getCotizacion());
                            diferencia = diferencia.subtract(total.multiply(factCab.getCotDolar()));
                        }
                    } else if(factCab.getCotDolar().compareTo(cotDolar.getCotizacion()) == 0) {
                        //El dolar se mantuvo estable
                        diferencia = BigDecimal.ZERO;
                    }

                    Parametro parametro = parametroFacade.getParametro("difCotDolar", user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                    if(parametro == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No existe el parametro para la bussqueda del prod: Dif. Cotizacion Dolar");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    Producto prod = productoFacade.getByCodigoProdEmpresa(parametro.getValor(), user.getIdPerfil().getIdSucursal().getIdEmpresa());              
                    if(prod == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No existe el producto Dif.Cotizacion Dolar");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    //Armo la respuesta con el producto dif cot dolar
                    ProductoResponse producto = new ProductoResponse(prod);
                    producto.setCostoReposicion(diferencia);

                    //Agrego las cuentas contables
                    producto.getModeloCab().agregarModeloDetalleTipo(prod.getIdModeloCab().getModeloDetalleCollection(), 1, factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos());
                    if(!prod.getLoteCollection().isEmpty()) {
                        producto.setEditar(false);
                    }
                    //Seteo el iva del producto dependiendo la cuenta contable que tiene asignada
                    for(ModeloDetalle r : prod.getIdModeloCab().getModeloDetalleCollection()) {
                        if(r.getIdSisModulo().getIdSisModulos().equals(factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos()) && 
                            r.getValor().compareTo(BigDecimal.ZERO) != 0 &&
                            r.getIdSisTipoModelo().getIdSisTipoModelo().equals(2)) {
                            producto.getIVA().setPorcIVA(r.getValor());
                            break;
                        }
                    }

                    PendientesCancelarResponse pendientesCancelar = new PendientesCancelarResponse(producto);
                    pendientesCancelar.setPrecio(diferencia);
                    pendientesCancelar.setPendiente(BigDecimal.ONE);
                    pendientes.add(pendientesCancelar);      
                }
            }                        
            respuesta.setArraydatos(pendientes);
            respuesta.setControl(AppCodigo.OK, "Lista de Comprobantes Pendientes");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al buscar los pendientes: " + e.getMessage());
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path ("/{idProducto}")
    public Response getProductos(  
        @HeaderParam ("token") String token,
        @QueryParam ("idSisTipoModelo") Integer idSisTipoModelo,
        @QueryParam ("modulo") Integer idModulo,
        @QueryParam ("listaPrecio") Integer idListaPrecios,
        @QueryParam ("idMoneda") Integer idMoneda,
        @QueryParam ("idCteTipo") Integer idCteTipo,
        @QueryParam ("idSisTipoOperacion") Integer idSisTipoOperacion,
        @QueryParam ("diferenciaFechas") String diferenciaFechas,
        @QueryParam ("esCanje") Boolean esCanje,
        @QueryParam ("esPesificado") Boolean esPesificado,
        @QueryParam ("codigoCereal") String codigoCereal,
        @PathParam ("idProducto") Integer idProducto,
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
            
            if(esCanje == null) {
                esCanje = false;
            }
            
            if(esPesificado == null) {
                esPesificado = false;
            }
            
            List<Payload> productosResponse = new ArrayList<>();
            
            if(idModulo != null && idSisTipoModelo != null && idProducto != null &&  idListaPrecios == null && idMoneda != null && !esCanje) {
                //Busco el modulo(Por lo general en este if viene el de compras)
                SisModulo modulo = sisModuloFacade.find(idModulo);
                if(modulo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                SisMonedas moneda = sisMonedasFacade.find(idMoneda);
                if(moneda == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no la moneda");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el producto
                Producto s = productoFacade.find(idProducto);                
                if(s == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el producto");
                    return Response.status(Response.Status.NOT_FOUND).entity(respuesta.toJson()).build();
                }              

                //Armo la respuesta
                ProductoResponse sr = new ProductoResponse(s);                
                
                //Calculo el cambio de moneda de acuerdo a la moneda seleccionada y a la moneda asignada en el producto
                if(moneda.getDescripcion().equals("u$s") && s.getIdMoneda().getDescripcion().equals("$AR")) {
                    SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                    sr.setCostoReposicion(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP));
                } else if(moneda.getDescripcion().equals("$AR") && s.getIdMoneda().getDescripcion().equals("u$s")) {
                    SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                    sr.setCostoReposicion(cotDolar.getCotizacion().multiply(sr.getCostoReposicion()));
                }
                
                //Agrego las cuentas contables
                sr.getModeloCab().agregarModeloDetalleTipo(s.getIdModeloCab().getModeloDetalleCollection(), idSisTipoModelo, idModulo);
                
                //Si tiene un lote asignado no se puede editar
                if(!s.getLoteCollection().isEmpty()) {
                    sr.setEditar(false);
                }
                
                //Seteo el iva del producto dependiendo la cuenta contable que tiene asignada
                for(ModeloDetalle r : s.getIdModeloCab().getModeloDetalleCollection()) {
                    if(r.getIdSisModulo().getIdSisModulos().equals(modulo.getIdSisModulos()) && 
                        r.getValor().compareTo(BigDecimal.ZERO) != 0 &&
                        r.getIdSisTipoModelo().getIdSisTipoModelo().equals(2)) {
                        sr.getIVA().setPorcIVA(r.getValor());
                        break;
                    }
                }
                
                //Armo la respuesta de pendientes de cancelar para que sea la misma que viene del store procedure
                PendientesCancelarResponse pr = new PendientesCancelarResponse(sr);
                pr.setPrecio(sr.getCostoReposicion());
                
                productosResponse.add(pr);               
            } else if(idListaPrecios != null && idModulo != null && idSisTipoModelo != null && idProducto != null && idMoneda != null && idSisTipoOperacion != null && idCteTipo != null && diferenciaFechas != null && esCanje) { 
            
                ListaPrecio listaPrecio = listaPrecioFacade.find(idListaPrecios);
                if(listaPrecio == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay lista de precios disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el modulo. (Por lo general en este if va a ser el de ventas)
                SisModulo modulo = sisModuloFacade.find(idModulo);
                if(modulo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                SisMonedas moneda = sisMonedasFacade.find(idMoneda);
                if(moneda == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no la moneda");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el producto
                Producto prod = productoFacade.find(idProducto);
                if(prod == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el producto seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate vencimientoDate = LocalDate.parse(diferenciaFechas, dtf);
                LocalDate currentDate = LocalDate.now();
                long daysBetween = Duration.between(currentDate.atStartOfDay(), vencimientoDate.atStartOfDay()).toDays();
                Integer diasPorMedio = new Long(daysBetween).intValue();
                System.out.println("Dias de por medio ->" + diasPorMedio);
                
                for(ListaPrecioDet l : listaPrecio.getListaPrecioDetCollection()) {
                    //Si no es el producto que continue
                    if(!l.getIdProductos().equals(prod)) {
                        continue;
                    }                    
                    //Armo el producto response
                    ProductoResponse sr = new ProductoResponse(l.getIdProductos());
                    //Seteo el costo en costo de repocicion el de la lista de precio
                    sr.setCostoReposicion(l.getPrecio());
                    //Agrego los detalles de cuentas contables
                    sr.getModeloCab().agregarModeloDetalleTipo(l.getIdProductos().getIdModeloCab().getModeloDetalleCollection(), idSisTipoModelo, idModulo);
                    //Seteo el iva del producto dependiendo del detalle si es != de 0 seteo el del detalle sino el del producto
                    for(ModeloDetalle r : prod.getIdModeloCab().getModeloDetalleCollection()) {
                        if(r.getIdSisModulo().getIdSisModulos().equals(modulo.getIdSisModulos()) && 
                           r.getValor().compareTo(BigDecimal.ZERO) != 0 &&
                           r.getIdSisTipoModelo().getIdSisTipoModelo().equals(2)) {
                            sr.getIVA().setPorcIVA(r.getValor());
                            break;
                        }
                    }
                    //Armo la respuesta de pendientes de cancelar asi es igual a la del store procedure
                    PendientesCancelarResponse pr = new PendientesCancelarResponse(sr);
                    //seteo el precio de la lista de precios seleccionada
                    if(moneda.getDescripcion().equals("u$s") && l.getIdListaPrecios().getIdMoneda().getDescripcion().equals("$AR")) {
                        SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                        ParametrosCanjes params = parametrosCanjesFacade.findParametrosCanjes(2, codigoCereal);
                        Integer diasTotales = 0;
                        BigDecimal recargo = BigDecimal.ZERO;
                        Integer diasLibres = new Short(params.getDiasLIbres()).intValue();
                        if(diasPorMedio > diasLibres) {
                            diasTotales = diasPorMedio - diasLibres;
                            pr.setDiasLibres(diasLibres);
                            pr.setDiasResultantes(diasTotales);
                            pr.setRecargo(params.getInteresDiario());
                            recargo = params.getInteresDiario().multiply(new BigDecimal(diasTotales));
                            pr.setRecargoTotal(recargo);
                            BigDecimal nuevoPrecio = sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP).add(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP).multiply(recargo).divide(new BigDecimal(100)));
                            pr.setDiferenciaPrecio(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP));
                            pr.setPrecio(nuevoPrecio);
                        } else {
                            pr.setPrecio(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP));
                        }
                    } else if(moneda.getDescripcion().equals("$AR") && l.getIdListaPrecios().getIdMoneda().getDescripcion().equals("u$s")){
                       SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                       ParametrosCanjes params = parametrosCanjesFacade.findParametrosCanjes(2, codigoCereal);
                       Integer diasTotales = 0;
                       BigDecimal recargo = BigDecimal.ZERO;
                       Integer diasLibres = new Short(params.getDiasLIbres()).intValue();
                       if(diasPorMedio > diasLibres) {
                           diasTotales = diasPorMedio - diasLibres;
                           pr.setDiasLibres(diasLibres);
                           pr.setDiasResultantes(diasTotales);
                           pr.setRecargo(params.getInteresDiario());
                           recargo = params.getInteresDiario().multiply(new BigDecimal(diasTotales));
                           pr.setRecargoTotal(recargo);
                           BigDecimal nuevoPrecio = sr.getCostoReposicion().add(sr.getCostoReposicion().multiply(recargo).divide(new BigDecimal(100))).multiply(cotDolar.getCotizacion());
                           pr.setDiferenciaPrecio(sr.getCostoReposicion().multiply(cotDolar.getCotizacion()));
                           pr.setPrecio(nuevoPrecio);
                        } else {
                           pr.setPrecio(sr.getCostoReposicion().multiply(cotDolar.getCotizacion()));
                       }
                    } else {
                        SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                        ParametrosCanjes params = parametrosCanjesFacade.findParametrosCanjes(2, codigoCereal);
                        Integer diasTotales = 0;
                        BigDecimal recargo = BigDecimal.ZERO;
                        System.out.println("dias libres ---->" + params.getDiasLIbres());
                        Integer diasLibres = new Short(params.getDiasLIbres()).intValue();
                        if(diasPorMedio > diasLibres) {
                            diasTotales = diasPorMedio - diasLibres;
                            pr.setDiasLibres(diasLibres);
                            pr.setDiasResultantes(diasTotales);
                            pr.setRecargo(params.getInteresDiario());
                            recargo = params.getInteresDiario().multiply(new BigDecimal(diasTotales));
                            pr.setRecargoTotal(recargo);
                            if(moneda.getDescripcion().equals("$AR")) {
                                BigDecimal nuevoPrecio = sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP).add(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP).multiply(recargo).divide(new BigDecimal(100))).multiply(cotDolar.getCotizacion());
                                pr.setDiferenciaPrecio(sr.getCostoReposicion());
                                pr.setPrecio(nuevoPrecio);
                            } else {
                                BigDecimal nuevoPrecio = sr.getCostoReposicion().add(sr.getCostoReposicion().multiply(recargo).divide(new BigDecimal(100)));
                                pr.setPrecio(sr.getCostoReposicion());
                            }
                         } else {
                            pr.setPrecio(sr.getCostoReposicion());
                        }
                    }
                    productosResponse.add(pr);
                }
                
            } else if(idListaPrecios != null && idModulo != null && idSisTipoModelo != null && idProducto != null && idMoneda != null && idSisTipoOperacion != null && idCteTipo != null && diferenciaFechas != null && esPesificado) { 
            
                ListaPrecio listaPrecio = listaPrecioFacade.find(idListaPrecios);
                if(listaPrecio == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay lista de precios disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el modulo. (Por lo general en este if va a ser el de ventas)
                SisModulo modulo = sisModuloFacade.find(idModulo);
                if(modulo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                SisMonedas moneda = sisMonedasFacade.find(idMoneda);
                if(moneda == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no la moneda");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el producto
                Producto prod = productoFacade.find(idProducto);
                if(prod == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el producto seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate vencimientoDate = LocalDate.parse(diferenciaFechas, dtf);
                LocalDate currentDate = LocalDate.now();
                long daysBetween = Duration.between(currentDate.atStartOfDay(), vencimientoDate.atStartOfDay()).toDays();
                Integer diasPorMedio = new Long(daysBetween).intValue();
                System.out.println("Dias de por medio ->" + diasPorMedio);
                
                for(ListaPrecioDet l : listaPrecio.getListaPrecioDetCollection()) {
                    //Si no es el producto que continue
                    if(!l.getIdProductos().equals(prod)) {
                        continue;
                    }                    
                    //Armo el producto response
                    ProductoResponse sr = new ProductoResponse(l.getIdProductos());
                    //Seteo el costo en costo de repocicion el de la lista de precio
                    sr.setCostoReposicion(l.getPrecio());
                    //Agrego los detalles de cuentas contables
                    sr.getModeloCab().agregarModeloDetalleTipo(l.getIdProductos().getIdModeloCab().getModeloDetalleCollection(), idSisTipoModelo, idModulo);
                    //Seteo el iva del producto dependiendo del detalle si es != de 0 seteo el del detalle sino el del producto
                    for(ModeloDetalle r : prod.getIdModeloCab().getModeloDetalleCollection()) {
                        if(r.getIdSisModulo().getIdSisModulos().equals(modulo.getIdSisModulos()) && 
                           r.getValor().compareTo(BigDecimal.ZERO) != 0 &&
                           r.getIdSisTipoModelo().getIdSisTipoModelo().equals(2)) {
                            sr.getIVA().setPorcIVA(r.getValor());
                            break;
                        }
                    }
                    //Armo la respuesta de pendientes de cancelar asi es igual a la del store procedure
                    PendientesCancelarResponse pr = new PendientesCancelarResponse(sr);
                    //seteo el precio de la lista de precios seleccionada
                    if(moneda.getDescripcion().equals("u$s") && l.getIdListaPrecios().getIdMoneda().getDescripcion().equals("$AR")) {
                        SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                        ParametrosPesificado params = parametrosPesificadoFacade.getParametro();
                        Integer diasTotales = 0;
                        BigDecimal recargo = BigDecimal.ZERO;
                        Integer diasLibres = 0;
                        if(diasPorMedio > diasLibres) {
                            diasTotales = diasPorMedio - diasLibres;
                            pr.setDiasLibres(diasLibres);
                            pr.setDiasResultantes(diasTotales);
                            pr.setRecargo(params.getInteresMensual());
                            recargo = params.getInteresMensual().multiply(new BigDecimal(12)).divide(new BigDecimal(360), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(diasTotales));
                            pr.setRecargoTotal(recargo);
                            BigDecimal nuevoPrecio = sr.getCostoReposicion().add(sr.getCostoReposicion().multiply(recargo).divide(new BigDecimal(100))).divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP);
                            pr.setDiferenciaPrecio(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP));
                            pr.setPrecio(nuevoPrecio);
                        } else {
                            pr.setPrecio(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP));
                        }
                    } else if(moneda.getDescripcion().equals("$AR") && l.getIdListaPrecios().getIdMoneda().getDescripcion().equals("u$s")){
                       SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                       ParametrosPesificado params = parametrosPesificadoFacade.getParametro();
                       Integer diasTotales = 0;
                       BigDecimal recargo = BigDecimal.ZERO;
                       Integer diasLibres = 0;
                       if(diasPorMedio > diasLibres) {
                           diasTotales = diasPorMedio - diasLibres;
                           pr.setDiasLibres(diasLibres);
                           pr.setDiasResultantes(diasTotales);
                           pr.setRecargo(params.getInteresMensual());
                           recargo = params.getInteresMensual().multiply(new BigDecimal(12)).divide(new BigDecimal(360), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(diasTotales));
                           pr.setRecargoTotal(recargo);
                           BigDecimal nuevoPrecio = sr.getCostoReposicion().multiply(cotDolar.getCotizacion()).add(sr.getCostoReposicion().multiply(cotDolar.getCotizacion()).multiply(recargo).divide(new BigDecimal(100)));
                           pr.setDiferenciaPrecio(sr.getCostoReposicion().multiply(cotDolar.getCotizacion()));
                           pr.setPrecio(nuevoPrecio);
                        } else {
                           pr.setPrecio(sr.getCostoReposicion().multiply(cotDolar.getCotizacion()));
                       }
                    } else {
                        SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                        ParametrosPesificado params = parametrosPesificadoFacade.getParametro();
                        Integer diasTotales = 0;
                        BigDecimal recargo = BigDecimal.ZERO;
                        Integer diasLibres = 0;
                        if(diasPorMedio > diasLibres) {
                            diasTotales = diasPorMedio - diasLibres;
                            pr.setDiasLibres(diasLibres);
                            pr.setDiasResultantes(diasTotales);
                            pr.setRecargo(params.getInteresMensual());
                            recargo = params.getInteresMensual().multiply(new BigDecimal(12)).divide(new BigDecimal(360), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(diasTotales));
                            pr.setRecargoTotal(recargo);
                            if(moneda.getDescripcion().equals("$AR")) {
                                BigDecimal nuevoPrecio = sr.getCostoReposicion().add(sr.getCostoReposicion().multiply(recargo).divide(new BigDecimal(100)));
                                pr.setDiferenciaPrecio(sr.getCostoReposicion());
                                pr.setPrecio(nuevoPrecio);
                            } else {
                                BigDecimal nuevoPrecio = sr.getCostoReposicion().multiply(cotDolar.getCotizacion()).add(sr.getCostoReposicion().multiply(cotDolar.getCotizacion()).multiply(recargo).divide(new BigDecimal(100))).divide(cotDolar.getCotizacion(), 2, RoundingMode.HALF_UP);
                                pr.setPrecio(sr.getCostoReposicion());
                            }
                         } else {
                            pr.setPrecio(sr.getCostoReposicion());
                        }
                    }
                    productosResponse.add(pr);
                }
                
            } else if(idListaPrecios != null && idModulo != null && idSisTipoModelo != null && idProducto != null && idMoneda != null) {
                //Busco la lista de precios seleccionada
                ListaPrecio listaPrecio = listaPrecioFacade.find(idListaPrecios);
                if(listaPrecio == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay lista de precios disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el modulo. (Por lo general en este if va a ser el de ventas)
                SisModulo modulo = sisModuloFacade.find(idModulo);
                if(modulo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                SisMonedas moneda = sisMonedasFacade.find(idMoneda);
                if(moneda == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no la moneda");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el producto
                Producto prod = productoFacade.find(idProducto);
                if(prod == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el producto seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Busco el producto en la lista de precios
                for(ListaPrecioDet l : listaPrecio.getListaPrecioDetCollection()) {
                    //Si no es el producto que continue
                    if(!l.getIdProductos().equals(prod)) {
                        continue;
                    }                    
                    //Armo el producto response
                    ProductoResponse sr = new ProductoResponse(l.getIdProductos());
                    //Seteo el costo en costo de repocicion el de la lista de precio
                    sr.setCostoReposicion(l.getPrecio());
                    //Agrego los detalles de cuentas contables
                    sr.getModeloCab().agregarModeloDetalleTipo(l.getIdProductos().getIdModeloCab().getModeloDetalleCollection(), idSisTipoModelo, idModulo);
                    //Seteo el iva del producto dependiendo del detalle si es != de 0 seteo el del detalle sino el del producto
                    for(ModeloDetalle r : prod.getIdModeloCab().getModeloDetalleCollection()) {
                        if(r.getIdSisModulo().getIdSisModulos().equals(modulo.getIdSisModulos()) && 
                           r.getValor().compareTo(BigDecimal.ZERO) != 0 &&
                           r.getIdSisTipoModelo().getIdSisTipoModelo().equals(2)) {
                            sr.getIVA().setPorcIVA(r.getValor());
                            break;
                        }
                    }
                    //Armo la respuesta de pendientes de cancelar asi es igual a la del store procedure
                    PendientesCancelarResponse pr = new PendientesCancelarResponse(sr);
                    pr.setCotaInferior(l.getCotaInf());
                    pr.setCotaSuperior(l.getCotaSup());
                    //seteo el precio de la lista de precios seleccionada
                    if(moneda.getDescripcion().equals("u$s") && l.getIdListaPrecios().getIdMoneda().getDescripcion().equals("$AR")) {
                        SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                        pr.setPrecio(sr.getCostoReposicion().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP));
                    } else if(moneda.getDescripcion().equals("$AR") && l.getIdListaPrecios().getIdMoneda().getDescripcion().equals("u$s")){
                       SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                       pr.setPrecio(sr.getCostoReposicion().multiply(cotDolar.getCotizacion()));
                    } else {
                        pr.setPrecio(sr.getCostoReposicion());
                    }
                    productosResponse.add(pr);
                }                
            } else {                  
                for(Producto s : productos) {
                    ProductoResponse pr = new ProductoResponse(s);
                    pr.getModeloCab().agregarModeloDetalleTipo(s.getIdModeloCab().getModeloDetalleCollection(),idSisTipoModelo, idModulo);
                    PendientesCancelarResponse sr = new PendientesCancelarResponse(pr);
                    productosResponse.add(sr);
                }
            }
            respuesta.setArraydatos(productosResponse);
            respuesta.setControl(AppCodigo.OK, "Lista de Productos");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al buscar el producto: " + e.getMessage());
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
