package servicios;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author Kernel Informatica
 */
@javax.ws.rs.ApplicationPath("ws")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(MultiPartFeature.class);
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(servicios.ActualizaProductosRest.class);
        resources.add(servicios.BorraComprobanteOrdenPagoRest.class);
        resources.add(servicios.BorraComprobanteRest.class);
        resources.add(servicios.BuscaCerealesRest.class);
        resources.add(servicios.BuscaComprobanteAnticipadoRest.class);
        resources.add(servicios.BuscaComprobanteRest.class);
        resources.add(servicios.BuscaCotizacionRest.class);
        resources.add(servicios.BuscaListaPrecio.class);
        resources.add(servicios.BuscaLote.class);
        resources.add(servicios.BuscaLotes.class);
        resources.add(servicios.BuscaModeloRest.class);
        resources.add(servicios.BuscaPosicionStockRest.class);
        resources.add(servicios.BuscaProductosRest.class);
        resources.add(servicios.BuscaStockRest.class);
        resources.add(servicios.BuscarComprobantesOrdenesPagoRest.class);
        resources.add(servicios.BuscarOrdenesPagoRest.class);
        resources.add(servicios.CalculoImportesSubtotales.class);
        resources.add(servicios.CanjesContratosCerealesRest.class);
        resources.add(servicios.CategoriaRest.class);
        resources.add(servicios.ClienteRest.class);
        resources.add(servicios.ComprobanteGestAgroRest.class);
        resources.add(servicios.ContPlanCuentaRest.class);
        resources.add(servicios.ContratoComprobanteRest.class);
        resources.add(servicios.ContratoRest.class);
        resources.add(servicios.CteTipoRest.class);
        resources.add(servicios.CultivoRest.class);
        resources.add(servicios.DepositoRest.class);
        resources.add(servicios.DescargarContratoRest.class);
        resources.add(servicios.DescargarLibroIvaRest.class);
        resources.add(servicios.DescargarListadoRest.class);
        resources.add(servicios.DescargarOrdenPagoPdfRest.class);
        resources.add(servicios.DescargarPdfRest.class);
        resources.add(servicios.DescargarStockRest.class);
        resources.add(servicios.EstadoSisaRest.class);
        resources.add(servicios.FiltroListaPrecioRest.class);
        resources.add(servicios.FormaPagoRest.class);
        resources.add(servicios.GrabaComprobanteRest.class);
        resources.add(servicios.GrabaOrdenesDePagoRest.class);
        resources.add(servicios.ImputacionesRest.class);
        resources.add(servicios.LibroRest.class);
        resources.add(servicios.ListaPrecioRest.class);
        resources.add(servicios.MandaMailPdfRest.class);
        resources.add(servicios.MarcaRest.class);
        resources.add(servicios.ModeloCabRest.class);
        resources.add(servicios.ModeloImputacionRest.class);
        resources.add(servicios.NumeradorRest.class);
        resources.add(servicios.PadronRest.class);
        resources.add(servicios.ParametrosCanjeRest.class);
        resources.add(servicios.ParametrosPesificadoRest.class);
        resources.add(servicios.PendientesCancelarRest.class);
        resources.add(servicios.PerfilRest.class);
        resources.add(servicios.ProductoRest.class);
        resources.add(servicios.ProveedoresRest.class);
        resources.add(servicios.PtoVentaRest.class);
        resources.add(servicios.RelacionesCanjeRest.class);
        resources.add(servicios.RubroRest.class);
        resources.add(servicios.SisCanjeRest.class);
        resources.add(servicios.SisCategoriaRest.class);
        resources.add(servicios.SisCodigoAfipRest.class);
        resources.add(servicios.SisComprobanteRest.class);
        resources.add(servicios.SisCotDolarRest.class);
        resources.add(servicios.SisEstadoRest.class);
        resources.add(servicios.SisFormaPagoRest.class);
        resources.add(servicios.SisIVARest.class);
        resources.add(servicios.SisLetraCodigoAfipRest.class);
        resources.add(servicios.SisLetraRest.class);
        resources.add(servicios.SisModuloRest.class);
        resources.add(servicios.SisMonedaRest.class);
        resources.add(servicios.SisSitIvaRest.class);
        resources.add(servicios.SisTipoModeloRest.class);
        resources.add(servicios.SisTipoOperacionRest.class);
        resources.add(servicios.SisUnidadRest.class);
        resources.add(servicios.SubRubroRest.class);
        resources.add(servicios.SucursalRest.class);
        resources.add(servicios.TiposOpDepositosRest.class);
        resources.add(servicios.UltimoCodigoProdRest.class);
        resources.add(servicios.UsuarioRest.class);
        resources.add(servicios.VendedorRest.class);
        resources.add(utils.CrossRules.class);
    }
    
}
