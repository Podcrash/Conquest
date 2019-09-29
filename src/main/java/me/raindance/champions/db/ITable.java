package me.raindance.champions.db;

public interface ITable {
    void createTable(String name);
    void createTable();

    void dropTable(String name);
    void dropTable();


}
