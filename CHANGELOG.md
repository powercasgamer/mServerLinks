# Changelog

All notable changes to this project will be documented in this file.

## [2025-01-06] # 0.11.15

### Changed
- Skipped 0.1.11 through 0.1.14

### Added
- API (Thanks to [lucko/LuckPerms](https://github.com/LuckPerms/LuckPerms) for the event api)
- Version updater

## [2024-10-02] # 0.1.10 & 0.1.11

### Fixed
- Config migration not working properly

## [2024-08-23] # 0.0.9

### Added
- Placeholder support. You can now use placeholders from PlaceholderAPI and MiniPlaceholders. For PlaceholderAPI you use 
  `<papi:placeholder>` for example, `<papi:player_name>`.
- Some more bStats metrics

### Fixed
- An error being thrown on Velocity when a player joins with a version older than 1.21 [#9](https://github.com/powercasgamer/mServerLinks/pull/9)

## [2024-07-03] # 0.0.7

### Added
- Velocity support

### Changed
- Moved version utility stuff into one class and add a `isDev()` function for future usage.

### Fixed
- Removed debug code
- Only publishing Paper jar on GitHub releases

## [2024-06-25] # 0.0.6

### Added
- Spoogot support
- Configuration migration from YAML to HOCON

## [202-06-24] # 0.0.5

### Added
- Reload command (Note: This will not work for online players, they will need to rejoin the server)
- Per player links, this will allow you to set a link for a specific player (Note: horrible hack)

### Changed
- Temporarily disabled the update checker

## [2024-06-24] # 0.0.4

### Added
- Initial release
- bStats
- UpdateChecker
