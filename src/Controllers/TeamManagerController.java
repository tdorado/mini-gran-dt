package Controllers;

import Models.*;
import Models.Exceptions.InsufficientFundsException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controlador de la vista del manejo de un equipo para un jugador.
 *
 * @author pmommeso
 */
public class TeamManagerController {

    @FXML
    private Button exitButton, ruleButton, playerRankingButton, addPlayerButton, removePlayerButton;
    @FXML
    private ListView<Player> userPlayerList;
    @FXML
    private TabPane teamsTabPanes;
    @FXML
    private Pane teamsToCheckOutPane;
    @FXML
    private Label pointsLabel, fundsLabel;

    private UserDT userDT;
    private static Tournament tournament;

    /**
     * Configura todos los equipos del torneo y jugadores del usuario.
     * Setea los botones de la pantalla
     */
    public void initialize() {
        /*Inicializo variables de instancia*/
        userDT = (UserDT) AccountsManager.getSignedAccount();
        /*Configuro los tabs*/
        for(PhysicalTeam team : tournament.getTeams()) {
            Tab tab = new Tab();
            tab.setText(team.getName());

            /*Defino la data que va a ir adrento de la tabla*/
            ObservableList<Player> data = FXCollections.observableArrayList();
            data.addAll(team.getPlayers());

            /*Defino la tabla de jugadores que va adentro del tab particular*/
            TableView<Player> playerTableView = new TableView<>();
            playerTableView.setItems(data);

            /*Defino las columnas de la tabla*/
            TableColumn<Player, String> playerName = new TableColumn<>("Nombre");
            TableColumn<Player, Integer> playerPoints = new TableColumn<>("Puntos");
            TableColumn<Player, Integer> playerPrice = new TableColumn<>("Precio");
            TableColumn<Player, Integer> playerRanking = new TableColumn<>("Ranking");

            /*Asocio los datos con las celdas de la tabla*/
            playerPoints.setCellValueFactory(info -> (new SimpleIntegerProperty(info.getValue().getPoints())).asObject());
            playerPrice.setCellValueFactory(param -> (new SimpleIntegerProperty(param.getValue().getPrice())).asObject());
            playerName.setCellValueFactory(param -> (new SimpleStringProperty(param.getValue().getName())));
            playerRanking.setCellValueFactory(param -> (new SimpleIntegerProperty(tournament.getRanking(param.getValue()))).asObject());

            /*Agrego las columnas a la tabla*/
            playerTableView.getColumns().addAll(playerRanking, playerName, playerPoints, playerPrice);

            /*Agrego la tabla al tab*/
            tab.setContent(playerTableView);

            /*Agrego la tab*/
            teamsTabPanes.getTabs().add(tab);

            pointsLabel.setText("Puntos actuales: " + Integer.toString(userDT.getPoints(tournament)));
            fundsLabel.setText("Fondos disponibles: " + Integer.toString(userDT.getExpenses().getAvailableFunds(tournament)));
        }
        /*Configuro el listView del usuario*/
        /*Lo lleno con los jugadores que tenga*/
        if(userDT != null && userDT.hasSigned(tournament))
            userPlayerList.setItems(FXCollections.observableArrayList(userDT.getUserTeams().getUserTeamPlayers(tournament)));
        userPlayerList.setCellFactory(param -> new ListCell<Player>() {
                @Override
                protected void updateItem(Player p, boolean empty) { super.updateItem(p, empty);
                if (empty || p == null || p.getName() == null) {
                    setText(null);
                } else {
                    setText(p.getName());
                } }
        });
        /*Permite selecciones múltiples*/
        userPlayerList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox aux = new VBox();
        aux.getChildren().add(teamsTabPanes);
        teamsToCheckOutPane.getChildren().add(aux);
        /*Seteo los listeners de los botones*/
        exitButton.setOnAction(exitHandler);
        ruleButton.setOnAction(ruleHandler);
        playerRankingButton.setOnAction(rankingHandler);
        addPlayerButton.setOnAction(addPlayerHandler);
        removePlayerButton.setOnAction(removePlayerHandler);
    }

    /**
     * Deslogea y abre la ventana de login
     */
    private EventHandler exitHandler = event -> {
        MainApp.setScene("login");
    };

    /**
     * Abre la ventana de reglas
     */
    private EventHandler ruleHandler = event -> {
        MainApp.setNewScene("rulesWindow");
    };

    /**
     * Abre la ventana de ranking de los jugadores
     */
    private EventHandler rankingHandler = event -> {
        UserRankingsController.setInfo(tournament);
        MainApp.setNewScene("userRankings");
    };

    /**
     * Añade un jugador al equipo y decrementa los fondos
     */
    private EventHandler addPlayerHandler = new EventHandler() {
        @Override
        public void handle(Event event) {
            try {
                for (Tab t : teamsTabPanes.getTabs()) {
                    for (Player p : (ObservableList<Player>) ((TableView) t.getContent()).getSelectionModel().getSelectedItems()) {
                        if(!userPlayerList.getItems().contains(p)) {
                            userDT.buy(tournament, p);
                            userPlayerList.getItems().add(p);
                        }
                    }
                }
                fundsLabel.setText("Fondos disponibles: " + Integer.toString(userDT.getExpenses().getAvailableFunds(tournament)));
            } catch (InsufficientFundsException e) {
                showErrorMessage();
            }
        }

    };

    /**
     * Remueve el jugador elegido y aumenta los fondos
     */
    private EventHandler removePlayerHandler = new EventHandler(){
        @Override
        public void handle(Event event) {

            for(Player p : userPlayerList.getSelectionModel().getSelectedItems()) {
                userDT.sell(tournament, p);
                userPlayerList.getItems().removeAll(p);
            }
            fundsLabel.setText("Fondos disponibles: " + Integer.toString(userDT.getExpenses().getAvailableFunds(tournament)));
        }
    };

    /**
     * Muestra mensaje de fondos insuficientes
     */
    private void showErrorMessage() {
        Alert aux = new Alert(Alert.AlertType.ERROR);
        aux.setTitle("ERROR");
        aux.setHeaderText("FONDOS INSUFICIENTES O EQUIPO COMPLETO");
        aux.showAndWait();
    }

    /**
     * Setea un torneo
     * @param t Torneo a setear
     */
    public static void setTournament(Tournament t) {
        tournament = t;
    }

}
