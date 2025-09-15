To initialize the service using the existing compose.yaml and Dockerfile, add the following instructions to your README:

---

## How to Run Zoolip with Docker Compose

1. **Install Docker and Docker Compose**
   - Make sure you have [Docker](https://docs.docker.com/get-docker/) installed.
   - Docker Compose comes bundled with Docker Desktop, or install separately as needed.

2. **Clone the Repository**
   ```bash
   git clone https://github.com/santiagoabsalom-star/Zoolip.git
   cd Zoolip
   ```

3. **Start the Services**
   ```bash
   docker compose up --build
   ```
   This command will:
   - Build the application image using the provided `Dockerfile`.
   - Start a MySQL database service (`db`) and the main application (`monolito`).
   - Map application port `3050` and database port `3306` to your host.

4. **Access the Application**
   - Once the containers are healthy, you can access the app at: `http://localhost:3050/`
   - The MySQL database will be available at `localhost:3306` (credentials are set in `compose.yaml`).

5. **Stopping the Services**
   ```bash
   docker compose down
   ```

**Note:** The database will persist data in the `db-data` directory. Initialization scripts can be placed in `db-init`.

---
