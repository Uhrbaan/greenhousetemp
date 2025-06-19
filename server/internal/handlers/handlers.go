package handlers

import (
	"fmt"
	"log"
	"net/http"
	"encoding/json"
	"context"
	"server/internal/db"

	"server/web/templates/components"
)

type Application struct {
	Repo *db.Queries
}

func (app *Application) HandleRoot(w http.ResponseWriter, r *http.Request) {
	component := components.Hello("Léonard")
	component.Render(context.Background(), w)
}

func (app *Application) ReceiveData(w http.ResponseWriter, r *http.Request) {
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
