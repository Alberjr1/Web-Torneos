package practica1.artefacto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import practica1.artefacto.model.Team;
import practica1.artefacto.service.TeamService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public Team create(@RequestBody Team team) {
        return teamService.create(team);
    }

    @GetMapping("/{id}")
    public Team read(@PathVariable Long id) {
        return teamService.read(id);
    }

    @GetMapping
    public List<Team> getAll() {
        return teamService.getAll();
    }

    @PutMapping("/{id}")
    public Team update(@PathVariable Long id, @RequestBody Team team) {
        return teamService.update(id, team);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teamService.delete(id);
    }

    @PatchMapping("/{id}")
    public Team patch(@PathVariable Long id,
                      @RequestBody Map<String, Object> updates) {
        return teamService.patch(id, updates);
    }
}