# mServerLinks

This plugin allows you to add custom links to the Server Links feature added in 1.21. [MiniMessage](https://docs.advntr.dev/minimessage/format.html) can be used in the names.

***Get DEV builds [here](https://nightly.link/powercasgamer/mServerLinks/workflows/build/main/artifacts.zip)***

### Screenshots
![](https://i.imgur.com/ac911YS.png)
![](https://i.imgur.com/R3QUcKN.gif)


### Example config:
```hocon
# mServerLinks | by powercas_gamer

# Possible values Type:
#  - REPORT_BUG
#  - COMMUNITY_GUIDELINES
#  - SUPPORT
#  - STATUS
#  - FEEDBACK
#  - COMMUNITY
#  - WEBSITE
#  - FORUMS
#  - NEWS
#  - ANNOUNCEMENTS
links {
  example {
    name="<red>Example"
    uri="https://example.com"
  }
  bug {
    name="Report a Bug"
    uri="https://example.com"
    type="REPORT_BUG"
  }
}
# Links that are only visible to players with the specified permission
# This currently works but is not recommended to use due to Spigot's implementation of the event.
player-links {
  staff-guide {
    name="Staff Guide"
    uri="https://example.com"
    permission="mserverlinks.staff"
  }
}
update-checker=false
bStats=true
```
