package com.megabares.api.data;

/**
 *
 * @author Francisco
 */
public enum TipoEmail {
    
    UPDATE("[MEGA-ACT]"),
    ERROR("[MEGA-ERR]"),
    DINERO("Recibiste dinero"),
    PAGO("Recibiste un pago"),
    SUSCRI("Tienes un nuevo suscriptor"),
    VENTA("Vendiste"),
    OTRO("Otro");
    
    private final String tipo;

    private TipoEmail(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "TipoEmail{" + "tipo=" + tipo + '}';
    }

    public String getTipo() {
        return tipo;
    }
}
