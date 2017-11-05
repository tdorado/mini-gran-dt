package Controllers;

import Models.Account;
import Models.Administrator;
import Models.Identifiable;
import Models.Tournament;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.util.Pair;

public class TournamentSelectionController {

    @FXML
    private Button editTeamButton;
    @FXML
    private TableView<MyPair<Administrator, Tournament>> tournamentsTableView;

    @FXML
    private void handelTournamentSelection() {
        // pido la seleccion EJ larana: copa mayores y seteo la ventana
        MyPair pair = tournamentsTableView.getSelectionModel().getSelectedItem();
        if (pair != null) {
            AccountsManager.addUserToTournament(pair.getKey(),pair.getValue());
            MainApp.setScene("TeamManager");
        }
    }


    private class MyPair<R extends Identifiable,T extends Identifiable> extends Pair<R,T>{
        private String adminName;
        private String tournamentName;

        public MyPair(R admin, T tournament) {
            super(admin, tournament);
            this.adminName = admin.getName();
            this.tournamentName = tournament.getName()
        }
    }

}
