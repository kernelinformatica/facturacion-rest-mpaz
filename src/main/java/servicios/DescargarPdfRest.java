package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.FactCab;
import entidades.FactFormaPago;
import entidades.Usuario;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.FactCabFacade;
import persistencia.FactFormaPagoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("descargarPdf")
public class DescargarPdfRest extends HttpServlet {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    @Inject FactCabFacade factCabFacade;
    @Inject FactFormaPagoFacade factFormaPagoFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPDF(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idFactCab = (Integer) Utils.getKeyFromJsonObject("idFactCab", jsonBody, "Integer");
            Integer nroCopias = (Integer) Utils.getKeyFromJsonObject("nroCopias", jsonBody, "Integer");
            String nombrePdf = (String) Utils.getKeyFromJsonObject("nombrePdf", jsonBody, "String");
            Boolean esCanje = (Boolean) Utils.getKeyFromJsonObject("esCanje", jsonBody, "boolean");
            System.out.println(nombrePdf);
            
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
            if(idFactCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "IdFacCab esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            FactCab factCab = factCabFacade.find(idFactCab);
            if(factCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe ese comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            String nombreReporte = factCab.getIdCteTipo().getIdReportes().getNombre();
            System.out.println(nombreReporte);
            
             if(nombreReporte == null) {
                respuesta.setControl(AppCodigo.ERROR, "El comprobante nro: "+factCab.getNumero()+" no tiene un reporte asociado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
             
             if(nroCopias == null) { 
                 nroCopias = 1;
             }
             
             if(esCanje == null) {
                 if(factCab.getFactFormaPagoCollection() != null && factCab.getFactFormaPagoCollection().size() > 0) {
                     List<FactFormaPago> listaFFP = new ArrayList(factCab.getFactFormaPagoCollection());
                     if(listaFFP != null && listaFFP.size() == 1 && (listaFFP.get(0).getIdFormaPago().getIdFormaPago() == 12 || listaFFP.get(0).getIdFormaPago().getIdFormaPago() == 13)) {
                         esCanje = true;
                     } else {
                         esCanje = false;
                     }
                 }
             }
             
             if(factCab.getIdCteTipo().getIdCteTipo() != 75) {
                 esCanje = false;
             }
             
            if(nombrePdf == null) {
                //Codigo verificador
                String codigoVerificador = "";
                if(factCab.getNumeroAfip() != null && factCab.getIdCteTipo().getCursoLegal() && factCab.getCai() != null && !" ".equals(factCab.getCai()) && !("".equals(factCab.getCai()))) {
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd");
                    Formatter obj = new Formatter();
                    String txtCuit = factCab.getCuit();
                    String txtCodComp = String.valueOf(obj.format("%02d",factCab.getCodigoAfip()));
                    String numero = String.valueOf(factCab.getNumero());
                    String ptoVenta = numero.substring(0, numero.length()-8);                
                    String txtPtoVta  = String.valueOf(obj.format("%04d", Integer.parseInt(ptoVenta)));
                    String txtCae = factCab.getCai();
                    String txtVtoCae = formatoFecha.format(factCab.getFechaVto());
                    codigoVerificador = utils.calculoDigitoVerificador(txtCuit, txtCodComp, txtPtoVta, txtCae, txtVtoCae);
                } 
                HashMap hm2 = new HashMap();
                HashMap hm3 = new HashMap();
                if(esCanje) {
                    hm2.put("idFactCab", idFactCab);
                    Collection<FactFormaPago> formaPagos = factFormaPagoFacade.getFactFormaPago(idFactCab);
                    if(formaPagos == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error al buscar el importe de la factura");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    BigDecimal importeTotalFact = BigDecimal.ZERO;
                    for(FactFormaPago formaPago : formaPagos) {
                        if(formaPago.getIdFactCab().getIdmoneda().getIdMoneda() == 1) {
                            importeTotalFact = importeTotalFact.add(formaPago.getImporte().divide(formaPago.getIdFactCab().getCotDolar(), 2, RoundingMode.HALF_UP));
                        } else {
                            importeTotalFact = importeTotalFact.add(formaPago.getImporte());
                        }
                    }
                    String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
                    LocalDate localDate = factCab.getFechaEmision().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int year  = localDate.getYear();
                    int month = localDate.getMonthValue();
                    int day   = localDate.getDayOfMonth();
                    String fechaEmision = String.valueOf(day) + " de " + meses[month - 1] + " del " + String.valueOf(year);
                    localDate = factCab.getFechaVto().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    year  = localDate.getYear();
                    month = localDate.getMonthValue();
                    day   = localDate.getDayOfMonth();
                    String fechaVto = String.valueOf(day) + " de " + meses[month - 1] + " del " + String.valueOf(year);
                    String importeString = this.Convertir(importeTotalFact.setScale(2).toString(), true);
                    HashMap hm = new HashMap();
                    hm3.put("idFactCab", idFactCab);
                    hm3.put("vtoString", fechaVto);
                    hm3.put("emisionString", fechaEmision);
                    hm3.put("importeString", importeString);
                }
                System.out.println("codigoVerificador: " + codigoVerificador);
                HashMap hm = new HashMap();
                hm.put("idFactCab", idFactCab);
                hm.put("codigoVerificador", codigoVerificador);
                hm.put("prefijoEmpresa", "05");
                System.out.println(idFactCab + " - " + codigoVerificador);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] bytes = utils.generateJasperReportPDF(request, nombreReporte, hm, user, outputStream, nroCopias, esCanje, hm2, hm3);
                System.out.println(request.toString() + " - " + nombreReporte + " - " + user.toString());
                String nomeRelatorio= nombreReporte + ".pdf";
                return Response.ok(bytes).type("application/pdf").header("Content-Disposition", "filename=\"" + nomeRelatorio + "\"").build();
            } else {
                /*if(nombrePdf.equals("contratoCanje")) {
                    HashMap hm2 = new HashMap();
                    hm2.put("idFactCab", idFactCab);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] bytes = utils.generateJasperReportPDF(request, nombrePdf, hm2, user, outputStream);
                    System.out.println(request.toString() + " - " + nombrePdf + " - " + user.toString());
                    String nomeRelatorio= nombrePdf + ".pdf";
                    return Response.ok(bytes).type("application/pdf").header("Content-Disposition", "filename=\"" + nomeRelatorio + "\"").build();
                } else if(nombrePdf.equals("documentoCanje")) {
                    Collection<FactFormaPago> formaPagos = factFormaPagoFacade.getFactFormaPago(idFactCab);
                    if(formaPagos == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error al buscar el importe de la factura");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    BigDecimal importeTotalFact = BigDecimal.ZERO;
                    for(FactFormaPago formaPago : formaPagos) {
                        if(formaPago.getIdFactCab().getIdmoneda().getIdMoneda() == 1) {
                            importeTotalFact = importeTotalFact.add(formaPago.getImporte().divide(formaPago.getIdFactCab().getCotDolar(), 2, RoundingMode.HALF_UP));
                        } else {
                            importeTotalFact = importeTotalFact.add(formaPago.getImporte());
                        }
                    }
                    String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
                    LocalDate localDate = factCab.getFechaEmision().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int year  = localDate.getYear();
                    int month = localDate.getMonthValue();
                    int day   = localDate.getDayOfMonth();
                    String fechaEmision = String.valueOf(day) + " de " + meses[month - 1] + " del " + String.valueOf(year);
                    localDate = factCab.getFechaVto().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    year  = localDate.getYear();
                    month = localDate.getMonthValue();
                    day   = localDate.getDayOfMonth();
                    String fechaVto = String.valueOf(day) + " de " + meses[month - 1] + " del " + String.valueOf(year);
                    String importeString = this.Convertir(importeTotalFact.setScale(2).toString(), true);
                    HashMap hm = new HashMap();
                    hm.put("idFactCab", idFactCab);
                    hm.put("vtoString", fechaVto);
                    hm.put("emisionString", fechaEmision);
                    hm.put("importeString", importeString);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] bytes = utils.generateJasperReportPDF(request, nombrePdf, hm, user, outputStream);
                    System.out.println(request.toString() + " - " + nombrePdf + " - " + user.toString());
                    String nomeRelatorio= nombrePdf + ".pdf";
                    return Response.ok(bytes).type("application/pdf").header("Content-Disposition", "filename=\"" + nomeRelatorio + "\"").build();
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "No existe ese comprobante para imprimir");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }*/
                respuesta.setControl(AppCodigo.ERROR, "No existe ese comprobante para imprimir");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    private final String[] UNIDADES = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private final String[] DECENAS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
        "diecisiete ", "dieciocho ", "diecinueve", "veinte ", "VEINTIUN","VEINTIDOS","VEINTITRES","VEINTICUATRO","VEINTICINCO","VEINTISEIS","VEINTISIETE","VEINTIOCHO","VEINTINUEVE", "treinta ", "cuarenta ",
        "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "};
    private final String[] CENTENAS = {"", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
        "setecientos ", "ochocientos ", "novecientos "};


    public String Convertir(String numero, boolean mayusculas) {
        String literal = "";
        String parte_decimal;
        //si el numero utiliza (.) en lugar de (,) -> se reemplaza
        numero = numero.replace(".", ",");
        //si el numero no tiene parte decimal, se le agrega ,00
        if (numero.indexOf(",") == -1) {
            numero = numero + ",00";
        }
        //se valida formato de entrada -> 0,00 y 999 999 999,00
        if (Pattern.matches("\\d{1,9},\\d{1,2}", numero)) {
            //se divide el numero 0000000,00 -> entero y decimal
            String Num[] = numero.split(",");
            //de da formato al numero decimal
            parte_decimal = " C/" + Num[1] + " CENTAVOS";
            //se convierte el numero a literal
            if (Integer.parseInt(Num[0]) == 0) {//si el valor es cero
                literal = "cero ";
            } else if (Integer.parseInt(Num[0]) > 999999) {//si es millon
                literal = getMillones(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 999) {//si es miles
                literal = getMiles(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 99) {//si es centena
                literal = getCentenas(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 9) {//si es decena
                literal = getDecenas(Num[0]);
            } else {//sino unidades -> 9
                literal = getUnidades(Num[0]);
            }
            //devuelve el resultado en mayusculas o minusculas
            if (mayusculas) {
                return (literal + parte_decimal).toUpperCase();
            } else {
                return (literal + parte_decimal);
            }
        } else {//error, no se puede convertir
            return literal = null;
        }
    }

    /* funciones para convertir los numeros a literales */
    private String getUnidades(String numero) {// 1 - 9
        //si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9
        String num = numero.substring(numero.length() - 1);
        return UNIDADES[Integer.parseInt(num)];
    }

    private String getDecenas(String num) {// 99                        
        int n = Integer.parseInt(num);
        if (n < 10) {//para casos como -> 01 - 09
            return getUnidades(num);
        } else if (n > 29) {//para 20...99
            String u = getUnidades(num);
            if (u.equals("")) { //para 20,30,40,50,60,70,80,90
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 17];
            } else {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 17] + " y " + u;
            }
        } else {//numeros entre 11 y 19
            return DECENAS[n - 10];
        }
    }

    private String getCentenas(String num) {// 999 o 099
        if (Integer.parseInt(num) > 99) {//es centena
            if (Integer.parseInt(num) == 100) {//caso especial
                return " cien ";
            } else {
                return CENTENAS[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            }
        } else {//por Ej. 099 
            //se quita el 0 antes de convertir a decenas
            return getDecenas(Integer.parseInt(num) + "");
        }
    }

    private String getMiles(String numero) {// 999 999
        //obtiene las centenas
        String c = numero.substring(numero.length() - 3);
        //obtiene los miles
        String m = numero.substring(0, numero.length() - 3);
        String n = "";
        //se comprueba que miles tenga valor entero
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);
            return n + "mil " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }

    }

    private String getMillones(String numero) { //000 000 000        
        //se obtiene los miles
        String miles = numero.substring(numero.length() - 6);
        //se obtiene los millones
        String millon = numero.substring(0, numero.length() - 6);
        String n = "";
        if (millon.length() > 1) {
            n = getCentenas(millon) + "millones ";
        } else {
            n = getUnidades(millon) + "millon ";
        }
        return n + getMiles(miles);
    }
}

