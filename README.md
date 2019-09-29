# Champions!

:^)

# USE `./gradlew shadowjar` INSTEAD of `./gradlew jar`

# How to setup ( don't read this)

1. Make a new folder called 'server'
2. Make another folder in it called 'plugins'
3. Drag the spigot jar from the libs folder to server
4. Drag the protocolib jar from the libs folder to server/plugins
5. Rename _build.gradle and change `System.getenv().get("testserver")` to the directory of the plugins folder
6. Open intelij, and you are all set!

# How to create new skills TODO
1. uhhhhhh

# Testing

For whatever reason when it says `Test events were not recieved`, do not be worried. It is working, the test will fail if the assertion fails.

You can run tests in two ways with selecting the test class and selecting the run test (the green arrow), or my favorite:
```bash
gradlew clean test
```

You can forgo the clean, but it's nice. However, if you do manage to fix this bug, it would be very nice to tell me (rain) because it's very annoying.

To run individuals tests:
```bash
gradlew test --tests <testname here>
```

See https://docs.gradle.org/current/userguide/command_line_interface.html for more reading.

# Postgres
1. Install postgres + pgadmin4.
2. Set your environment variables:
    ```bash
    setx PSQL_HOST localhost
    setx PSQL_PASS <pass>
    setx PSQL_PORT 5432
    setx PSQL_USER postgres
    setx PSQL_DBNAME champions
    ```
    Make sure to restart your terminal.
3. Open pgadmin, log in and create a new database named "champions".
4. Run the test using `./gradlew clean test` to create the new dbs (See: DatabaseLoginTest)
5. Run `./gradlew clean generateChampionsdatabaseJooqSchemaSource`
6. You should get new files on the directory build/generated-src/jooq
7. Now you can reference said files! See the ChampionsKitTable class for examples!
8. Learn PSQL

# Redis

1. Install redis (windows: https://github.com/microsoftarchive/redis/releases)
2. Set your environment variables.
    ```bash
    setx REDIS_HOST redis://127.0.0.1:6379
    setx REDIS_PASS foobared
    ``` 
3. Go to the directory where you installed it, and go to the config. Delete the '#' next to "requirepass".
4. Run the redis server, using (windows: command prompt) 
    ```bash
    redis-server ./redis.conf
    
    redis-cli - for commandline  
    ```
    https://redis.io/topics/rediscli