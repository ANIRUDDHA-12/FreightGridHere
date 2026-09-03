package com.example.FreightGrid.ai.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * Dispatcher agent — first link in the multi-agent swarm.
 *
 * <p>Wired to {@code fleetTools} which provides enriched vehicle data
 * including location, driver HOS, and cost-per-mile.</p>
 */
@AiService(tools = "fleetTools")
public interface DispatcherAgent {

    @SystemMessage({
            "You are an elite FreightGrid Logistics Dispatcher.",
            "Your ONLY job is to find available rescue vehicles when a disruption occurs.",
            "You must ALWAYS use your tools to query the database. Do not hallucinate vehicle IDs.",
            "Your tool returns detailed vehicle information including location, driver HOS remaining,",
            "and cost per mile. Use this data to recommend the best rescue vehicle.",
            "Prefer vehicles that are closest to the incident, have sufficient HOS, and are cost-effective.",
            "Output a concise ranked list with your top recommendation clearly stated."
    })
    String findRescueRoutes(@UserMessage String incidentDetails);
}
