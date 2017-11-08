package Models;

import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class Administrator extends Account implements Serializable{

    private static final long serialVersionUID = 1L;

    private Map<Tournament,ArrayList<User>> tournamentUsers = new HashMap<>();

    public Administrator() {
        super();
    }

    public Administrator(String name) {
        super(name);
    }

    public Set<String> getTournamentNames() {
        Set<String> resp = new HashSet<>();
        for (Tournament t : tournamentUsers.keySet()) {
            resp.add(t.getName());
        }
        return resp;
    }

    public void refresh(Map<String,Map<String,Map<String,Properties>>> dataTournaments) {
        for (Tournament myTour : tournamentUsers.keySet()) {
            myTour.refresh(dataTournaments.get(myTour.getName()));
            refreshUsers(myTour.getName(), dataTournaments.get(myTour.getName()));
        }
    }

    private void refreshUsers(String tourName, Map<String,Map<String,Properties>> tournament) {
        Tournament tour = getTournament(tourName);
        ArrayList<User> users = tournamentUsers.get(tour);
        if (users!=null) {
            for (User user : users) {
                user.refreshPoints(tourName,unifyPlayers(tournament));
            }
        }
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User t, User t1) {
                return t.getPoints()-t1.getPoints();
            }
        });
    }

    //junta todos los jugadores de todos los teams en un solo arreglo clave-valor
    private Map<String,Properties> unifyPlayers(Map<String,Map<String,Properties>> teams) {
        Map<String,Properties> unified = new HashMap<>();
        for (Map<String,Properties> team : teams.values()) {
            unified.putAll(team);
        }
        return unified;
    }

    /**Metodo para que el Controllers pueda tener acceso a los torneos y asi poder mostrarlos al admin*/
    public Set<Tournament> getTournaments() {
        return tournamentUsers.keySet();
    }

    /**Para cuando el administrador quiera crear un nuevo torneo. Mi idea es que desde el Controllers se instancie la
     * clase torneo para poder ingresarla directamente*/
    public void addTournament(Tournament t){
        tournamentUsers.put(new Tournament(t),new ArrayList<User>());
    }

    public boolean hasTournament(String tournamentName) {
        for (Tournament t : tournamentUsers.keySet()) {
            if (t.getName().equals(tournamentName))
                return true;
        }
        return false;
    }

    public Tournament getTournament(String name) {
        for (Tournament t : tournamentUsers.keySet()) {
            if (t.getName().compareTo(name) == 0)
                return new Tournament(t);
        }
        return null;
    }

    public void addUser(String tournament, User user) {
        Tournament tour = getTournament(tournament);
        tournamentUsers.get(tour).add(user);
    }

    public ArrayList<User> getUsers(String tournament) {
        Tournament tour = getTournament(tournament);
        return tournamentUsers.get(tour);
    }

    @Override
    public String toString() {
        return "Administrator{" + "name='" + name + Arrays.toString(tournamentUsers.keySet().toArray()) + '}';
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(name);
        out.writeObject(tournamentUsers);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        name = ois.readUTF();
        tournamentUsers = (Map<Tournament, ArrayList<User>>) ois.readObject();
    }
}