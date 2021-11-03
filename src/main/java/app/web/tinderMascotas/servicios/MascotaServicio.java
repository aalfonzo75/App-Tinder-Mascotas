package app.web.tinderMascotas.servicios;

import app.web.tinderMascotas.entidades.Foto;
import app.web.tinderMascotas.entidades.Mascota;
import app.web.tinderMascotas.entidades.Usuario;
import app.web.tinderMascotas.enumeraciones.Sexo;
import app.web.tinderMascotas.excepciones.ErrorServicio;
import app.web.tinderMascotas.repositorios.MascotaRepositorio;
import app.web.tinderMascotas.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aleidy Alfonzo
 */
@Service
public class MascotaServicio {

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private FotoServicio fotoServicio;

@Transactional
    public void agregarMascota(MultipartFile archivo,String idUsuario, String nombre, Sexo sexo) throws ErrorServicio {
        //Buscamos el usuario para la mascota
        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();

        //Hacemos las validaciones
        validarDatos(nombre, sexo);

        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setSexo(sexo);
        mascota.setAlta(new Date());
        
        //Cargamos una foto para la mascota
         Foto foto=fotoServicio.guardar(archivo);
        mascota.setFoto(foto);
        
        //Guardamos los valores en la DB
        mascotaRepositorio.save(mascota);
    }

    @Transactional
    public void modificarMascota(MultipartFile archivo,String idUsuario, String idMascota, String nombre, Sexo sexo) throws ErrorServicio {
        validarDatos(nombre, sexo);
        
        //Comprobamos si la mascota existe
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);

        if (respuesta.isPresent()) {
            Mascota mascota = respuesta.get();
            
            //Comprobamos que el id del usuario sea equivalente al id que traemos
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);
                
                 //Inicializamos un String de idFoto
                String idFoto=null;
                
                 //Modificamos el idFoto
            if (mascota.getFoto() !=null) {
                idFoto=mascota.getFoto().getId();                              
            }
            
             //Modificamos la foto para el usuario
            Foto foto=fotoServicio.actualizar(idFoto, archivo);
            mascota.setFoto(foto);
            
                //Guardamos los valores en la DB
                mascotaRepositorio.save(mascota);
            } else {
                throw new ErrorServicio("No tiene servicios suficientes para realizar la operación");
            }
        } else {
            throw new ErrorServicio("No existe una mascota con el id solicitado");
        }
    }
    
    @Transactional
    public void eliminarMascota(String idUsuario, String idMascota) throws ErrorServicio {

         //Comprobamos si la mascota existe
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);

        if (respuesta.isPresent()) {
            Mascota mascota = respuesta.get();
            
             //Comprobamos que el id del usuario sea equivalente al id que traemos
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setBaja(new Date());
                mascotaRepositorio.save(mascota);
            } else {
                throw new ErrorServicio("No tiene servicios suficientes para realizar la operación");
            }
        } else {
            throw new ErrorServicio("No existe una mascota con el id solicitado");
        }
    }

    public void validarDatos(String nombre, Sexo sexo) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre de la mascota no puede ser nulo o vacio");
        }
        if (sexo == null) {
            throw new ErrorServicio("El sexo de la mascota no puede ser nulo");
        }

    }

}
