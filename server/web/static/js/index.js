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
    }))
})