# Stage 1: Build the Go application
FROM golang:1.24 AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

# Build Go application
RUN CGO_ENABLED=0 GOOS=linux go build -o /greenhousetemp ./main.go

# Stage 2: Create the final production image
FROM alpine:latest

WORKDIR /app

# Copy the built Go application from the builder stage
COPY --from=builder /greenhousetemp .

# Copy the migration files
# Make sure the destination path matches where your application expects to find them
COPY db/migrations ./db/migrations

# Copy static content
COPY web/static ./web/static

# If you also need your database.db for initial setup/testing, copy it too
# COPY db/database.db ./db/database.db

# Expose the port your application listens on
EXPOSE 8080

# Command to run your application
CMD ["/app/greenhousetemp"]