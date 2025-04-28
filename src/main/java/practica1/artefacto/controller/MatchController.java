package practica1.artefacto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practica1.artefacto.model.Match;
import practica1.artefacto.service.MatchService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Match match) {
        try {
            Match created = matchService.create(match);
            return ResponseEntity.ok(matchService.getMatchWithTeams(created.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public Match patch(@PathVariable Long id,@RequestBody Map<String,Object> updates){
        return matchService.patch(id,updates);
    }

    @GetMapping
    public List<Match> getAll() {
        return matchService.getAllWithTeams();
    }

    @PutMapping("/{id}")
    public Match update(@PathVariable Long id, @RequestBody Match match) {
        return matchService.update(id, match);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        matchService.delete(id);
    }



}
