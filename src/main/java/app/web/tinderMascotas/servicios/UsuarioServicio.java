package app.web.tinderMascotas.servicios;

import app.web.tinderMascotas.entidades.Foto;
import app.web.tinderMascotas.entidades.Usuario;
import app.web.tinderMascotas.excepciones.ErrorServicio;
import app.web.tinderMascotas.repositorios.FotoRepositorio;
import app.web.tinderMascotas.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aleidy Alfonzo
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private FotoServicio fotoServicio;

    @Transactional
    public void registrarUsuario(MultipartFile archivo, String nombre, String apellido, String mail, String clave) throws ErrorServicio {

        // Validamos
        validarDatos(nombre, apellido, mail, clave);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        usuario.setAlta(new Date());

        //Encriptamos la clave
        String claveEncriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(claveEncriptada);

        //Cargamos una foto para el usuario
        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        //Guardamos los valores en la DB
        usuarioRepositorio.save(usuario);

//        Cuando el usuario se registra se le da la bienvenida
        notificacionServicio.enviar("Bienvenidos al Tinder de Mascotas!", "Tinder de Mascota", usuario.getMail()); // mail y titulo del usuario recien registrado
    }

    @Transactional
    public void modificarUsuario(MultipartFile archivo, String id, String nombre, String apellido, String mail, String clave) throws ErrorServicio {
        //Validamos
        validarDatos(nombre, apellido, mail, clave);

        //Comprobamos si el usuario existe
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            //Modificamos los valores
            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);

            //Encriptamos la clave
            String claveEncriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(claveEncriptada);

            //Inicializamos un String de idFoto
            String idFoto = null;

            //Modificamos el idFoto
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            //Modificamos la foto para el usuario
            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            //Guardamos los valores en la DB
            usuarioRepositorio.save(usuario);

        } else {
            throw new ErrorServicio("No se encontro el usuario con el id solicitado");
        }
    }

    @Transactional
    public void Deshabilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);

        } else {
            throw new ErrorServicio("No se encontro el usuario con el id solicitado");
        }
    }

    @Transactional
    public void Habilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(null);
            usuarioRepositorio.save(usuario);

        } else {
            throw new ErrorServicio("No se encontro el usuario con el id solicitado");
        }
    }

    public void validarDatos(String nombre, String apellido, String mail, String clave) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("La clave del usuario no puede ser nulo");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException { //Este metodo se usa para que el usuario se autentifique en la plataforma
        //Buscamos el usuario por su mail
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);

        if (usuario != null) {

            //Listado de permisos
            List<GrantedAuthority> permisos = new ArrayList<>();

            //Creamos los permisos
            GrantedAuthority p1 = new SimpleGrantedAuthority("MODULO_FOTOS");
            GrantedAuthority p2 = new SimpleGrantedAuthority("MODULO_MASCOTAS");
            GrantedAuthority p3 = new SimpleGrantedAuthority("MODULO_VOTOS");

            //Agregamos los permisos a la lista
            permisos.add(p1);
            permisos.add(p2);
            permisos.add(p3);

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);

            //Retornamos el user
            return user;

        } else {

            return null;

        }

    }

}
