# Simple Block Commands 
[![GitHub Total Downloads](https://img.shields.io/github/downloads/TechnicJelle/SimpleBlockCommands/total?label=Downloads&color=success "Click here to download the plugin")](https://github.com/TechnicJelle/SimpleBlockCommands/releases/latest)

A Minecraft Paper plugin that allows server owners to assign commands that get run when a block is clicked

## [Click here to download!](../../releases/latest)

## [TODO list](../../projects/1?fullscreen=true)

## Configuration
The configuration file for this plugin is a [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md) file

This is the default example config:
```hocon
# Example: https://github.com/TechnicJelle/SimpleBlockCommands/blob/main/example.conf

blocks=[
    {
        commands=[
            {
                command="say Hello World!"
                hand=either
                run-by=player
            }
        ]
        pos {
            x=0
            y=64
            z=0
        }
        world=world
    }
]

```
Everything should be within the `blocks` square brackets [ ]\
To add a new command block, make a new pair of curly brackets { } with a world string, pos object and commands list as you can see in the example.\
If you want to have multiple commands per block you can do that like so:
```hocon
commands=[
    {
        command="say whoosh!"
        hand=either
        run-by=player
    }
    {
        command="tp ~ ~1 ~"
        hand=either
        run-by=player
    }
]
```
You can also leave out the `hand` and `run-by` properties. Their defaults are `either` and `player`, respectively.\
**More examples can be found [here](https://github.com/TechnicJelle/SimpleBlockCommands/blob/main/example.conf)**


## Commands

| Command       | Description                                                               |
|---------------|---------------------------------------------------------------------------|
| `/sbc reload` | Reloads the blockCommands.conf file with the block locations and commands |

## Special thanks to
[kencider](https://github.com/kencinder) for the initial idea for this plugin\
[Antti](https://github.com/Chicken), [Mark](https://github.com/Mark-225) and [Blue](https://github.com/TBlueF) for their help with the design and development of the plugin
