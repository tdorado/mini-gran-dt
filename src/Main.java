import Controller.AccountsMananger;
import Model.Administrator;
import Model.User;
import View.Login;


public class Main {

    public static void main(String[] args) {

        AccountsMananger users = new AccountsMananger();
        users.loadUsers();

        AccountsMananger admins = new AccountsMananger();
        admins.loadAdmins();

        LoginWindow.start();
        String username = Login.getUserName;
        String type = Login.getType();// con un selecionador tipo de paises

        if (users.contains(username)){
            User user = (User)users.getAccount(username);
            UserWindow.start(user);
        }else if(admins.contains(username)){
            Administrator admin = (Administrator) users.getAccount(username);
            AdminWindow.start(admin);
        }else{
            if (type.equals("Admin")){
                User user = new User(username);
                users.add(user);
                UserWindow.start(user);}
            else {
                Administrator admin = new Administrator(username);
                admins.add(admin);
                AdminWindow.start(admin);}
        }
    }
}
