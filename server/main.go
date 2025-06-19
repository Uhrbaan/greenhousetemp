package main

import (
	"context"
	"fmt"
	"log"
	"net/http"
	"encoding/json"

	"database/sql"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/sqlite"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "modernc.org/sqlite"

	"server/internal/db"
)

type application struct {
	Repo *db.Queries
}

func handleRoot(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hello ! welcome to my website")
}

func (app *application) receiveData(w http.ResponseWriter, r *http.Request) {
	log.Println("Got a POST request !")
	
	body := struct {
		Temperature *float64 `json:"temperature"`
		Humidity *float64 `json:"humidity"`
	}{}

	err := json.NewDecoder(r.Body).Decode(&body)
	if err != nil {
		http.Error(w, "Invalid JSON.", http.StatusBadRequest)
		return
	}

	if body.Temperature == nil || body.Humidity == nil {
		http.Error(w, "temperature and or humidity fields are missing.", http.StatusBadRequest)
		return
	}

	// valid json data -> inserting into database
	err = app.Repo.AddMeasurement(context.Background(), db.AddMeasurementParams{
		Temperature: *body.Temperature,
		Humidity: *body.Humidity,
	})
	if err != nil {
		http.Error(w, "The data could not be stored sucessfully.", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintln(w, "Measurements received and stored successfully!")

	measurements, _ := json.Marshal(body)
	log.Printf("INFO: Received and stored data sucessfully: %s\n", measurements)
}

func main() {
	// load database
	database, err := sql.Open("sqlite", "file:./db/database.db")
	if err != nil { log.Fatal(err) }
	defer database.Close()
	repo := db.New(database)

	// apply database migration (if any)
	driver, err := sqlite.WithInstance(database, &sqlite.Config{})
	if err != nil { log.Fatal(err) }

	migration, err := migrate.NewWithDatabaseInstance("file:./db/migrations", "sqlite", driver)
	if err != nil { log.Fatal(err) }
	err = migration.Up()

	if err != nil && err != migrate.ErrNoChange {
        log.Fatalf("FATAL: Failed to apply migrations: %v", err) // Stop on real error
    } else if err == migrate.ErrNoChange {
        log.Println("INFO: No new migrations to apply.") // Informative, not an error
    } else {
        log.Println("INFO: Migrations applied successfully!") // Success message
    }

	app := application{
		Repo: repo,
	}

	router := http.NewServeMux()
	router.HandleFunc("/", handleRoot)
	router.HandleFunc("POST /", app.receiveData)

	server := http.Server{
		Addr: ":8080",
		Handler: router,
	}

	fmt.Println("Server listening on port :8080")
	server.ListenAndServe()
}
