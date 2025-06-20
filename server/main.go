package main

import (
	"fmt"
	"log"
	"net/http"
	"path/filepath"

	"database/sql"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/sqlite"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "modernc.org/sqlite"

	"server/internal/db"
	"server/internal/handlers"
)

func main() {
	// load database
	database, err := sql.Open("sqlite", "file:./db/database.db")
	if err != nil {
		log.Fatal(err)
	}
	defer database.Close()
	repo := db.New(database)

	// apply database migration (if any)
	driver, err := sqlite.WithInstance(database, &sqlite.Config{})
	if err != nil {
		log.Fatal(err)
	}

	migration, err := migrate.NewWithDatabaseInstance("file:./db/migrations", "sqlite", driver)
	if err != nil {
		log.Fatal(err)
	}
	err = migration.Up()

	if err != nil && err != migrate.ErrNoChange {
		log.Fatalf("FATAL: Failed to apply migrations: %v", err) // Stop on real error
	} else if err == migrate.ErrNoChange {
		log.Println("INFO: No new migrations to apply.") // Informative, not an error
	} else {
		log.Println("INFO: Migrations applied successfully!") // Success message
	}

	app := handlers.Application{Repo: repo}

	// configure static files directory access
	staticDir, err := filepath.Abs("./web/static")
	if err != nil {
		log.Fatal("Could not access ./web/static")
	}

	staticFileServer := http.FileServer(http.Dir(staticDir))

	router := http.NewServeMux()
	router.HandleFunc("GET /", app.HandleRoot)

	router.HandleFunc("POST /data", app.PostData)
	router.HandleFunc("GET /data/latest", app.GetLatest)
	router.HandleFunc("GET /data", app.GetRange) // </data/?from=date&to=date>

	router.Handle("GET /static/", http.StripPrefix("/static/", staticFileServer))

	server := http.Server{
		Addr:    ":8080",
		Handler: router,
	}

	fmt.Println("Server listening on port :8080")
	server.ListenAndServe()
}
