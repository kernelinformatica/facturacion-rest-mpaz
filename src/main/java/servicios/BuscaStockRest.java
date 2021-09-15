package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.Payload;
import datos.ServicioResponse;
import datos.StockGeneralResponse;
import datos.StockResponse;
import entidades.Acceso;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("buscaStock")
public class BuscaStockRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils; 
    
    @POST
    @Path("/{tipo}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStock(
        @PathParam ("tipo") String tipo,
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Date fechaDesde = (Date)Utils.getKeyFromJsonObject("fechaDesde", jsonBody, "Date");
            Date fechaHasta = (Date)Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");
            Integer idProductoDesde = (Integer) Utils.getKeyFromJsonObject("idProductoDesde", jsonBody, "Integer");
            Integer idProductoHasta = (Integer) Utils.getKeyFromJsonObject("idProductoHasta", jsonBody, "Integer");
            Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", jsonBody, "Integer");
            Integer idDeposito = (Integer) Utils.getKeyFromJsonObject("idDeposito", jsonBody, "Integer");
            Integer idCteTipo = (Integer) Utils.getKeyFromJsonObject("idCteTipo", jsonBody, "Integer");
            Integer idRubro= (Integer) Utils.getKeyFromJsonObject("idRubro", jsonBody, "Integer");
            Integer idSubRubro = (Integer) Utils.getKeyFromJsonObject("idSubRubro", jsonBody, "Integer");
            Integer tipoEstado = (Integer) Utils.getKeyFromJsonObject("tipoEstado", jsonBody, "Integer");


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
            
            List<Payload> stock = new ArrayList<>();
            if(tipo != null && tipo.equals("producto")) {
            //seteo el nombre del store
            String noombreSP = "call s_buscaStock(?,?,?,?,?,?,?)";

            //invoco al store
            CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, noombreSP);
            
            //valido que el Procedimiento Almacenado no sea null
            if(callableStatement == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
                       
            //Parseo las fechas a sql.date
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            
            //Seteo los parametros del SP
            callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatement.setDate(2, sqlFechaDesde);
            callableStatement.setDate(3, sqlFechaHasta);
            callableStatement.setInt(4,idProducto);
            callableStatement.setInt(5, idDeposito);
            callableStatement.setInt(6, tipoEstado);                        
            callableStatement.setInt(7, idCteTipo);
            ResultSet rs = callableStatement.executeQuery();
            List<StockResponse> sr = new ArrayList<>();
                while (rs.next()) {
                    StockResponse st = new StockResponse(
                            rs.getString("comprobante"),
                            rs.getBigDecimal("numero"),
                            rs.getDate("fechaEmision"),
                            rs.getBigDecimal("ingresos"),
                            rs.getBigDecimal("egresos"),
                            rs.getBigDecimal("pendiente"),
                            rs.getString("deposito"),
                            rs.getBoolean("trazable"),
                            rs.getString("rubro"),
                            rs.getString("subRubro"),
                            rs.getInt("idFactCab")
                            );
                    sr.add(st);
                }
                if(!sr.isEmpty()) {
                    BigDecimal st = new BigDecimal(0);
                    BigDecimal sv = new BigDecimal(0);
                    //Recorro el array desde el ultimo registro al primero para calcular el stock fisico
                    for(int i = sr.size() -1; i >= 0; i--) {
                        //sumo los ingresos al stock
                        st = st.add(sr.get(i).getIngresos());
                        //sumo los egresos negativos al stock
                        st = st.add(sr.get(i).getEgresos().negate());
                        //Le seteo el stock fisico
                        sr.get(i).setStockFisico(st);
                        //Seteo el stock virtual
                        sv = sv.add(sr.get(i).getPendiente());
                        sr.get(i).setStockVirtual(sv);
                    }
                    stock.addAll(sr);
                }
              callableStatement.getConnection().close();
            } else if(tipo != null && tipo.equals("general")){
               //seteo el nombre del store
               String noombreSP = "call s_buscaStockGral(?,?,?,?,?,?,?,?,?)";
               //invoco al store
               CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, noombreSP);

               //valido que el Procedimiento Almacenado no sea null
               if(callableStatement == null) {
                   respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                   return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

               //Parseo las fechas a sql.date
               java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
               java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
               //Seteo los parametros del SP
               callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
               callableStatement.setDate(2, sqlFechaDesde);
               callableStatement.setDate(3, sqlFechaHasta);
               callableStatement.setInt(4,idProductoDesde);
               callableStatement.setInt(5,idProductoHasta);
               callableStatement.setInt(6, idRubro);
               callableStatement.setInt(7, idSubRubro);
               callableStatement.setInt(8, idDeposito);
               callableStatement.setInt(9, tipoEstado);
               ResultSet rs = callableStatement.executeQuery();
                   while (rs.next()) {
                       StockGeneralResponse st = new StockGeneralResponse(
                               rs.getString("codProducto"),
                               rs.getString("descripcion"),
                               rs.getBigDecimal("ingresos"),
                               rs.getBigDecimal("egresos"),                              
                               rs.getBigDecimal("fisicoImputado"),
                               rs.getBigDecimal("ingresos").subtract(rs.getBigDecimal("egresos")),
                               rs.getBigDecimal("ingresoVirtual"),
                               rs.getBigDecimal("egresoVirtual"),
                               //rs.getBigDecimal("virtualImputado"),
                               BigDecimal.ZERO,
                               (rs.getBigDecimal("ingresoVirtual").subtract(rs.getBigDecimal("egresoVirtual"))),                               
                               rs.getBoolean("trazable"),
                               rs.getString("rubro"),
                               rs.getString("subRubro"));
                       stock.add(st);
                   }
               callableStatement.getConnection().close();
            } else {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el servicio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setArraydatos(stock);
            respuesta.setControl(AppCodigo.OK, "Stock");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
