package com.megabares.api.util;

import com.megabares.api.data.*;
import static com.megabares.api.data.TipoPago.LIC005;
import com.megabares.api.exceptions.NonexistentEntityException;
import com.megabares.api.repositories.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.Hibernate;

/**
 * @author Francisco
 */
@Service
@Log4j2
public class EmailProcess {

    // constastes de recepcion de pago
    final String NRO_OPERACION = "Número de operación:";
    final String TARGET_BLANK = "target=\"_blank\">";
    final String REFERENCIA_VENDEDOR = "Referencia de";
    final String HTML_P_OPEN = "<p>";
    final String HTML_P_CLOSE = "</p>";
    final String HTML_A_CLOSE = "</a>";
    final String HTML_DIV_CLOSE = "</div>";
    final String HTML_STRONG_CLOSE = "</strong>";
    final String SIMBOL_$_ESP = "$ ";
    final String CONTRAPARTE = "Contraparte:";
    final String PAGO_ESPECIAL = "MB_ESPECIAL";
    final String ASFGASDF = "<div style=\"margin-top:20px;margin-right:3%;margin-bottom:20px;padding-left:3%\">";
    // constantes
//    private final String USUARIO = "sistemamegabares@gmail.com";
//    private final String REMITENTE = "sistemamegabares";
//    private final String PASS = "mega.bares";
    private final String MERCADOPAGO = "mercadopago.com";
    //    private final String MERCADOLIBRE = "mercadolibre.com";
    private final String PROTOCOL = "mail.store.protocol";
    private final String IMAPS = "imaps";
    private final String HOST = "imap.gmail.com";
    private final String NA = "NO_APLICA";
    private final String PUNTO = ".";
    private final String COMA = ",";
    private final String VACIO = "";
    private final String ESPACIO = " ";
    private final String PU = "pdm.0PU";
    // constantes de mensajes
    private final String TOTAL_ACREDITADO = "Total acreditado en tu cuenta";
    private final String PAGO = "Pago: $ ";
    private final String MODULO_BASE = "moduloBase";
    private final String MODULO_STOCK = "moduloStock";
    private final String MODULO_FISCAL = "moduloFical";
    private final String MODULO_OLD = "moduloOld";
    private EmailRepository emailRepository;
    private ClienteRepository clienteRepository;
    private CobroRepository cobroRepository;
    private ModuloRepository moduloRepository;
    private PeriodoRepository periodoRepository;
    private String destinatario;
    @Value("${email.asunto}")
    private String asunto;
    private String cuerpo;
    private String contenidoDelMail;
    private Long frecuencia = 180000L; // 3 minutos por defecto
    private Logger logger;

    Session session;

    public EmailProcess(EmailRepository emailRepository, ClienteRepository clienteRepository,
            CobroRepository cobroRepository, ModuloRepository moduloRepository, PeriodoRepository periodoRepository) {
        this.emailRepository = emailRepository;
        this.clienteRepository = clienteRepository;
        this.cobroRepository = cobroRepository;
        this.moduloRepository = moduloRepository;
        this.periodoRepository = periodoRepository;
    }

    public void enviar(String destinatario, String asunto, String cuerpo) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.enviar();
    }

    public void enviar() {
        // Esto es lo que va delante de @gmail.com en tu cuenta de correo. Es el remitente también.
//        String remitente = "sistemamegabares";  

        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", "465");

        if (session == null) {
            session = Session.getDefaultInstance(props);
        }

        MimeMessage message = new MimeMessage(session);

        try {
            // config mail donweb
            message.setFrom(new InternetAddress("contacto@megabares.com"));
            // Se podrían añadir varios de la misma manera
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
//            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(USUARIO));
            message.setSubject(asunto);
            message.setContent(cuerpo, "text/html");
            Transport transport = session.getTransport("smtp");
            transport.connect("c1701223.ferozo.com", "contacto@megabares.com", "megapuntoBarespunto1");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (MessagingException me) {
            me.printStackTrace();   //Si se produce un error
        }
    }

    public void monitor() {
        log.info("monitoreo...");
        processEmails("contacto@megabares.com", "megapuntoBarespunto1", "INBOX");

    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getContenidoDelMail() {
        return contenidoDelMail;
    }

    public void setContenidoDelMail(String contenidoDelMail) {
        this.contenidoDelMail = contenidoDelMail;
    }

    public Long getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Long frecuencia) {
        this.frecuencia = frecuencia;
    }

    /**
     * analiza los correos, y los discrimina por los tipos posibles actualemente 1 - pagos registrados 2 - nueva
     * suscripcion 3 - Errores 4 - actualizaciones
     *
     * @param usuario
     * @param contrasenia
     * @param carpeta
     */
    private void processEmails(String usuario, String contrasenia, String carpeta) {
        log.info("processEmails...");

        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.port", "993");
//        props.put("mail.smtp.starttls.required", "true");
//props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        int pagos = 0, suscriptores = 0, ventas = 0, otros = 0, errores = 0, updates = 0, dineros = 0;
// descomentar si hay un proxy para acceder a intener
        /* props.setProperty("http.proxyHost", "192.168.0.1");
            props.setProperty("http.proxyPort", "8080"); */
        try {
            Session session = Session.getDefaultInstance(props);
//            session.setDebug(true);
            Store store = session.getStore(IMAPS);
            store.connect("c1701223.ferozo.com", usuario, contrasenia);
            Folder inbox = store.getFolder(carpeta);
            inbox.open(Folder.READ_WRITE);
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);// solo traigo los NO leidos
            Message messages[] = inbox.search(ft);

            for (Message message : messages) {
                TipoEmail tipoDeCorreo = getTipoEmail(message);
                message.setFlag(Flags.Flag.SEEN, true); // marca como leido
                switch (tipoDeCorreo) {
                    case UPDATE:
                        updates++;
//                        System.out.println("actualizacion");
                        /**
                         * TODO: debe descargar las actualizaciones y dejarlas disponibles para la apimegabares
                         */
//                        procesarActualizacion(message);
                        break;
                    case ERROR:
                        errores++;
//                        System.out.println("error");
                        break;
                    case DINERO:
                        dineros++;
//                        System.out.println("Recibiste dinero");
                         {
                            try {
//                                procesarDinero(message);
                            } catch (Exception ex) {
                                log.error("Error al procesar envio/pago de dinero", ex);
                            }
                        }
                        break;

                    case SUSCRI:
                        suscriptores++;
                        log.info("Nuevo Suscriptor - subject: " + message.getSubject() + " - received: " + message.getReceivedDate());
                        break;
                    case PAGO:
                        pagos++;
                        log.info("Procesando Pago... - subject: " + message.getSubject() + " - received: " + message.getReceivedDate());
                        try {
                            procesarPago(message);
                        } catch (Exception ex) {
                            log.error("Error al procesar envio/pago de dinero", ex);
                        }
                        break;
                    case VENTA:
                        ventas++;
//                        System.out.println("nueva venta");
                        break;
                    case OTRO:
                        otros++;

                        log.info("OTROS - subject: " + message.getSubject()
                                + " - received: " + message.getReceivedDate());
                        break;

                }

            }
            String logStr = "\n******* TOTALES *******";

            if (pagos > 0) {
                logStr += "\n - " + pagos + " pagos recibidos";
            }
            if (suscriptores > 0) {
                logStr += "\n - " + suscriptores + " suscriptores nuevos";
            }

            if (updates > 0) {
                logStr += "\n - " + updates + " updates";
            }

            if (errores > 0) {
                logStr += "\n - " + errores + " errores";
            }

            if (dineros > 0) {
                logStr += "\n - " + dineros + " dineros";
            }

            if (ventas > 0) {
                logStr += "\n - " + ventas + " ventas";
            }

            if (otros > 0) {
                logStr += "\n - " + otros + " otros";
            }

            logStr += "\n" + "TOTAL: "
                    + (pagos + suscriptores + updates + errores + dineros + ventas + otros)
                    + " Recibidos.";

            log.info(logStr);

//            inbox.close();
            store.close();

        } catch (NoSuchProviderException e) {

            log.error("Error desconocido", e);

        } catch (MessagingException e) {
            log.error("Error de mensaje", e);
        }
    }

    public void setContenido(Part p) throws Exception {

        Object o = p.getContent();

        if (o instanceof String) {
            String contenido = (String) o;
//            if (contenido.contains(MERCADOPAGO)
//                    || contenido.contains(MERCADOLIBRE)
//                    || contenido.contains(SISTEMAMEGABARES)) {
////                System.out.println((String) o);
            contenidoDelMail = contenido;
//            }

        } else if (o instanceof Multipart) {

//            System.out.println("This is a Multipart");
            Multipart mp = (Multipart) o;

            int count = mp.getCount();

            for (int i = 0; i < count; i++) {
                setContenido(mp.getBodyPart(i));
            }

        } else if (o instanceof InputStream) {
//            System.out.println("This is just an input stream");
            InputStream is = (InputStream) o;

            int c;

// while ((c = is.read()) != -1)
// System.out.write(c);
        }

    }

    private TipoPago getTipoPago(String refVen) {

        switch (refVen) {
            case "LIC001":
                return TipoPago.LIC001;

            case "LIC002":
                return TipoPago.LIC002;

            case "LIC003":
                return TipoPago.LIC003;

            case "LIC004":
                return TipoPago.LIC004;

            case "LIC005":
                return TipoPago.LIC005;

            case "LIC006":
                return TipoPago.LIC006;

            case PAGO_ESPECIAL:
                return TipoPago.LIC005;

            case "COB001":
                return TipoPago.COB001;

            case "ING001":
                return TipoPago.ING001;

            default:
                return null;
        }

    }

    private TipoEmail getTipoEmail(Message m) {
        if (null == m) {
            return null;
        }

        try {
            if (m.getSubject().contains(TipoEmail.UPDATE.getTipo())) {
                return TipoEmail.UPDATE;

            } else if (m.getSubject().contains(TipoEmail.ERROR.getTipo())) {
                return TipoEmail.ERROR;

            } else if (m.getSubject().contains(TipoEmail.DINERO.getTipo())) {
                return TipoEmail.DINERO;

            } else if (m.getSubject().contains(TipoEmail.SUSCRI.getTipo())) {
                return TipoEmail.SUSCRI;

            } else if (m.getSubject().contains(TipoEmail.PAGO.getTipo())) {
                return TipoEmail.PAGO;

            } else if (m.getSubject().contains(TipoEmail.VENTA.getTipo())) {
                return TipoEmail.VENTA;
            }

        } catch (MessagingException ex) {
            log.error("Error", ex);
        }
        return TipoEmail.OTRO;
    }

    private void procesarActualizacion(Message message) throws MessagingException, IOException {

        // TODO: completar la logica de actualizaciones
    }

    /**
     * metodo que parsea los emails de Recepcion de dinero
     *
     * @param message
     * @throws MessagingException
     * @throws IOException
     */
    private void procesarDinero(Message message)
            throws MessagingException, IOException, NonexistentEntityException, Exception {

        // message.setFlag(Flags.Flag.ANSWERED, true);
        contenidoDelMail = null;
        try {
            setContenido(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != contenidoDelMail) {
            Date fecha = message.getReceivedDate();
            message.setFlag(Flags.Flag.SEEN, true); // marca como leido
            String asunto = message.getSubject();

//            email.setFecha(message.getReceivedDate());
//                String content = message.getContentType();
//            MimeMultipart part = (MimeMultipart) message.getContent();
//                BodyPart bodyPart = part.getBodyPart(0);
//            part.getContentType();
//            part.getCount();
//            part.getPreamble();
//            Flags flags = message.getFlags();
            Address[] form = message.getFrom();
//            System.out.println("detalle mail: " + subject);
//            System.out.println("FROM: ");
            String destinatarios = VACIO;

            for (Address a : form) {
                destinatarios += a.toString() + ";";
            }

//            email.setReceptores(destinatarios);
//            System.out.println("contenido: ");
            int indexTotal = contenidoDelMail.indexOf(TOTAL_ACREDITADO);
            final String MENSAJE = "Mensaje: ";

            if (indexTotal >= 0) {
                int indexNroEnvio = contenidoDelMail.indexOf("Envío de dinero N.°:");
                int indexMailCliente = contenidoDelMail.indexOf(TARGET_BLANK);
                indexMailCliente = indexMailCliente + TARGET_BLANK.length();
                int indexNroEnvioSolo = indexNroEnvio + "Envío de dinero N.°:".length();
                int indexMensaje = contenidoDelMail.indexOf(MENSAJE);
                indexMensaje = indexMensaje + MENSAJE.length();
                int indexPago = contenidoDelMail.indexOf(PAGO);

                // total acreditado
                String ta = contenidoDelMail.substring(indexTotal, contenidoDelMail.indexOf("</p>", indexTotal));
                ta = ta.substring(ta.indexOf("$ ") + 2, ta.length());
//                Cobro cobro = new Cobro();

                ta = ta.replaceFirst("</strong>", VACIO);
                ta = ta.replace(PUNTO, VACIO);
                ta = ta.replace(COMA, PUNTO);
//                System.out.println(ta);

                BigDecimal totalAcreditado = new BigDecimal(ta.trim());
//                cobro.setTotalAcreditado(totalAcreditado);

//                String nroEnvio = contenidoDelMail.substring(indexNroEnvio
//                        , contenidoDelMail.indexOf("</p>", indexNroEnvio)).trim();
                String nroEnvioSolo = contenidoDelMail.substring(indexNroEnvioSolo, contenidoDelMail
                        .indexOf("</p>", indexNroEnvioSolo)).trim();
                String mensaje = contenidoDelMail.substring(indexMensaje, contenidoDelMail
                        .indexOf("</p>", indexMensaje)).trim();
                String montoPago = contenidoDelMail.substring(indexPago, contenidoDelMail
                        .indexOf("</p>", indexPago)).trim();
                montoPago = montoPago.substring(montoPago.indexOf("$ ") + 2, montoPago.length());
                montoPago = montoPago.replace(PUNTO, VACIO);
                montoPago = montoPago.replace(COMA, PUNTO);
                BigDecimal monto = new BigDecimal(montoPago);
                Long numero = ((NumberUtils.isNumber(nroEnvioSolo)) ? Long.valueOf(nroEnvioSolo) : null);
                String mailCliente = contenidoDelMail.substring(indexMailCliente, contenidoDelMail
                        .indexOf("</a>", indexMailCliente));

                // ************************************************
                // *************** FIN PARSEO EMAIL ***************
                // ************************************************
//                EmailJpaController emailDAO = new EmailJpaController(
//                        Persistence.createEntityManagerFactory(PU));
                // si el mail existe, ya fue analizado previamente y es repetido
//                Email emailExistente = emailDAO.findEmail(fecha, asunto, contenidoDelMail);
                Email emailExistente = emailRepository.findByFechaAndAsunto(fecha, asunto);

                if (null != emailExistente) {
                    // El email ya se proceso previamente
                    System.err.print("Email repetido: " + asunto + " - " + fecha.toString());
                } else {
                    /*
                    Se debe generar un email con un ingreso (cobro)
                    El ingreso tiene que tener un cliente que en caso de no existir,
                    debe crearse el cliente nuevo. Además el cobro debe generar un nuevo periodoPago
                    asociado al cliente y que se refiera a algúm módulo.
                    
                    La secuencia deberia ser la siguiente:
                    1 Email => 1 Ingreso => 1 Cliente => 1 PeriodoPago
                                                      => 1 modulo
                    Para el caso del cliente y modulo en caso de no existir, se crea.
                     */
                    Email email = new Email(fecha, MERCADOPAGO, contenidoDelMail, asunto,
                            destinatarios, TipoEmail.PAGO);

//                    ClienteJpaController clienteDAO = new ClienteJpaController(
//                            Persistence.createEntityManagerFactory(PU));
//                    CobroJpaController cobroDAO = new CobroJpaController(
//                            Persistence.createEntityManagerFactory(PU));
                    Cobro cobro = new Cobro();
                    cobro.setFecha(fecha);
                    cobro.setMonto(monto);
                    cobro.setNroMercadoPago(numero);
                    cobro.setConcepto(TipoEmail.DINERO.name());
                    cobro.setTipoPago(MERCADOPAGO);
                    cobro.setPeriodoEnMeses((byte) 1);
                    cobro.setComision(null);
                    cobro.setMensaje(mensaje);
                    cobro.setTotalAcreditado(totalAcreditado);

                    // si el monto es diferente al totalAcreditado se calcula la comision
                    if (!monto.equals(totalAcreditado)) {
                        cobro.setComision(monto.subtract(totalAcreditado));
                    }

                    email.setIngreso(cobro); // el email crea el cobro en cascada

                    // comprobamos si el cliente ya existe
                    Cliente clienteExistente = clienteRepository.findByEmail(mailCliente);
                    Cliente cliente;
                    if (null == clienteExistente) { // si el cliente es nuevo
                        cliente = new Cliente();
                        cliente.setEmail(mailCliente);
                        clienteRepository.saveAndFlush(cliente);
                    } else {
                        cliente = clienteExistente;
                    }

                    cobro.setCliente(cliente);
                    cobroRepository.saveAndFlush(cobro);

                    // si el cliente tiene modulos asociados 
                    if (null != cliente.getModulos() && !cliente.getModulos().isEmpty()) {
                        // comprobamos que el pago corresponde con el monto de los modulos
                        BigDecimal suma = BigDecimal.ZERO;
                        for (Modulo m : cliente.getModulos()) {
                            suma = suma.add(m.getPrecio());
                        }
                        if (suma.compareTo(cobro.getMonto()) != 0) {
                            // TODO: El pago corresponde a otra cuestion
                        }
                    } else {
                        /*
                        si el cliente no tiene modulos asociados, se infieren los modulos
                        en base al monto abonado. Luego se asocian los modulos correspondientes
                         */
//                        ModuloJpaController moduloDAO = new ModuloJpaController(
//                                Persistence.createEntityManagerFactory(PU));

                        // determinamos cual o cuales modulo/s pago
//                        Modulo moduloBase = moduloDAO.findModulo(MODULO_BASE);
                        Modulo moduloBase = moduloRepository.findByNombre(MODULO_BASE);

                        if (null == moduloBase) {
                            // creo e modulo base la primera vez
                            moduloBase = new Modulo();
                            moduloBase.setAlta(Calendar.getInstance().getTime());
                            moduloBase.setBaja(null);
                            moduloBase.setNombre(MODULO_BASE);
                            moduloBase.setPrecio(new BigDecimal(600));
                            moduloBase.setDescripcion("Sistema Base Megabares");
                            moduloRepository.saveAndFlush(moduloBase);
                            moduloBase.getClientes().add(cliente);
                            moduloRepository.saveAndFlush(moduloBase); // revisar si puedo quitar un save

                        }

                        int comparacion = cobro.getMonto().compareTo(moduloBase.getPrecio());

                        if (comparacion > 0) {
                            // tiene agregados al modudoBase
                        } else if (comparacion == 0) {
                            // solo posee el modulo base
                            moduloBase.getClientes().add(cliente);
                            moduloRepository.saveAndFlush(moduloBase);
                            cliente.getModulos().add(moduloBase);
                        }
                    }
                    // creamos el periodopago con el nuevo codigo
                    PeriodoPago nuevo = crearPeriodoPago(cliente);
                    // lo asociamos al cliente
//                    cliente.getPeriodosPagos().add(nuevo);
//                    clienteDAO.edit(cliente);
                    // enviamos el codigo al cliente
                    enviarCodigoAlCliente(nuevo);
                    // completamos el email y lo persistimos
                    email.setIngreso(cobro);
                    emailRepository.saveAndFlush(email);
                }
            } else {
                log.info(contenidoDelMail);
            }
        }
    }

    private void enviarCodigoAlCliente(PeriodoPago pp) {
//        this.setDestinatario("dieguezfrancisco@gmail.com");
        this.setAsunto("[Megabares] Código Licencia");

        String cuerpo = "<html>\n"
                + "    <head>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <title>Licencia MEGABARES</title>\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <p>El nuevo c&oacute;digo de activaci&oacute;n es:</p>\n"
                + "        <h1>"
                + pp.getCodigoCliente()
                + "</h1>        \n"
                + "        <p>\n"
                + "            <strong><font size=4> Cliente: "
                + pp.getCliente().toString()
                + "</font></strong><br>\n"
                + "        </p>\n"
                + "        <p>\n"
                + "            <strong><font size=4>Sistema MegaBares</font></strong><br>\n"
                + "            Email:&nbsp;<a href=\"mailto:contacto@megabares.com\" target=\"_blank\">contacto@megabares.com</a><br>\n"
                + "            Tel&eacute;fono: &nbsp;<a href=\"tel:+5493424631692\">3424631692(Francisco)</a>&nbsp;/&nbsp;\n"
                + "            <a href=\"tel:+5493424672930\">3424672930(Dami&aacute;n)</a><br>            \n"
                + "            Website:&nbsp;<a href=\"http://www.megabares.com.ar/\" target=\"_blank\">www.megabares.com</a><br>\n"
                + "            WhatsApp:&nbsp;<a href=\"https://api.whatsapp.com/send?phone=+5493424631692\" target=\"_blank\">Francisco</a>&nbsp;/&nbsp;\n"
                + "            <a href=\"https://api.whatsapp.com/send?phone=+5493424672930\" target=\"_blank\">Dami&aacute;n</a>\n"
                + "        </p>\n"
                + "    </body>\n"
                + "</html>";

        this.setCuerpo(cuerpo);
        this.setDestinatario("dieguezfrancisco@gmail.com");
        this.enviar();
        this.setDestinatario("damiandac@gmail.com");
        this.enviar();
//        this.setDestinatario("iriberrypatricia@gmail.com");        
//        this.enviar();

    }

    private PeriodoPago crearPeriodoPago(Cliente cliente) {
        PeriodoPago nuevoPeriodo = null;

        List<PeriodoPago> periodos = cliente.getPeriodosPagos();

        nuevoPeriodo = new PeriodoPago();
        nuevoPeriodo.setCliente(cliente);
        nuevoPeriodo.setMultiplicador((short) 1);
        nuevoPeriodo.setSemilla((short) 2);

        if (null != periodos && !periodos.isEmpty()) { // si tiene periodos previos

            // obtengo el anterior
            PeriodoPago periodoAnterior = periodos.get(periodos.size() - 1);
            short codAnt = periodoAnterior.getCodigo();
            nuevoPeriodo.setCodigo(++codAnt);

            // pago unos dias antes del vencimiento
            if (Calendar.getInstance().getTime().compareTo(periodoAnterior.getHasta()) <= 0) {

                nuevoPeriodo.setDesde(periodoAnterior.getHasta());

            } else { // si pago posterior al vencimiento

                nuevoPeriodo.setDesde(Calendar.getInstance().getTime());

            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(nuevoPeriodo.getDesde());
            cal.add(Calendar.MONTH, 1); // se suma un mes            
            nuevoPeriodo.setHasta(cal.getTime());

        } else {
            // si es el primer periodo
            nuevoPeriodo.setCodigo((short) 1);
            nuevoPeriodo.setDesde(Calendar.getInstance().getTime());
            Calendar hasta = Calendar.getInstance();
            hasta.add(Calendar.MONTH, 1); // se suma un mes         
            nuevoPeriodo.setHasta(hasta.getTime());

        }
        nuevoPeriodo.setNumero(periodoRepository.getMaxNumero() + 1);

        log.info("Nuevo Periodo: " + nuevoPeriodo.toString());

        periodoRepository.saveAndFlush(nuevoPeriodo);
        return nuevoPeriodo;

    }

    /**
     * Metodo que parsea los emails de Recepcion de Pago
     *
     * @param message
     * @throws MessagingException
     * @throws IOException
     */
    private void procesarPago(Message message) throws MessagingException, IOException, NonexistentEntityException, Exception {

        String nombreContraparte = null, telefonoContraparte = null, emailContraparte = null;
        BigDecimal totalAcreditado = null;
        /*
        Datos caracteristicos:
        1 - Pago
        2 - Total Acreditado
        3 - Numero de operacion
        4 - Referencia del vendedor
        5 - Contraparte (pueden venir 3 o menos datos)
            5.1 - Nombre
            5.2 - Telefono
            5.3 - email
        
         */
        // message.setFlag(Flags.Flag.ANSWERED, true);
        contenidoDelMail = null;
        try {
            setContenido(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != contenidoDelMail) {
            Date fecha = message.getReceivedDate();

            // comentamos aca porque ya se antes antes de invocar este metodo
            // message.setFlag(Flags.Flag.SEEN, true); // marca como leido
            String asunto = message.getSubject();
            Address[] from = message.getFrom();
            String destinatarios = VACIO;

            for (Address a : from) {
                destinatarios += a.toString() + 4;
            }

            // ******* email del cliente **********
            int indiceDatos = contenidoDelMail.indexOf("El dinero fue enviado por");
            String datosStr = contenidoDelMail.substring(indiceDatos);
            final String regexEmail = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
            String emailCliente = null;
//            Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",
//                    Pattern.CASE_INSENSITIVE);
//            Matcher matcher = p.matcher(datosStr);
//            Set<String> emails = new HashSet<String>();

//            while (matcher.find()) {
//                emailCliente = matcher.group();
//                if (!emailCliente.equals("dieguezfrancisco@gmail.com")) {
//                    emails.add(matcher.group());
//                }
//            }
//            Optional<String> optMailCliente = emails.stream().findFirst();
//            if (optMailCliente.isPresent()) {
//                emailCliente = optMailCliente.get();
//            } else {
//                emailCliente = "N/A";
//            }
            String[] excluidos = {"dieguezfrancisco@gmail.com", "damiandac@gmail.com"};
            emailCliente = findRegex(regexEmail, datosStr, true, excluidos);
            if (emailCliente == null) {
                emailCliente = "N/A";
            }
            emailContraparte = emailCliente;
            log.info("Email cliente: " + emailCliente);

            // Fin de email del cliente
            // ----------------------------------------------------------------
            // ******* telefono del cliente
            String[] regexsTelefonos = {"^(?:(?:00)?549?)?0?(?:11|[2368]\\d)(?:(?=\\d{0,2}15)\\d{2})??\\d{8}$",
                "(?<=\\s|:)\\(?(?:(0?[1-3]\\d{1,2})\\)?(?:\\s|-)?)?((?:\\d[\\d-]{5}|15[\\s\\d-]{7})\\d+)",
                "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$",
                "^\\+([0-9\\-]?){9,11}[0-9]$"};
            String telefono = null;

            for (int i = 0; i < regexsTelefonos.length; i++) {
                telefono = findRegex(regexsTelefonos[i], datosStr, true, null);
                if (telefono != null) {
                    break;
                }
            }

            if (telefono == null) {
                telefono = "N/A";
            }
            telefonoContraparte = telefono;
            log.info("Teléfono cliente: " + telefono);

            final String regexImporte = "(?:- ?)?(?:\\d{4,}|\\d{1,3}(?:\\.\\d{3})*)(?:,\\d+)?";
            int indiceTotalAcred = contenidoDelMail.indexOf(TOTAL_ACREDITADO);
            if (indiceTotalAcred >= 0) { // si tiene el monto total acreditado continuamos

                // total acreditado
                String totalAcredText = contenidoDelMail.substring(indiceTotalAcred, contenidoDelMail
                        .indexOf(HTML_STRONG_CLOSE, indiceTotalAcred));

                totalAcredText = totalAcredText.substring(totalAcredText.indexOf(SIMBOL_$_ESP) + 2,
                        totalAcredText.length());
//                Cobro cobro = new Cobro();

                totalAcredText = totalAcredText.replaceFirst(HTML_STRONG_CLOSE, VACIO);
                totalAcredText = totalAcredText.replace(PUNTO, VACIO);
                totalAcredText = totalAcredText.replace(COMA, PUNTO);

                try {
                    totalAcreditado = new BigDecimal(totalAcredText.trim());
                } catch (Exception e) {
                    totalAcreditado = null;
                }

                log.info("Total acreditado: " + totalAcreditado);

                // referenciaVendedor del vendedor 
                final String expresionRegularReferencia = "^\\d{11,20}$";
                String referenciaVendedor = findRegex(expresionRegularReferencia, contenidoDelMail, false, null);
                String[] partes = contenidoDelMail.split(" ");
                for (String part : partes) {
                    referenciaVendedor = findRegex(expresionRegularReferencia, part, true, null);
                    if (referenciaVendedor != null) {
                        break;
                    }
                }

                if (referenciaVendedor == null) {
                    referenciaVendedor = "N/A";
                }

                log.info("Referencia de Pago: " + referenciaVendedor);
                // referenciaVendedor megabares
                //^[A-Z]{0,3}\d{0,3}$
                final String megabaresRegex = "^[A-Z]{3}\\d{3}$";
                String refMegabares = findRegex(megabaresRegex, contenidoDelMail, false, null);
                for (String p : partes) {
                    refMegabares = findRegex(megabaresRegex, p, true, null);
                    if (refMegabares != null) {
                        break;
                    }
                }

                if (refMegabares == null) {
                    refMegabares = "N/A";
                }

                log.info("Referencia Megabares: " + refMegabares);

                int indDePago = contenidoDelMail.indexOf(PAGO);

                // ************************************************
                // *************** FIN PARSEO EMAIL ***************
                // ************************************************
                // si el mail existe, ya fue analizado previamente y es repetido
                Email emailExistente = emailRepository.findByFechaAndAsunto(fecha, asunto);

                if (null != emailExistente) {
                    // El email ya se proceso previamente
                    System.err.print("Email repetido: " + asunto + " - " + fecha.toString());
                } else {
                    /*                    
                    - 1 Registrar el correo si no es repetido
                    - 2 Registrar el cilente si no existe
                    - 3 Registrar el pago. tipo y monto
                    - 4 Asociar modulos pagados si es pago de licencias
                    - 5 Enviar codigo activacion si es un pago de licencia
                    
                    La secuencia deberia ser la siguiente:
                    Email => Ingreso => Cliente => modulo => periodoPago
                    
                    Para el caso del cliente y modulo en caso de no existir, se crean.
                     */
                    Email email = new Email(fecha, MERCADOPAGO, contenidoDelMail, asunto,
                            destinatarios, TipoEmail.PAGO);

                    Cobro cobro = new Cobro();
                    cobro.setFecha(fecha);
                    cobro.setMonto(totalAcreditado);
                    cobro.setNroMercadoPago(Long.valueOf(referenciaVendedor));
                    cobro.setConcepto(TipoEmail.DINERO.name());
                    cobro.setTipoPago(MERCADOPAGO);
                    cobro.setPeriodoEnMeses((byte) 1);
                    cobro.setComision(null);
                    cobro.setMensaje(refMegabares);
                    cobro.setTotalAcreditado(totalAcreditado);

                    // si el monto es diferente al totalAcreditado se calcula la comision
                    // TODO: revisar el calculo de comision
//                    if (!pago.equals(totalAcreditado)) {
//                        cobro.setComision(pago.subtract(totalAcreditado));
//                    } else {
//                        cobro.setComision(null);
//                    }
                    email.setIngreso(cobro); // el email crea el cobro en cascada

                    // comprobamos si el cliente ya existe
//                    Cliente clienteExistente = clienteDAO.findCliente(emailContraparte);
                    Cliente clienteExistente = clienteRepository.findByEmail(emailContraparte);
                    Cliente cliente;
                    if (null == clienteExistente) { // si el cliente es nuevo
                        cliente = new Cliente();

                        if (null != emailContraparte && !emailContraparte.isEmpty()) {

                            cliente.setEmail(emailContraparte);
                        }

                        if (null != nombreContraparte && !nombreContraparte.isEmpty()) {

                            String[] na = nombreContraparte.split(ESPACIO);
                            final String APELLIDO = na[na.length - 1];
                            final String NOMBRE = na[0];

                            if (!NOMBRE.isEmpty()) {
                                cliente.setNombre(NOMBRE);
                            }
                            if (!APELLIDO.isEmpty()) {
                                cliente.setApellido(APELLIDO);
                            }

                        }

                        if (null != telefonoContraparte && !telefonoContraparte.isEmpty()) {
                            cliente.setTelefono(telefonoContraparte.trim());
                        }

                        Optional<Long> optMaxNumero = Optional.of(clienteRepository.getMaxNumero());
                        if (optMaxNumero.isPresent()) {
                            cliente.setNumero(optMaxNumero.get() + 1);
                        }

                        clienteRepository.saveAndFlush(cliente);

                    } else {
                        // si el cliente no es nuevo. Chequeamos que haya que actualizar datos
                        cliente = clienteExistente;
                        cliente = actualizarDatos(cliente, nombreContraparte, emailContraparte, telefonoContraparte);
                    }

                    cobro.setCliente(cliente);

                    Optional<Long> optMaxNumero = Optional.of(cobroRepository.getMaxId());
                    if (optMaxNumero.isPresent()) {
                        cobro.setNumero(optMaxNumero.get() + 1);
                    }

                    cobro = cobroRepository.saveAndFlush(cobro);

                    // si el cliente tiene modulos asociados y pago licencias
                    // chequeamos que pago todas
                    Modulo moduloBase = moduloRepository.findByNombre(MODULO_BASE);
                    Modulo moduloStock = moduloRepository.findByNombre(MODULO_STOCK);
                    Modulo moduloFiscal = moduloRepository.findByNombre(MODULO_FISCAL);
                    Modulo moduloOld = moduloRepository.findByNombre(MODULO_OLD);

                    PeriodoPago nuevo = null;
                    if (refMegabares.contains("LIC")) {

                        switch (getTipoPago(refMegabares)) {

                            case LIC001: // base

                                if (!cliente.getModulos().contains(moduloBase)) {
                                    cliente.getModulos().add(moduloBase);
                                    moduloBase.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloBase);
                                }

                                // creamos el periodopago con el nuevo codigo
                                nuevo = crearPeriodoPago(cliente);

                                // lo asociamos al cliente
//                    cliente.getPeriodosPagos().add(nuevo);
//                    clienteDAO.edit(cliente);
                                // enviamos el codigo al cliente
                                enviarCodigoAlCliente(nuevo);

                                break;
                            case LIC002: // base + fiscal
                                // mod base
                                try { // aca rompe
                                if (cliente.getModulos() == null) {
                                    log.error("El cliente no tiene asociado ningun modulo");
                                    cliente.setModulos(new HashSet<>());
                                    cliente.getModulos().add(moduloBase);
                                    moduloBase.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloBase);
                                } else if (!cliente.getModulos().contains(moduloBase)) {
                                    cliente.getModulos().add(moduloBase);
                                    moduloBase.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloBase);
                                }
                            } catch (LayerInstantiationException e) {
                                log.error("El cliente no tiene asociado ningun modulo");
                                cliente.setModulos(new HashSet<>());
                                cliente.getModulos().add(moduloBase);
                                moduloBase.getClientes().add(cliente);
                                clienteRepository.saveAndFlush(cliente);
                                moduloRepository.saveAndFlush(moduloBase);
                            }

                            // mod fiscal
                            if (!cliente.getModulos().contains(moduloFiscal)
                                    && moduloFiscal != null) {
                                cliente.getModulos().add(moduloFiscal);
                                moduloFiscal.getClientes().add(cliente);
                                clienteRepository.save(cliente);
                                moduloRepository.save(moduloFiscal);
                            }

                            // creamos el periodopago con el nuevo codigo
                            nuevo = crearPeriodoPago(cliente);
                            enviarCodigoAlCliente(nuevo);

                            break;

                            case LIC003: // base + stock
                                if (!cliente.getModulos().contains(moduloBase)) { // base
                                    cliente.getModulos().add(moduloBase);
                                    moduloBase.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloBase);
                                }
                                if (!cliente.getModulos().contains(moduloStock)) { // stock
                                    cliente.getModulos().add(moduloStock);
                                    moduloStock.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloStock);
                                }

                                break;
                            case LIC004: // base + stock + fiscal
                                if (!cliente.getModulos().contains(moduloBase)) { // base
                                    cliente.getModulos().add(moduloBase);
                                    moduloBase.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloBase);
                                }
                                if (!cliente.getModulos().contains(moduloStock)) { // stock
                                    cliente.getModulos().add(moduloStock);
                                    moduloStock.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloStock);
                                }
                                if (!cliente.getModulos().contains(moduloFiscal)) { // fiscal
                                    cliente.getModulos().add(moduloFiscal);
                                    moduloFiscal.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloFiscal);
                                }
                                break;
                            case LIC005: // licencias viejas con comodato
                                // base
                                if (!cliente.getModulos().contains(moduloOld)) {
                                    cliente.getModulos().add(moduloOld);
                                    moduloOld.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloOld);
                                }
                                // creamos el periodopago con el nuevo codigo
                                nuevo = crearPeriodoPago(cliente);
                                enviarCodigoAlCliente(nuevo);

                                break;

                            case LIC006: // licencias viejas con comodato
                                // base
                                if (!cliente.getModulos().contains(moduloOld)) {
                                    cliente.getModulos().add(moduloOld);
                                    moduloOld.getClientes().add(cliente);
                                    clienteRepository.saveAndFlush(cliente);
                                    moduloRepository.saveAndFlush(moduloOld);
                                }
                                // creamos el periodopago con el nuevo codigo
                                nuevo = crearPeriodoPago(cliente);
                                enviarCodigoAlCliente(nuevo);
                                break;

                        }

                    } else if (referenciaVendedor.contains("COB")) {

                        // hacer algo con los cobros generales
                    } else if (referenciaVendedor.contains("ING")) {
                        // hacer algo con los ingresoss de dinero

                    }

                    // creamos el periodopago con el nuevo codigo
                    if (nuevo == null) {
                        nuevo = crearPeriodoPago(cliente);
                    }

                    // completamos el email y lo persistimos
                    email.setIngreso(cobro);

                    /*
                    TODO: continuar error de ingreso 
                    
                    Caused by: org.hibernate.PersistentObjectException: detached entity passed to persist: com.megabares.api.data.Ingreso
                    
                     */
                    email.setNumero(emailRepository.getMaxNumero()+1);
                    emailRepository.saveAndFlush(email);
                }

            } else {
                log.info(contenidoDelMail);
            }
        }
    }

    private String findRegex(String regex, String datosStr, boolean caseInsensitive,
            String[] excluidos) {
        Pattern pattern = null;
        if (caseInsensitive) {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile(regex);
        }
        Set<String> buscados = new HashSet<String>();
        String buscado = null;
        Matcher matcher = pattern.matcher(datosStr);
        boolean excluir;
        while (matcher.find()) {
            excluir = false;
            buscado = matcher.group();

            // chequeamos que no este excluido
            if (excluidos != null) {
                for (String e : excluidos) {
                    if (buscado.trim().equals(e)) {
                        excluir = true;
                    }
                }
            }
            // si no esta excluido se agrega
            if (!excluir) {
                buscados.add(buscado);
            }
        }

        Optional<String> optTelCliente = buscados.stream().findFirst();
        if (optTelCliente.isPresent()) {
            buscado = optTelCliente.get();
            return buscado;
        }
        return null;
    }

    private Cliente actualizarDatos(Cliente cliente, String nombreContraparte, String emailContraparte, String telefonoContraparte) {

        String nombre = null, apellido = null;
        if (nombreContraparte != null) {
            String[] nc = nombreContraparte.split(ESPACIO); // nombre completo
            nombre = nc[0].trim();
            apellido = nc[nc.length - 1].trim();
        }

        if (nombre != null && !nombre.isEmpty() && !cliente.getNombre().equals(nombre)) {
            cliente.setNombre(nombre);
        }
        if (apellido != null && !apellido.isEmpty() && !cliente.getApellido().equals(apellido)) {
            cliente.setApellido(apellido);
        }

        // validamos los emails si no esta guardado lo agregamos como email secundario
        if (!cliente.getEmail().equals(emailContraparte)) {

            if (!cliente.getEmailsSecundarios().contains(emailContraparte)) {

                // si hay espacio se guarda como secundario
                if ((cliente.getEmailsSecundarios() + emailContraparte).length() < 100) {
                    cliente.setEmailsSecundarios(cliente.getEmailsSecundarios() + emailContraparte);
                }
            }
        }

        return cliente;
    }

    /**
     * @autor fdieguez crea los modulos la primera vez
     */
    private void crearModulos() {

        if (null == moduloRepository.findByNombre(MODULO_BASE)) {
            Modulo moduloBase = new Modulo();
            moduloBase.setAlta(Calendar.getInstance().getTime());
            moduloBase.setBaja(null);
            moduloBase.setNombre(MODULO_BASE);
            moduloBase.setPrecio(new BigDecimal(600));
            moduloBase.setDescripcion("Modulo Base");
            moduloRepository.saveAndFlush(moduloBase);
        }

        if (null == moduloRepository.findByNombre(MODULO_STOCK)) {
            Modulo moduloStock = new Modulo();
            moduloStock.setAlta(Calendar.getInstance().getTime());
            moduloStock.setBaja(null);
            moduloStock.setNombre(MODULO_STOCK);
            moduloStock.setPrecio(new BigDecimal(600));
            moduloStock.setDescripcion("Modulo Stock");
            moduloRepository.saveAndFlush(moduloStock);
        }

        if (null == moduloRepository.findByNombre(MODULO_FISCAL)) {
            Modulo moduloFiscal = new Modulo();
            moduloFiscal.setAlta(Calendar.getInstance().getTime());
            moduloFiscal.setBaja(null);
            moduloFiscal.setNombre(MODULO_STOCK);
            moduloFiscal.setPrecio(new BigDecimal(600));
            moduloFiscal.setDescripcion("Modulo Fiscal");
            moduloRepository.saveAndFlush(moduloFiscal);
        }

    }
}
