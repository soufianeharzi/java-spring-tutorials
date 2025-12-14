# AGENTS.md

AI guidance for working with this repository.

## Project Overview

**Stack:** Spring Boot 4.0.0, Java 17, Maven
**Structure:** Multi-module monorepo with 4 tutorial modules
**Purpose:** Hands-on implementations of official Spring Guides with detailed documentation

## Read First

Before making changes, read these in order:
1. This file (AGENTS.md)
2. [docs/README.md](docs/README.md) - Documentation hub
3. [docs/adr/README.md](docs/adr/README.md) - ADR index and numbering
4. Target module's README and ADRs (especially for 03 modules)
5. [templates/](templates/) - Use these for new files

## Adding a New Module

1. **Create from template**: Copy structure from an existing module or use `templates/`
2. **Required structure**:
   ```
   modules/XX-module-name/
   ├── pom.xml                    # Child POM (see POM guidance below)
   ├── README.md                  # Use templates/MODULE_README.md
   ├── .gitattributes             # Copy from existing module
   ├── src/main/resources/
   │   └── application.properties # Set server.port if not 8080
   └── docs/
       ├── setup/
       │   ├── spring-initializr.md
       │   └── run-instructions.md
       ├── concepts/              # At least one concept doc
       ├── reference/
       │   └── guide.md           # Link to Spring guide
       └── images/
   ```
3. **Register module**: Add to parent `pom.xml` `<modules>` section
4. **Badges**: Added automatically by CI after first successful build on main
5. **Update docs**: Add to `docs/INDEX.md` and root `README.md` contents table
6. **Update Module Ports table**: Add entry to the table below if module uses a port

## Adding Documentation

- **Concept docs**: Use `templates/CONCEPT.md`, place in `docs/concepts/`
- **Images**: Place in `docs/images/`, name descriptively (e.g., `feature-output.png`), use relative links
- **Update links**: Add to module README, `docs/README.md`, and `docs/INDEX.md`
- **API changes in 03 modules**: Update the "API Contract" section in both provider and consumer READMEs

## Commands

```bash
# Build and test all modules
./mvnw clean verify

# Run a specific module
./mvnw spring-boot:run -pl modules/{module-name}

# Run tests for a specific module
./mvnw test -pl modules/{module-name}

# Run checkstyle
./mvnw checkstyle:check

# Run mutation testing (03 modules only)
./mvnw test org.pitest:pitest-maven:mutationCoverage -pl modules/03-quote-service
```

## Testing

- All modules have unit tests in `src/test/java/`
- Run `./mvnw test` from root to test all modules
- Module 02 uses Awaitility for async scheduler testing
- Module 03 uses MockMvc and mocked RestClient for isolation

## ADR Process

**When to write an ADR:**
- Architecture changes, new patterns, or significant design decisions
- CI/CD configuration changes
- New shared dependencies
- Deviations from Spring guide implementations

**Numbering:**
- Repository-level ADRs: `docs/adr/ADR-XXXX-title.md` (sequential from 0001)
- Module-specific ADRs: `modules/{module}/docs/adr/ADR-XXXX-title.md` (sequential per module)
- Note: `03-spring-consuming-rest` starts at ADR-0003 (inherited from development); do not add placeholder ADRs

**Process:**
1. Use `templates/ADR_TEMPLATE.md`
2. Place in appropriate `docs/adr/` folder
3. Update `docs/adr/README.md` index (for repo-level) or module README ADR table

## Parent/Child POM

- **Parent POM** (`pom.xml`): Manages all dependency and plugin versions via `<dependencyManagement>` and `<pluginManagement>`
- **Child POMs**: Inherit versions from parent; do NOT specify versions in child POMs
- **Checkstyle path**: Child POMs use `<configLocation>../../checkstyle.xml</configLocation>`
- **Adding a module**: Add `<module>modules/XX-name</module>` to parent's `<modules>` section

## CI/CD and Badges

**GitHub Actions** (`.github/workflows/java-ci.yml`):
- `build-test`: Runs on all pushes/PRs - tests, JaCoCo, Checkstyle, SpotBugs
- `link-check`: Validates markdown links (excludes shields.io, localhost)
- `mutation-test`: PITest on 03 modules (main branch only)
- `update-badges`: Generates badge JSON files (main branch only)
- `security-check`: OWASP dependency scan (main branch only)

**Badge generation:**
- Script: `ci/scripts/ci_metrics_summary.py` parses XML reports
- Output: `ci/badges/*.json` (aggregate) and `ci/badges/{module}/*.json`
- Shields.io fetches JSON from raw.githubusercontent.com
- Badges update automatically on successful main branch builds
- **Do NOT edit badge JSON files manually** - they are overwritten by CI

**Run link checker locally:**
```bash
# Install lychee: brew install lychee (macOS) or cargo install lychee
lychee '**/*.md' --exclude 'img.shields.io' --exclude 'localhost'
```

## Consistency Rules

- **Formatting**: Follow `.editorconfig` (spaces for Java/MD, tabs for XML)
- **Line endings**: LF for all files except `*.cmd` (CRLF)
- **Markdown**: Use fenced code blocks with language, tables for structured data
- **Images**: PNG preferred, descriptive names, store in `docs/images/`
- **No module-level AGENTS.md**: Single root file only

## Boundaries

### Always Do
- Run `./mvnw clean verify` before committing
- Update docs when changing behavior
- Add tests for new functionality
- Follow existing code patterns

### Ask First
- Adding new modules
- Changing CI configuration
- Modifying shared dependencies in parent POM
- Architectural changes (create ADR)

### Never Do
- Commit without running tests
- Remove tests to make CI pass
- Hardcode credentials or secrets
- Skip checkstyle violations

## Key Files

| Purpose | Location |
|---------|----------|
| Parent POM | `pom.xml` |
| CI Workflow | `.github/workflows/java-ci.yml` |
| Checkstyle | `checkstyle.xml` |
| Badge Generator | `ci/scripts/ci_metrics_summary.py` |
| ADR Index | `docs/adr/README.md` |
| Quick Start | `docs/QUICK_START.md` |

## Module Ports

| Module | Port |
|--------|------|
| 01-spring-hello-rest | 8080 |
| 02-spring-scheduling-tasks | 8080 (console only) |
| 03-quote-service | 8080 |
| 03-spring-consuming-rest | 8081 |

## Links

- [Module READMEs](modules/)
- [ADR Index](docs/adr/README.md)
- [Templates](templates/)
- [CI Plan](docs/CI_PLAN.md)
