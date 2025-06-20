function formatDateTime(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

document.addEventListener("alpine:init", () => {
    Alpine.data("thermometer", () => ({
        tempHeight: 1.0,

        temperature: 20.0,
        minTemp: 18.0,
        maxTemp: 35.0,
        minThermometer: 1.0,
        maxThermometer: 27.0,
        pollIntervalsMs: 1 * 60 * 1000,

        // fetch data on loading, setup the polling
        init() {
            this.fetchData()
            setInterval(() => this.fetchData(), this.pollIntervalsMs)
        },

        // fetch the data from /data/latest
        fetchData() {
            fetch("/data/latest")
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Http Error! Status: ${response.status}`)
                }
                return response.json()
            })
            .then(data => {
                this.temperature = data.temperature
                this.updateHeight()
                console.log("Fetched new data: ", data)
            })
            .catch(error => {
                console.error("Error while fetching data: ", error)
            })
        },

        // update the height based on the temperature
        updateHeight() {
            const clampedTemp = Math.max(this.minTemp, Math.min(this.maxTemp, this.temperature));
            this.tempHeight = this.minThermometer + ((clampedTemp - this.minTemp) / (this.maxTemp - this.minTemp)) * (this.maxThermometer - this.minThermometer)
            this.tempHeight = Math.max(this.minThermometer, Math.min(this.tempHeight, this.maxThermometer))
        }
    })),

    Alpine.data("sendData", () => ({
        formData: {
            temperature: '',
            humidity: ''
        },

        submitForm() {
            fetch("/data", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    temperature: parseFloat(this.formData.temperature),
                    humidity: parseFloat(this.formData.humidity)
                })
            })
            .then(response => {
                if (response.ok) {
                    console.log("message sent successfully !")
                } else {
                    console.error("Server responded with an error.")
                }
            })
            .catch(error => {
                console.error("Server responded with an error: ", error)
            })
        }
    })),

    Alpine.data("tempGraph", () => ({
        duration: 'day',
        chart: null,

        init() {
            if (this.chart) {
                this.chart.destroy()
            }

            const ctx = document.getElementById("temp-graph-chart")
            this.chart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: [],
                    datasets: [{
                        label: 'Température (°C)',
                        data: [],
                        borderColor: 'rgb(75, 192, 192)', // Added color for line
                        tension: 0.1, // Smoothness of the line
                        fill: false, // Don't fill area under the line
                        pointRadius: 3,
                        pointHoverRadius: 5,
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false, // Allow canvas to stretch with parent
                    scales: {
                        x: {
                            type: 'time', // Important: Use time scale for dates
                            time: {
                                unit: 'hour', // Default unit, will adjust dynamically
                                tooltipFormat: 'MMM d, H:mm', // Format for tooltips
                                displayFormats: {
                                    hour: 'H:mm',
                                    day: 'MMM d',
                                    week: 'MMM d',
                                    month: 'MMM', // Example for month/year
                                    year: 'yyyy',
                                }
                            },
                            title: {
                                display: true,
                                text: 'Time'
                            }
                        },
                        y: {
                            beginAtZero: false,
                            title: {
                                display: true,
                                text: 'Temperature (°C)'
                            }
                        }
                    },
                    plugins: {
                        tooltip: {
                            callbacks: {
                                title: function(context) {
                                    if (context.length > 0) {
                                        // Format tooltip title for better date display
                                        return new Date(context[0].parsed.x).toLocaleString();
                                    }
                                    return '';
                                }
                            }
                        }
                    }
                }
            })

            this.fetchGraphData()
        },

        fetchGraphData() {
            let toDate = new Date()
            let fromDate = new Date()

            switch (this.duration) {
                case 'day':
                    fromDate.setDate(toDate.getDate() - 1);
                    break;
                case 'week':
                    fromDate.setDate(toDate.getDate() - 7);
                    break;
                case 'month':
                    fromDate.setMonth(toDate.getMonth() - 1);
                    break;
                case 'year':
                    fromDate.setFullYear(toDate.getFullYear() - 1);
                    break;
            }

            const fromStr = formatDateTime(fromDate)
            const toStr = formatDateTime(toDate)

            const url = `/data?from=${encodeURIComponent(fromStr)}&to=${encodeURIComponent(toStr)}`
            console.log("Fetching data from ", url)

            fetch(url)
            .then(response => {
                if (response.ok) {
                    console.log("Data retrieved sucessfully")
                } else {
                    throw new Error(`HTTP Error ! Status: ${response.status}`)
                }

                return response.json()
            })
            .then(measurements => {
                const labels = measurements.map(m => new Date(m.Time)); // Use Date objects for time scale
                const data = measurements.map(m => m.Temperature);

                this.chart.data.labels = labels
                this.chart.data.datasets[0].data = data
                this.chart.update()
            })
            .catch(error => {
                console.error(`Error while fetching data: ${error}`)
            })
        }
    }))
})