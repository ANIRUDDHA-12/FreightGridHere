# FreightGrid - AI-Powered Freight & SLA Governance Engine

## Core Tech Stack
- Backend: Java 21, Spring Boot 3.3+, Spring Data JPA, Spring Security (OAuth 2.0 Resource Server).
- AI Engine: Spring AI (OpenAI Starter re-routed to Groq LPU API `https://api.groq.com/openai/v1` using model `llama-3.3-70b-versatile`).
- Database: Serverless PostgreSQL (Neon DB with HikariCP connection pooling).
- Agent Skills: `fleet-cost-optimizer.skill.md` stored in `src/main/resources/skills/`.
- Frontend (Future Sprint): Next.js, Tailwind CSS, React Flow (Network Graph UI).

## Architectural Rules & Guardrails
1. NEVER hardcode API keys, passwords, or hosts. Load them via `spring.config.import=optional:file:.env[.properties]`.
2. All mathematical calculations (costs, SLA penalties, fuel) MUST be executed by deterministic Java code via Spring AI Function Calling (`java.util.Function`), NOT hallucinated by the LLM.
3. Database connection pool MUST be kept low (`maximum-pool-size=5`) to accommodate Neon DB serverless connection limits.
4. Maintain absolute role separation:
   - `ROLE_ANALYST`: Can approve mitigation strategies < 5,000.
   - `ROLE_DIRECTOR`: Required for approving mitigation strategies >= 5,000 or SLA penalty overrides.
5. Directory Structure MUST follow: `com.freightgrid.core` -> `config`, `domain` (entity, enums, repository), `service`, `ai` (functions, dto), `web` (controller, dto, exception), `security`.
