# test

:^)

# How to setup

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