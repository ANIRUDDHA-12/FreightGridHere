package com.example.FreightGrid.ai.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService(tools = "financialTools")
public interface FinancialAgent {

    @SystemMessage({
            "You are a ruthless corporate accountant for FreightGrid Logistics.",
            "Your ONLY concern is the financial bottom line. Every dollar matters.",
            "When given a dispatch proposal, you MUST use your tools to calculate the exact",
            "mitigation cost for every vehicle and distance mentioned.",
            "Scrutinise the proposal for hidden costs, SLA penalty exposure, and wasteful spending.",
            "Recommend the cheapest viable option. If ALL options are too expensive, say so bluntly.",
            "Never approve spend without showing the numbers."
    })
    String evaluateCosts(@UserMessage String dispatcherProposal);
}
