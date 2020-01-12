# discord-parker

The following repository contains all of the source needed to compile and run an instance of the Foreman Discord `Parker` bot.

This bot will continuously poll the Foreman `pools` API for newly mapped pools and announce them in a predefined channel, typically [#pools](https://discord.gg/Bh7ptvK).

## Compiling

### Requirements

To build the application from sources, you'll need:
- JDK version 13
- Apache Maven

### Building

To build the bot, from the top level of the repository:

```sh
$ mvn clean install
```

Upon a successful build, you should see something similar to the following:

```sh
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 15.828 s
[INFO] Finished at: 2020-01-12T13:47:57-05:00
[INFO] Final Memory: 50M/512M
[INFO] ------------------------------------------------------------------------
```

The artifacts will be available in the `target` folders.

### Deploying a Docker image

This project leverages the Maven jib plugin to produce and upload a docker image via the following:

```sh
$ mvn clean deploy
```

The following environment variables must be defined to run the bot:
- DISCORD_TOKEN: a discord bot token obtained from Discord
- FOREMAN_API_TOKEN: a Foreman api token obtained from [your Foreman account](https://dashboard.foreman.mn/dashboard/profile/)
- MONGODB_URI: the mongodb uri to a running mongodb instance that may be used by the bot

### Running the bot

For ease of use, a `docker-compose.yml` file has been provided. The following containers will be deployed:
- mongodb
- discord-parker

See below for an example of how to start this bot:

```sh
DISCORD_TOKEN=<discord-token-here> FOREMAN_API_TOKEN=<foreman-token-here> MONGODB_URI=mongodb://discord-parker-mongo:27017/parker docker-compose up -d
```

## License ##

Copyright © 2020, [OBM LLC](https://obm.mn/).  Released under the [GPL-3.0 License](LICENSE).
