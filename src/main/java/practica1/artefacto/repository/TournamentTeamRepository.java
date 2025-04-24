package practica1.artefacto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.artefacto.model.TournamentTeam;
import practica1.artefacto.model.TournamentTeamId;

import java.util.List;

public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, TournamentTeamId> {
    List<TournamentTeam> findByTournamentId(Long tournamentId);
    List<TournamentTeam> findByTeamId(Long teamId);
}
