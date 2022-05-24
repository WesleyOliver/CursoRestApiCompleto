package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

//@CrossOrigin(origins = {"http://www.google.com.br"})
@RestController
@RequestMapping("/usuarios")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@GetMapping("/{id}")
	@CacheEvict(value = "cacheuser", allEntries = true)
	@CachePut(value = "cacheuser")
	public ResponseEntity<Usuario> init(@PathVariable("id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	// @CrossOrigin(origins = "*")
	@GetMapping
	//@Cacheable(value = "cacheusuarios")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut(value = "cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarios(){
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		//Thread.sleep(6000);
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		/*
		 * for(int i = 0; i < usuario.getTelefones().size(); i++) {
		 * usuario.getTelefones().get(i).setUsuario(usuario); }
		 */
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

		usuario.getTelefones().forEach(telefone -> {
			telefone.setUsuario(usuario);
		});

		Usuario usuarioRetorno = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioRetorno, HttpStatus.CREATED);
	}

	@PutMapping
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		usuario.getTelefones().forEach(telefone -> {
			telefone.setUsuario(usuario);
		});

		Usuario userTemp = usuarioRepository.findUserByLogin(usuario.getLogin());

		if (!usuario.getSenha().equals(userTemp.getSenha())) {
			usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}

		Usuario usuarioRetorno = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioRetorno, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		return new ResponseEntity<String>("ok", HttpStatus.OK);
	}

}
