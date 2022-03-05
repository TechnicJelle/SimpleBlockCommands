# Right Click Command 
[![GitHub Total Downloads](https://img.shields.io/github/downloads/TechnicJelle/RightClickCommand/total?label=Downloads&color=success "Click here to download the plugin")](https://github.com/TechnicJelle/RightClickCommand/releases/latest)

A Minecraft Paper plugin that allows server owners to assign commands that get run when a block is clicked

## [Click here to download!](../../releases/latest)

## [TODO list](../../projects/1?fullscreen=true)

## Configuration
The configuration for this plugin is very simple.\
It uses a CSV (Comma Separated Values) file. Think of it like a spreadsheet.\
The first row is the heading row, which tells you what to put in each column.\
This is the basic CSV file:
```csv
"world","x","y","z","command"
"world","0","64","0","say Hello world!"
```
This prints `[Server] Hello World`

If you want to use "double quotes" in your commands, use double double quotes, like this:
```csv
"world","0","64","0","say ""Hello world!"""
```
This prints `[Server] "Hello World"`

You can have as many commands per block as you wish, and they'll get run from left to right:
```csv
"world","0","64","0","say One!","say Two@","say Three#" 
```
This prints\
`[Server] One!`\
`[Server] Two@`\
`[Server] Three#`


## Commands

| Command       | Description                                                |
|---------------|------------------------------------------------------------|
| `/rcc reload` | Reloads the CSV file with the block locations and commands |

## Special thanks to
[kencider](https://github.com/kencinder) for the initial idea for this plugin\
[Antti](https://github.com/Chicken), [Mark](https://github.com/Mark-225) and [Blue](https://github.com/TBlueF) for their help with the design and development of the plugin
