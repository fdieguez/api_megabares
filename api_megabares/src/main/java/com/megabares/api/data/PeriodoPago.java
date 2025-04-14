package com.megabares.api.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author Francisco
 */
@Entity
@Table(name = "periodo")
public class PeriodoPago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Version
    @Column(name = "version", nullable = false)
    private int version;
    @Column(name = "desde", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date desde;
    @Column(name = "hasta", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date hasta;
    @Column(name = "codigo", nullable = false)
    private short codigo;
    @Column(name = "semilla", nullable = false)
    private short semilla;
    @Column(name = "multip", nullable = false)
    private short multiplicador;
    @Column(name = "numero", unique = true, nullable = false)
    private long numero;
    @JoinColumn(name = "id_cliente", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Cliente cliente;

    public PeriodoPago() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public short getCodigo() {
        return codigo;
    }

    public void setCodigo(short codigo) {
        this.codigo = codigo;
    }

    public short getSemilla() {
        return semilla;
    }

    public void setSemilla(short semilla) {
        this.semilla = semilla;
    }

    public short getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(short multiplicador) {
        this.multiplicador = multiplicador;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    @Transient
    public String getCodigoCliente() {

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
        //Parsing the given String to Date object
        String fechaFormateada = formatter.format(getHasta());
//        System.out.println("Date object value: " + fechaFormateada);

        short nextCodigo = (short) (codigo + 1);

        String input = (codigo < 10 ? "0" + codigo : codigo)
                + fechaFormateada
                + (nextCodigo < 10 ? "0" + nextCodigo : nextCodigo);
        StringBuilder resultado = new StringBuilder();
        int sem = Integer.valueOf(this.semilla);
        int multi = Integer.valueOf(this.multiplicador);

        for (int i = 0; i < input.length(); i++) {
            resultado.append((char) (input.charAt(i) + (sem * multi)));
        }
        String licencia = "";
        licencia += sem;
        licencia += multi;
        licencia += resultado.toString();

        return licencia;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.hasta);
        hash = 47 * hash + this.codigo;
        hash = 47 * hash + this.semilla;
        hash = 47 * hash + this.multiplicador;
        hash = 47 * hash + (int) (this.numero ^ (this.numero >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeriodoPago other = (PeriodoPago) obj;
        if (this.codigo != other.codigo) {
            return false;
        }
        if (this.semilla != other.semilla) {
            return false;
        }
        if (this.multiplicador != other.multiplicador) {
            return false;
        }
        if (this.numero != other.numero) {
            return false;
        }
        if (!Objects.equals(this.hasta, other.hasta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PeriodoPago{desde=").append(desde);
        sb.append(", hasta=").append(hasta);
        sb.append(", codigo=").append(codigo);
        sb.append(", semilla=").append(semilla);
        sb.append(", multiplicador=").append(multiplicador);
        sb.append(", numero=").append(numero);
        sb.append('}');
        return sb.toString();
    }

}
