package pl.coderslab;

import pl.coderslab.entity.User;
import pl.coderslab.entity.UserDao;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        User userMichal = new User("michal", "michal@mail.com", "dummypassword");
        User userMichalTest = new User("michal", "michal@noname.pl", "sillypassword");
        User userKrzys = new User("Krzys", "krzys@niemamaila.pl", "password");

//        UserDao daoObject = new UserDao();
//        User newUser = new User("Michal", "michal@email.com", "dummypassword");
        UserDao daoHandler = new UserDao();
        User checkMichal = daoHandler.read("michal@mail.com");
        User checkKrzys = daoHandler.read(9);
        User checkMichalTest = daoHandler.read("michal@noname.pl");
        User [] checkMichalNoname = daoHandler.readByUsername("michal");
        User krzysToUpdate = daoHandler.read(7);
        if (krzysToUpdate != null){

            krzysToUpdate.setUserName("Krzys");
            krzysToUpdate.setEmail("MAILKRZYS@pl");
            krzysToUpdate.setPassword("HAAASLO");
            daoHandler.update(krzysToUpdate);

        }
        daoHandler.update(new User("chujoza", "mail@mail.pl", "passWORD"));
        if (checkMichal == null){
            User newUser = daoHandler.create(userMichal);
            System.out.println(newUser);
        } else {
            System.out.println(checkMichal);
        }

        if (checkKrzys == null){
            User anotherUser = daoHandler.create(userKrzys);
            System.out.println(anotherUser);
        } else {
            System.out.println(checkKrzys);
        }

        if (checkMichalTest == null){
            User duplicateMichal = daoHandler.create(userMichalTest);
            System.out.println(duplicateMichal);
        } else {
            System.out.println(checkMichalTest);
        }

        daoHandler.delete(8);

        User[] allUsers = daoHandler.findAll();

    }
}