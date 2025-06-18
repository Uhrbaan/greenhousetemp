# Greenhouse Temperature
This project aims to notify the user when the greenhouse is too hot.
The temperature and humidity inside the greenhouse can be seen on a website or an Android app.

## Tech stack
### Server
The server uses [go](https://go.dev/) and its builtin library for the webserver, 
[sqlite](https://sqlite.org) as a database, 
[golang-migrate](https://pkg.go.dev/github.com/golang-migrate/migrate/v4)
for database migration, and [sqlc](https://sqlc.dev/) for the generation of 
database queries.

### Android app
Is build with [Android Studio](https://developer.android.com/studio) and [Kotlin](https://kotlinlang.org/)
