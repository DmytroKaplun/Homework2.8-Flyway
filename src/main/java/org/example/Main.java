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
        Client client = clientService.getNameById(17, Client.class);
        client.setName("Harry");

//        clientService.updateName(client);
//        Client client = Client.builder()
//                .setName("Timoty")
//                .build();
//        System.out.println(clientService.create(client));

//        for (Client client2 : clientService.getEntitiesList(Client.class)) {
//            System.out.println(client2);
//        }

        Dao<Worker> workertDao = new EntityDao<>(db);
        EntityService<Worker> workerService = new EntityService<>(workertDao);

        Worker worker = workerService.getNameById(35, Worker.class);
        worker.setName("Paul Muad'Dib ");
        workerService.updateName(worker);

        Worker worker1 = Worker.builder()
                .setName("Irulan")
                .setBirthday(LocalDate.parse("1999-08-11"))
                .setLevel(Worker.Level.MIDDLE)
                .setSalary(1000)
                .build();
        System.out.println(workerService.create(worker1));
        workerService.deleteById(34, Worker.class);
        for (Worker worker2 : workerService.getEntitiesList(Worker.class)) {
            System.out.println(worker2);
        }

    }
}
