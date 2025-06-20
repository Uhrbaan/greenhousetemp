package handlers

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"server/internal/db"
	"time"

	// "server/web/templates/components"
	// "server/web/templates/pages"
	// "server/web/templates/layouts"
	"server/web/templates/pages"
)

type Application struct {
	Repo *db.Queries
}

func (app *Application) HandleRoot(w http.ResponseWriter, r *http.Request) {
	basic := pages.Index("welcome")
	basic.Render(context.Background(), w)
}

func (app *Application) GetLatest(w http.ResponseWriter, r *http.Request) {
	measurement, err := app.Repo.SelectLatestMeasurement(context.Background())
	if err != nil {
		http.Error(w, "Could not fetch latest data", http.StatusInternalServerError)
		log.Println("INFO: the server could not fetch latest data.")
		return
	}
	
	// write the header to return
	w.Header().Set("Content-Type", "application/json")
	j, err := json.MarshalIndent(measurement, "", "    ")
	if err != nil {
		http.Error(w, "Error while encoding the response to JSON.", http.StatusInternalServerError)
		log.Println("INFO: the server could not encode the latest response to JSON.")
		return
	}

	w.Write(j)
}

func (app *Application) GetRange(w http.ResponseWriter, r *http.Request) {
	log.Printf("Got parameters: from: %s, to: %s\n", r.URL.Query().Get("from"), r.URL.Query().Get("to"))
	
	from, err1 := time.Parse(time.DateTime, r.URL.Query().Get("from"))
	to, err2 := time.Parse(time.DateTime, r.URL.Query().Get("to"))

	if err1 != nil || err2 != nil {
		http.Error(w, "Could not parse timestamp, please provide a timestamp of the shape 'YYYY-MM-DD HH:MM:SS'", http.StatusBadRequest)
		log.Println("INFO: the server could not parse ")
		return
	}

	measurements, err := app.Repo.SelectRangeMeasurements(context.Background(), db.SelectRangeMeasurementsParams{
		From: from,
		To: to,
	})

	log.Printf("INFO: Aquired following data: %v", measurements)

	if err != nil {
		http.Error(w, "The data could not be retrieved sucessfully.", http.StatusInternalServerError)
		log.Println("INFO: the server could not retreive the corret data.")
		return
	}

	// write return header
	w.Header().Set("Content-Type", "application/json")
	j, err := json.MarshalIndent(measurements, "", "    ")

	if err != nil {
		http.Error(w, "Error while encoding response to JSON.", http.StatusInternalServerError)
		log.Println("INFO: the server could not encode the response to JSON")
		return
	}

	w.Write(j)
}

func (app *Application) PostData(w http.ResponseWriter, r *http.Request) {
	log.Printf("INFO: Got new data: %v\n", r.Header)

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
