

# GTime
A Minecraft plugin to help you track your runs for parkour-like minigames. Currently it supports YML and SQL data storing.

## Configuration
The configuration file has 2 main fields you want to take a look at: the database type you're going to use and the database credentials.

The *database* field can take 2 values: **sql** and **yml**. Using anything but these 2 will result in the plugin not working.

The *sql* field can be ignored in case you're using yml. You'll need to fill in the the database information and make sure the database exists (tables are automatically created and you're encouraged to not touch them).

## PAPI

There's 2 PAPI hooks you can use: 

* %gtimepapi_current_time% displays the current time for your run.
* %gtimepapi_best_time_\<map\>% displays the best time for the map you provide.

## Commands
The plugin has 2 commands so far:

### gdebug
Command that is used for debugging purposes. Unless you're trying to break the plugin you will most likely not need this command.

- /gdebug  save \<map\> \<time\> *(gtime.admin.save)*
command will save a time (in milliseconds) for you.
- /gdebug get \<map\> *(gtime.admin.get)*
command will fetch a time for a specific map.

### gtime
Main command of the plugin.

- /gtime start \<map\> *(gtime.use)*
command will start the run for you for a specific map.
- /gtime end \<map\> *(gtime.use)*
command will end the run for you for a specific map.
- /gtime times *(gtime.times)*
command will open a container displaying all of your previous runs.
- /gtime leaderboard \<map\> *(gtime.leaderboard)*
Shows a leaderboard for a certain map.

## Contributing
These plugins are made open-source and free to use so any help is appreciated. If you want to contribute feel free to create a pull request. Not respecting the Java standards or having a poor code quality might lead to your PR being rejected. 

## API
In order to use the API make sure to add the dependency.
[![](https://jitpack.io/v/Gargant0373/GTime.svg)](https://jitpack.io/#Gargant0373/GTime)

To start using the API you'll need a reference to the GTimeAPI.

```java
GTimeAPI api = GTime.api();
```

To start a run for someone you can use the **RunService** instance from the api. Let's say you'd like someone to start the run when breaking a block.
*(NOTE! You can implement this in any context, this is just an example)*

```java
@EventHandler
public  void  onBlockBreak(BlockBreakEvent  event)  {
Player  player  =  event.getPlayer();

// getRunService() returns a RunService instance
api.getRunService().startRun(player.getUniqueId(),  "myCoolMapName");
// startRun takes 2 parameters- the player unique id and the map name
}
```

In order to end someone's run you can use the end run method from the RunService.

The end run method returns a **RunEnd** object. This object contains the name of the map, the current time and the previous time the player had. It also has a built-in message broadcast method called `broadcastEndMessage`.

See an example of how to broadcast the message and implement a run end:

```java
// the end run method takes one parameter,
// the unique id of the player
RunEnd run = api.getRunService().endRun(player.getUniqueId());
run.broadcastMessage(player);
```
