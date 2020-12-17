## mocking-examples

This is a plain scala project. Below are some sbt commands to get you started

### Commands

#### sbt

  ```bash
  sbt
  ```

This will start the interactive sbt shell. You can also directly follow `sbt` with one of the subsequent commands, but
I'd recommend using the shell, as it is a lot faster.


#### compile

  ```bash
  compile
  ```

Compiles the project without starting it. 

**Tip:** You can prefix any command with `~` (e.g. `~compile`) so it is executed on every file change.


#### run

  ```bash
  run
  ```

Compiles the project and starts the main method (or `App` object). If there is more than one in the project, you'll
be prompted to choose one to run.


#### test

  ```bash
  test
  ```

Runs the project test suite.

**Tip:** You can use `~test` to run your test suite continuously on every file change. This is handy when doing TDD/BDD.


#### testOnly (or test-only)

  ```bash
  testOnly MainSpec
  ```

Runs the specified spec only. You can use a wildcard (e.g. `*MainSpec`) to execute multiple matching specs.


#### testQuick (or test-quick)

  ```bash
  testQuick
  ```

Runs only the specs which were failing in the last executed test command.


#### reload

Use `reload` in your sbt session when you make any changes to `build.sbt` 
or files in the `project` folder. Otherwise the changes will likely not be applied.
