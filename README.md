# Bukkript [WIP]
**Buk**kit **K**otlin Sc**ript**


Bukkript is a Bukkit plugin that allows server admins to customize their server easily with the power of **Kotlin** language and **KotlinBukkitAPI**.

- Easy to use
- Full KotlinBukkitAPI experience
- IntelliJ plugin support ([KotlinBukkitAPI Tooling](https://github.com/DevSrSouza/KotlinBukkitAPI-Tooling/))
- Cache

#### Example Script

Creating a new command that sends `Hello Script World!`

`test.bk.kts`
```kotlin
@file:Script(name = "Test")

command("scripttest") {
    executor {
        sender.msg("Hello Script World!")
    }
}
```
