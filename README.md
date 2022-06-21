# MythicDrops

## A Utility Mod to highlight drops

### Goal: Making locating rare drops easier

## Commands
All commands are case-insensitive.
- /mythic - General config (toggle/autotrack) 
- /star <add/remove/list> [args...] - Adds custom names

Star also allows regex, commands:
- /pattern <add/remove/list/test> [args...]

## API Calls

This mod by default, on startup, calls the API exactly once per launch (api.wynncraft.com)

You may make additional calls to update by /mythic reload

## Building

IntellIJ IDEA with JDK 8u211