package app.web.tinderMascotas.servicios;

import app.web.tinderMascotas.entidades.Mascota;
import app.web.tinderMascotas.entidades.Voto;
import app.web.tinderMascotas.excepciones.ErrorServicio;
import app.web.tinderMascotas.repositorios.MascotaRepositorio;
import app.web.tinderMascotas.repositorios.VotoRepositorio;
import java.util.Date;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aleidy Alfonzo
 */
@Service
public class VotoServicio {

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private VotoRepositorio votoRepositorio;

    @Transactional
    public void votar(String idUsuario, String idMascota1, String idMascota2) throws ErrorServicio {
        //Le asignamos una fecha al voto
        Voto voto = new Voto();
        voto.setFecha(new Date());

        //Comprobamos que la votacion sea a otra mascota
        if (idMascota1.equals(idMascota2)) {
            throw new ErrorServicio("No puede votarse a si mismo");
        }

        //Comprobamos si la mascota existe
        Optional<Mascota> respuesta1 = mascotaRepositorio.findById(idMascota1);
        if (respuesta1.isPresent()) {
            Mascota mascota1 = respuesta1.get();
            //Verificamos si el usuario es duenio de la mascota 1
            if (mascota1.getUsuario().getId().equals(idUsuario)) {
                voto.setMascota1(mascota1);
            } else {
                throw new ErrorServicio("No tiene permisos para realizar la operacion solicitada");
            }
        } else {
            throw new ErrorServicio("No existe una mascota vinculada con ese id");
        }

        //Comprobamos si la mascota existe
        Optional<Mascota> respuesta2 = mascotaRepositorio.findById(idMascota2);
        if (respuesta2.isPresent()) {
            Mascota mascota2 = respuesta2.get();
            voto.setMascota2(mascota2);

            //Notificacion Mail del dueño mascota votada
            notificacionServicio.enviar("Tu mascota ha sido votada!", "Tinder de Mascota", mascota2.getUsuario().getMail());

        } else {
            throw new ErrorServicio("No existe una mascota vinculada con ese id");
        }
        //Guardamos los valores en la DB
        votoRepositorio.save(voto);
    }
    
    @Transactional
    public void responder(String idUsuario, String idVoto) throws ErrorServicio {
        //Comprobamos si el voto existe
        Optional<Voto> respuesta = votoRepositorio.findById(idVoto);
        if (respuesta.isPresent()) {
            Voto voto = respuesta.get();
            voto.setRespuesta(new Date());

            if (voto.getMascota2().getUsuario().getId().equals(idUsuario)) {
                //Guardamos los valores en la DB
                votoRepositorio.save(voto);
            } else {
                throw new ErrorServicio("No tiene permisos para realizar la operacion solicitada");
            }
        } else {
            throw new ErrorServicio("No existe el voto solicitado");
        }
    }

}
