package org.example;

import org.example.dao.EntityDao;
import org.example.dao.Dao;
import org.example.database.PostgreSqlDb;
import org.example.entity.Client;
import org.example.entity.Worker;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        PostgreSqlDb db = PostgreSqlDb.getInstance();

        Dao<Client> clientDao = new EntityDao<>(db);
        EntityService<Client> clientService = new EntityService<>(clientDao);
        System.out.println(clientService.getNameById(17, Client.class));
        Client client = Client.builder()
                .setName("Vlad")
                .build();
//        System.out.println(clientService.create(client));
//        clientService.updateName(1, "John Brown", Client.class);
//        for (Client client2 : clientService.getEntitiesList(Client.class)) {
//            System.out.println(client2);
//        }

        Dao<Worker> workertDao = new EntityDao<>(db);
        EntityService<Worker> workerService = new EntityService<>(workertDao);
        System.out.println(workerService.getNameById(1, Worker.class));

        Worker worker = Worker.builder()
                .setName("Olena")
                .setBirthday(LocalDate.parse("1991-11-22"))
                .setLevel(Worker.Level.TRAINEE)
                .setSalary(900)
                .build();
        System.out.println(workerService.create(worker));
//        for (Worker worker : workerService.getEntitiesList(Worker.class)) {
//            System.out.println(worker);
//        }
    }
}
