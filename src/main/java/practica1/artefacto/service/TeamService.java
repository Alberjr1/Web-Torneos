package practica1.artefacto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practica1.artefacto.model.Team;
import practica1.artefacto.repository.TeamRepository;

import java.util.List;
import java.util.Map;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public Team create(Team team) {
        return teamRepository.save(team);
    }

    public Team read(Long id) {
        System.out.println("Reading team with ID: " + id);
        return teamRepository.findById(id).orElse(null);
    }

    public Team update(Long id, Team team) {
        team.setId(id);
        return teamRepository.save(team);
    }

    public void delete(Long id) {
        teamRepository.deleteById(id);
    }

    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    public Team patch(Long id, Map<String, Object> updates) {
        return teamRepository.findById(id).map(team -> {

            updates.forEach((k, v) -> {
                switch (k) {
                    case "name"   -> team.setName((String) v);
                    case "coach"  -> team.setCoach((String) v);
                    case "badge"  -> team.setBadge((String) v);
                }
            });
            return teamRepository.save(team);
        }).orElse(null);
    }
}