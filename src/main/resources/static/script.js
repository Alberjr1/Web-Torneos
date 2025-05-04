document.addEventListener('DOMContentLoaded', async function () {
    const API_BASE_URL = 'http://localhost:8080/api';
    let isAdmin = false;

    /* ======================= User / Roles ======================= */
    async function fetchData(endpoint) {
        const res = await fetch(`${API_BASE_URL}/${endpoint}`);
        return res.json();
    }

    // carga info de /api/users/me para saber roles
    async function loadUserInfo() {
        try {
            const me = await fetchData('users/me');
            isAdmin = Array.isArray(me.roles) && me.roles.includes('ROLE_ADMIN');
        } catch (e) {
            console.warn('Error al cargar la información de usuario', e);
        }
    }
    await loadUserInfo();

    /* ======================= Helpers ======================= */
    async function postData(endpoint, data) {
        const res = await fetch(`${API_BASE_URL}/${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });
        return res.json();
    }

    async function deleteData(endpoint, id) {
        await fetch(`${API_BASE_URL}/${endpoint}/${id}`, { method: 'DELETE' });
    }

    async function patchEntity(resource, id, payload) {
        const res = await fetch(`${API_BASE_URL}/${resource}/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        return res.json();
    }

    function showModal(message) {
        const modal = document.getElementById('modal-alert');
        const msgEl = document.getElementById('modal-message');
        msgEl.textContent = message;
        modal.style.display = 'block';
    }
    function hideModal() {
        document.getElementById('modal-alert').style.display = 'none';
    }
    document.getElementById('modal-close').addEventListener('click', hideModal);

    /* ---------- EDIT-MODAL helpers ---------- */
    const editModal  = document.getElementById('edit-modal');
    const editTitle  = document.getElementById('edit-title');
    const editFields = document.getElementById('edit-fields');
    const editForm   = document.getElementById('edit-form');
    document.getElementById('edit-close').onclick = () => editModal.style.display = 'none';
    document.getElementById('edit-cancel').onclick = () => editModal.style.display = 'none';

    function openEditModal(entity, id, data, fields, cbRefresh) {
        editTitle.textContent = `Edit ${entity.slice(0,-1)} #${id}`;
        editFields.innerHTML = '';
        fields.forEach(f => {
            editFields.insertAdjacentHTML('beforeend', `
                <label style="display:block;margin:.5rem 0 .2rem">
                  ${f.charAt(0).toUpperCase()+f.slice(1)}
                </label>
                <input name="${f}" value="${data[f] ?? ''}" style="width:100%;padding:.4rem">
            `);
        });
        editForm.onsubmit = async ev => {
            ev.preventDefault();
            const formData = new FormData(editForm);
            const patch = {};
            fields.forEach(f => {
                const v = formData.get(f);
                if (v !== '' && v !== String(data[f] ?? '')) patch[f] = v;
            });
            // validación de fecha en pasado
            if (patch.date) {
                const today = new Date(); today.setHours(0,0,0,0);
                const sel   = new Date(patch.date);
                if (sel < today) {
                    showModal('Date cannot be in the past.');
                    return;
                }
            }
            await patchEntity(entity, id, patch);
            cbRefresh();
            editModal.style.display = 'none';
        };
        editModal.style.display = 'block';
    }

    // Global function for updating tournaments lists across different views
    async function updateTournamentsList() {
        const arr = Object.values(await fetchData('tournaments'));
        const tournamentsList = document.getElementById('tournaments-list');
        if (!tournamentsList) return; 
        
        tournamentsList.innerHTML = '';
        arr.forEach(t => {
            tournamentsList.insertAdjacentHTML('beforeend', `
              <div class="list-item">
                <strong>${t.name}</strong> – ${t.date} – ${t.location}
                <button data-id="${t.id}" class="edit-tournament">Edit</button>
                <button data-id="${t.id}" class="delete-tournament">Delete</button>
                <div class="details">
                  <p>Teams: ${t.teamIds.length}</p>
                  <p>Matches: ${t.matchIds.length}</p>
                </div>
              </div>
            `);
        });
    }

    // Move updateMatchesList to global scope like we did with tournaments
    async function updateMatchesList() {
        const matchesList = document.getElementById('matches-list');
        if (!matchesList) return; 
        
        const arr = Object.values(await fetchData('matches'));
        matchesList.innerHTML = '';
        for (const m of arr) {
            
            if (!m.team1Id || !m.team2Id || !m.tournamentId) continue;
            
            try {
                const t1 = await fetchData(`teams/${m.team1Id}`);
                const t2 = await fetchData(`teams/${m.team2Id}`);
                const tr = await fetchData(`tournaments/${m.tournamentId}`);
                
                if (!t1 || !t2 || !tr) continue;
                
                matchesList.insertAdjacentHTML('beforeend', `
                  <div class="list-item">
                    <div class="match-card">
                      <div class="team-info">
                        <img src="${t1.badge}" class="team-logo"><strong>${t1.name}</strong>
                      </div>
                      <span>vs</span>
                      <div class="team-info">
                        <img src="${t2.badge}" class="team-logo"><strong>${t2.name}</strong>
                      </div>
                      <div class="match-details">
                        <p><strong>Tournament:</strong> ${tr.name}</p>
                        <p><strong>Date:</strong> ${m.date} – <strong>Time:</strong> ${m.time}</p>
                      </div>
                      <button data-id="${m.id}" class="edit-match">Edit</button>
                      <button data-id="${m.id}" class="delete-match">Delete</button>
                    </div>
                  </div>
                `);
            } catch (error) {
                console.warn(`Error loading match details for match ${m.id}:`, error);
            }
        }
    }

    /* ======================= Load Line-up ======================= */
    function loadTeamLineup(teamId) {
        const soccerField = document.querySelector('.soccer-field');
        if (!soccerField) return;
        fetchData(`teams/${teamId}`)
          .then(team => {
            if (!team) return;
            let lineup = team.players || {};
            const key = team.name.trim().toLowerCase();
            if (!Object.keys(lineup).length && defaultTeamLineups[key]) {
                lineup = defaultTeamLineups[key];
            }
            soccerField.querySelectorAll('.position').forEach(posEl => {
                const pos = posEl.dataset.position;
                posEl.textContent = lineup[pos] || pos;
            });
        });
    }
    // si venimos con ?teamId=… en URL
    const teamsView = document.getElementById('teams-view');
    if (teamsView) {
        const params = new URLSearchParams(window.location.search);
        const teamId = params.get('teamId');
        if (teamId) {
            loadTeamLineup(+teamId);
            document.getElementById('players-field').style.display = 'block';
        }
    }

    /* ======================= Tournaments ======================= */
    if (document.getElementById('tournament-form')) {
        const form = document.getElementById('tournament-form');
        const list = document.getElementById('tournaments-list');

        form.addEventListener('submit', async e => {
            e.preventDefault();
            if (!isAdmin) {
                showModal('Debes ser administrador para añadir torneos');
                return;
            }
            const name = form['tournamentName'].value;
            const date = form['tournamentDate'].value;
            const loc  = form['tournamentLocation'].value;
            const td   = new Date(date), today = new Date(); today.setHours(0,0,0,0);
            if (td < today) {
                showModal('Cannot create a tournament with a date in the past.');
                return;
            }
            await postData('tournaments', { name, date, location: loc, teams: [], matches: [] });
            updateTournamentsList();
            form.reset();
        });

        list.addEventListener('click', async e => {
            const id = e.target.dataset.id;
            if (e.target.classList.contains('delete-tournament')) {
                if (!isAdmin) {
                    showModal('You must be an admin to delete tournaments');
                    return;
                }
                await deleteData('tournaments', id);
                updateTournamentsList();
                updateMatchesList(); 
            }
            if (e.target.classList.contains('edit-tournament')) {
                if (!isAdmin) {
                    showModal('You must be an admin to edit tournaments');
                    return;
                }
                const t = await fetchData(`tournaments/${id}`);
                openEditModal('tournaments', id, t, ['name','date','location'], updateTournamentsList);
            }
        });

        updateTournamentsList();
    }

    /* ======================= Teams ======================= */
    if (document.getElementById('team-form')) {
        const form = document.getElementById('team-form'),
              list = document.getElementById('teams-list');

        form.addEventListener('submit', async e => {
            e.preventDefault();
            if (!isAdmin) {
                showModal('You must be an admin to create teams');
                return;
            }
            const name  = form['teamName'].value;
            const coach = form['teamCoach'].value;
            const badge = `${API_BASE_URL}/logos/${name.toLowerCase()}`;
            await postData('teams', { name, coach, badge });
            updateTeamsList();
            form.reset();
        });

        async function updateTeamsList() {
            const arr = Object.values(await fetchData('teams'));
            list.innerHTML = '';
            arr.forEach(t => {
                list.insertAdjacentHTML('beforeend', `
                  <div class="list-item">
                    <img src="${t.badge}" class="team-logo">
                    <strong>${t.name}</strong> – Coach: ${t.coach}
                    <button data-id="${t.id}" class="show-lineup">Show Line-up</button>
                    <button data-id="${t.id}" class="edit-team">Edit</button>
                    <button data-id="${t.id}" class="delete-team">Delete</button>
                  </div>
                `);
            });
        }

        list.addEventListener('click', async e => {
            const id = e.target.dataset.id;
            if (e.target.classList.contains('delete-team')) {
                if (!isAdmin) {
                    showModal('You must be an admin to delete teams');
                    return;
                }
                await deleteData('teams', id);
                updateTeamsList();
            }
            if (e.target.classList.contains('show-lineup')) {
                const inTeams = window.location.pathname.endsWith('teams.html');
                if (inTeams) {
                    loadTeamLineup(+id);
                    document.getElementById('players-field').style.display = 'block';
                } else {
                    window.location.href = `teams.html?teamId=${id}`;
                }
            }
            if (e.target.classList.contains('edit-team')) {
                if (!isAdmin) {
                    showModal('you must be an admin to edit teams');
                    return;
                }
                const t = await fetchData(`teams/${id}`);
                openEditModal('teams', id, t, ['name','coach'], updateTeamsList);
            }
        });

        updateTeamsList();
    }

    /* ======================= Matches ======================= */
    if (document.getElementById('match-form')) {
        const form = document.getElementById('match-form'),
              list = document.getElementById('matches-list');

        form.addEventListener('submit', async e => {
            e.preventDefault();
            if (!isAdmin) {
                showModal('You must be an admin to create matches');
                return;
            }
            const date = form['matchDate'].value;
            const time = form['matchTime'].value;
            const t1   = form['matchTeam1'].value;
            const t2   = form['matchTeam2'].value;
            const tor  = form['matchTournament'].value;
            const md   = new Date(date), today = new Date(); today.setHours(0,0,0,0);
            if (md < today) {
                showModal('Cannot create a match with a date in the past.');
                return;
            }
            const teams       = Object.values(await fetchData('teams'));
            const tournaments = Object.values(await fetchData('tournaments'));
            const team1       = teams.find(t=>t.name===t1);
            const team2       = teams.find(t=>t.name===t2);
            const tournament  = tournaments.find(t=>t.name===tor);
            if (!team1 || !team2) {
                showModal('One or both teams do not exist.');
                return;
            }
            if (team1.id===team2.id) {
                showModal('A team cannot play against itself.');
                return;
            }
            if (!tournament) {
                showModal('Tournament not found.');
                return;
            }
            await postData('matches', {
                date, time,
                team1Id: team1.id,
                team2Id: team2.id,
                tournamentId: tournament.id
            });
            updateMatchesList();
            updateTournamentsList(); 
            form.reset();
        });

        list.addEventListener('click', async e => {
            const id = e.target.dataset.id;
            if (e.target.classList.contains('delete-match')) {
                if (!isAdmin) {
                    showModal('You must be an admin to delete matches');
                    return;
                }
                await deleteData('matches', id);
                updateMatchesList();
                updateTournamentsList(); 
            }
            if (e.target.classList.contains('edit-match')) {
                if (!isAdmin) {
                    showModal('You must be an admin to edit matches');
                    return;
                }
                const m = await fetchData(`matches/${id}`);
                openEditModal('matches', id, m, ['date','time'], updateMatchesList);
            }
        });

        updateMatchesList(); 
    }

});


const defaultLineup = {
    GK: "",
    LB: "",
    CB1: "",
    CB2: "",
    RB: "",
    LM: "",
    CM1: "",
    CM2: "",
    RM: "",
    ST1: "",
    ST2: ""
  };

  const defaultTeamLineups = {
    "barcelona": {
      GK: "Ter Stegen",
      LB: "Balde",
      CB1: "Cubarsí",
      CB2: "I.Martinez",
      RB: "Kounde",
      LM: "Fati",
      CM1: "Pedri",
      CM2: "De Jong",
      RM: "Lamine Yamal",
      ST1: "Lewandowski",
      ST2: "Rapinha"
    },
    "realmadrid": {
      GK: "Courtois",
      LB: "medy",
      CB1: "Rudiger",
      CB2: "Militao",
      RB: "carvajal",
      LM: "Vinicius",
      CM1: "Tchouameni",
      CM2: "Modric",
      RM: "Valverde",
      ST1: "Bellingham",
      ST2: "Mbappe"
    },

    "atleticodemadrid": {
      GK: "Oblak",
      LB: "Reinildo",
      CB1: "Gimenez",
      CB2: "Lenglet",
      RB: "Molina",
      LM: "Lino",
      CM1: "Barrios",
      CM2: "De Paul",
      RM: "LLorente",
      ST1: "Julian Alvarez",
      ST2: "Griezmann"
    },

    "liverpool": {
        GK: "Alisson",
        LB: "Robertson",
        CB1: "Van Dijk",
        CB2: "Konate",
        RB: "Alexander-Arnold",
        LM: "Gakpo",
        CM1: "McAllister",
        CM2: "Thiago",
        RM: "Salah",
        ST1: "Nunez",
        ST2: "Jota"
        },

    "borusia": {
        GK: "Kobel",
        LB: "Guerreiro",
        CB1: "Hummels",
        CB2: "Schlotterbeck",
        RB: "Meunier",
        LM: "Brandt",
        CM1: "Bellingham",
        CM2: "Witsel",
        RM: "Reus",
        ST1: "Haaland",
        ST2: "Moukoko"
    },

    "milan": {
        GK: "Maignan",
        LB: "Hernandez",
        CB1: "Tomori",
        CB2: "Kalulu",
        RB: "Florenzi",
        LM: "Leao",
        CM1: "Tonali",
        CM2: "Kessie",
        RM: "Saelemaekers",
        ST1: "Giroud",
        ST2: "Ibrahimovic"
    },

    "monaco": {
        GK: "Nübel",
        LB: "Henrichs",
        CB1: "Disasi",
        CB2: "Badiashile",
        RB: "Maripan",
        LM: "Golovin",
        CM1: "Tchouameni",
        CM2: "Fofana",
        RM: "Diatta",
        ST1: "Ben Yedder",
        ST2: "Boadu"
    },

    "betis": {
        GK: "Bravo",
        LB: "Miranda",
        CB1: "Pezzella",
        CB2: "Bartra",
        RB: "Emerson",
        LM: "Rodriguez",
        CM1: "Guido Rodriguez",
        CM2: "Canales",
        RM: "Fekir",
        ST1: "Borja Iglesias",
        ST2: "Juanmi"
    }
  };



