package main

import (
	"context"
	"fmt"
	"log"
	"net/http"

	"database/sql"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/sqlite"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "modernc.org/sqlite"

	"server/internal/db"
)

func handleRoot(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hello ! welcome to my website @ %s", r.URL.Path[1:])
}

func main() {
	fmt.Println("Hello, world !")
	log.Println("Starting the web server.")

	ctx := context.Background()

	// open the database
	database, err := sql.Open("sqlite", "file:./db/database.db")

	if err != nil {
		log.Fatal(err)
	}
	log.Println("The database was opened successfully !")

	defer database.Close()

	// get golang-migrate specific drivers for the db
	driver, err := sqlite.WithInstance(database, &sqlite.Config{})

	if err != nil {
		log.Fatal(err)
	}
	log.Println("The driver for sqlite was loaded successfully !")

	// initialize golang-migrate
	migration, err := migrate.NewWithDatabaseInstance(
		"file:./db/migrations",
		"sqlite",
		driver,
	)
	
	if err != nil {
		log.Fatal(err)
	}
	log.Println("Successfully started golang-migrate !")

	// update database if needed
	err = migration.Up()

	if err != nil && err != migrate.ErrNoChange {
        log.Fatalf("FATAL: Failed to apply migrations: %v", err) // Stop on real error
    } else if err == migrate.ErrNoChange {
        log.Println("INFO: No new migrations to apply.") // Informative, not an error
    } else {
        log.Println("INFO: Migrations applied successfully!") // Success message
    }

	router := http.NewServeMux()
	router.HandleFunc("/", handleRoot)

	server := http.Server{
		Addr: ":8080",
		Handler: router,
	}

	repo := db.New(database)
	row, err := repo.SelectLatestMeasurement(ctx)
	
	if err != nil {
		log.Print("The database query failed.")
	}

	fmt.Printf("%v was measured at %v\n", row.Value, row.Max)

	fmt.Println("Server listening on port :8080")
	server.ListenAndServe()
}
