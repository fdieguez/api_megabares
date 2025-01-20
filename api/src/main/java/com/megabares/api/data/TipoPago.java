package com.megabares.api.data;

/**
 *
 * @author Francisco
 */
public enum TipoPago {
    
    LIC001("Base"),
    LIC002("Base+Fiscal"),
    LIC003("Base+Stock"),
    LIC004("Base+Fiscal+Stock"),
    LIC005("Con Comodato"),
    LIC006("ConVentaDeEquiposEnCuotas"),
    COB001("Cobro General"),    
    ING001("Ingreso General");
    
    private final String tipo;

    private TipoPago(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "TipoPago{" + "tipo=" + tipo + '}';
    }    

    public String getTipo() {
        return tipo;
    }
}
