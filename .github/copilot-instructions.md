<!-- Copilot / AI agent guidance for the Hackathon repo -->
# Project quick-start for AI coding agents

This file captures concise, project-specific knowledge that helps an AI coding agent be immediately productive in this repository.

**Big Picture**
- Java Servlet-based monolith packaged as a WAR (`pom.xml` uses `<packaging>war</packaging>`).
- Modules by package under `src/main/java/com/wellness/*` (core, wellness, ai, community, academic).
- Persistence: JPA/Hibernate with `persistence-unit` name `WellnessPU` in `src/main/resources/META-INF/persistence.xml`.
- Runtime: deployed to Apache Tomcat in `tomcat/` (use `tomcat/bin/startup.sh` / `shutdown.sh`).
- External integrations: Supabase Postgres (supabase/migrations/), AWS Rekognition (AWS SDK), Supabase REST via OkHttp.

**Where to look first (key files & dirs)**
- `pom.xml` — Java 17, war packaging, dependencies (Postgres, AWS SDK, Gson, OkHttp).
- `src/main/java/com/wellness/` — main code organized by module (look for `servlet` packages for HTTP endpoints).
- `src/main/resources/META-INF/persistence.xml` — datasource and Hibernate settings (credentials often placed here in dev).
- `src/main/resources/aws.properties.template` and `AWS_REKOGNITION_SETUP.md` — how AWS creds and Rekognition are configured.
- `supabase/migrations/` — ordered SQL files (001_... → 006_...) — apply in order via Supabase SQL editor.
- `src/main/webapp/` and `webapp/*.html` — front-end pages used during manual testing (index, dev4 pages).
- `DEV4_QUICKSTART.md`, `DEV1_SUMMARY.md`, `QUICKSTART.md` — module-specific run/test instructions and API examples.

**Essential developer workflows (commands & examples)**
- Build the WAR:
  - `mvn clean package` (produces `target/wellness-app.war`)
- Run tests:
  - `mvn test` (JUnit 5)
- Deploy locally (standalone Tomcat in repo):
  - Copy `target/wellness-app.war` to `tomcat/webapps/` or run the local `startup.sh` in `tomcat/bin`.
  - Start Tomcat: `./tomcat/bin/startup.sh` (zsh)
  - Access app at: `http://localhost:8080/wellness-app/`
- Tail logs for debugging:
  - `tail -f tomcat/logs/catalina.out` (or `tomcat/logs/*`)
- Database migrations:
  - Apply files in `supabase/migrations/` in their numeric order using Supabase SQL editor or psql against your Supabase/Postgres instance.

**Configuration patterns & conventions**
- Credentials appear in two places during local development: `persistence.xml` (JPA JDBC url/user/password) and `src/main/resources/aws.properties` (AWS keys). When present, treat them as dev-only and avoid committing secrets.
- Preferred safe approach: use environment variables for AWS (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`) or `~/.aws/credentials` and update Tomcat `setenv.sh` for standalone runs.
- Servlet endpoints: look under `com.wellness.*.servlet` packages; endpoints are mapped in `WEB-INF/web.xml` and by servlet classes.
- Persistence: `hibernate.hbm2ddl.auto` is `update` in the repo—be cautious when changing in non-dev environments.

**Integration notes & pitfalls**
- AWS Rekognition: code uses AWS SDK (`software.amazon.awssdk:rekognition`) — follow `AWS_REKOGNITION_SETUP.md` to set credentials and region.
- Supabase: the project expects a Postgres URL and uses RLS. Migrations are not automatically run by Maven; they must be applied manually.
- Frontend testing: pages under `webapp/*.html` (e.g., `academic-gamification.html`, `heart-rate-measurement.html`) exercise camera and Rekognition flows — test in a real browser.

**Quick examples for common tasks**
- Create an API call to test DEV4 tasks:
  - `curl http://localhost:8080/wellness-app/api/academic/tasks/1`
- Start a Pomodoro session (API example in `DEV4_QUICKSTART.md`):
  - `curl -X POST http://localhost:8080/wellness-app/api/academic/pomodoro/start -H "Content-Type: application/json" -d '{"userId":1,"taskId":1,"durationMinutes":25}'`

**Code-review & change guidance for AI edits**
- When modifying DB entities, update `persistence.xml` classes list if necessary and check `supabase/migrations/` for matching schema changes.
- When adding endpoints, register servlets in `WEB-INF/web.xml` if not using automatic servlet annotations.
- Keep changes module-scoped (edit only the `com.wellness.<module>` package for that feature) and respect branch naming: `dev1-wellness`, `dev2-ai`, `dev3-community`, `dev4-academic`.

**Where to add tests**
- Unit tests: place JUnit 5 tests under `src/test/java/` mirroring package structure.
- Integration: run `mvn package` and test against local Tomcat + test database (create a separate Supabase project for CI/integration).

If anything in this file conflicts with up-to-date docs (e.g., `DEV4_QUICKSTART.md`), prefer module docs for feature-level behavior and API examples.

— End of guidance. Ask the devs which sections you should expand or keep minimal.
