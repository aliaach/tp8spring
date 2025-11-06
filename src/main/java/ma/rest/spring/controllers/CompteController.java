package ma.rest.spring.controllers;

import ma.rest.spring.entities.Compte;
import ma.rest.spring.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/banque")
public class CompteController {

    @Autowired
    private CompteRepository compteRepository;

    // READ : tous les comptes (JSON + XML)
    @GetMapping(value = "/comptes", produces = {"application/json", "application/xml"})
    public List<Compte> getAllComptes() {
        return compteRepository.findAll();
    }

    // READ : compte par ID (JSON + XML)
    @GetMapping(value = "/comptes/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<Compte> getCompteById(@PathVariable Long id) {
        return compteRepository.findById(id)
                .map(c -> ResponseEntity.ok().body(c))
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE : nouveau compte (JSON + XML)
    @PostMapping(
            value = "/comptes",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"}
    )
    public ResponseEntity<Compte> createCompte(@RequestBody Compte compte) {
        Compte saved = compteRepository.save(compte);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    // UPDATE : mise à jour (JSON + XML)
    @PutMapping(
            value = "/comptes/{id}",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"}
    )
    public ResponseEntity<Compte> updateCompte(@PathVariable Long id,
                                               @RequestBody Compte compteDetails) {
        return compteRepository.findById(id)
                .map(c -> {
                    c.setSolde(compteDetails.getSolde());
                    c.setDateCreation(compteDetails.getDateCreation());
                    c.setType(compteDetails.getType());
                    Compte updated = compteRepository.save(c);
                    return ResponseEntity.ok().body(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/comptes/{id}")
    public ResponseEntity<Void> deleteCompte(@PathVariable Long id) {
        var optionalCompte = compteRepository.findById(id);

        if (optionalCompte.isPresent()) {
            compteRepository.delete(optionalCompte.get());
            // 204 No Content, générique <Void>
            return ResponseEntity.noContent().build();
        } else {
            // 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

}
