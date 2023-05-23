# GTime
A Minecraft plugin to help you track your runs for parkour-like minigames. Currently it supports YML and SQL data storing.

## Configuration
The configuration file has 2 main fields you want to take a look at: the database type you're going to use and the database credentials.

The *database* field can take 2 values: **sql** and **yml**. Using anything but these 2 will result in the plugin not working.

The *sql* field can be ignored in case you're using yml. You'll need to fill in the the database information and make sure the database exists (tables are automatically created and you're encouraged to not touch them).

## Contributing
These plugins are made open-source and free to use so any help is appreciated. If you want to contribute feel free to create a pull request. Not respecting the Java standards or having a poor code quality might lead to your PR being rejected. 
