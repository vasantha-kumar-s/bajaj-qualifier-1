package com.bajaj.qualifier.runner;

import com.bajaj.qualifier.model.SolutionRequest;
import com.bajaj.qualifier.model.WebhookRequest;
import com.bajaj.qualifier.model.WebhookResponse;
import com.bajaj.qualifier.service.DatabaseService;
import com.bajaj.qualifier.service.SqlQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SqlQueryService sqlQueryService;
    
    @Autowired(required = false)
    private DatabaseService databaseService;

    @Value("${api.base.url}")
    private String baseUrl;

    @Value("${api.generate.webhook}")
    private String generateWebhookPath;

    @Value("${api.test.webhook}")
    private String testWebhookPath;

    @Value("${user.name}")
    private String userName;

    @Value("${user.regNo}")
    private String userRegNo;

    @Value("${user.email}")
    private String userEmail;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting qualifier flow...");

        try {
            // Test DB if available
            if (databaseService != null) {
                databaseService.testDatabaseConnection();
            }
            
            // Generate Webhook
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse == null || webhookResponse.getAccessToken() == null) {
                logger.error("Failed to generate webhook");
                return;
            }

            logger.info("Webhook generated");
            
            // Generate SQL Query
            String sqlQuery = sqlQueryService.generateSqlQuery(userRegNo);
            logger.info("SQL query generated");

            // Submit solution
            boolean success = submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            
            if (success) {
                logger.info("Solution submitted successfully");
                System.exit(0);
            } else {
                logger.error("Submission failed");
                System.exit(1);
            }

        } catch (Exception e) {
            logger.error("Error in startup flow: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            String url = baseUrl + generateWebhookPath;
            WebhookRequest request = new WebhookRequest(userName, userRegNo, userEmail);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, WebhookResponse.class
            );

            return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
        } catch (Exception e) {
            logger.error("Webhook error: {}", e.getMessage());
            return null;
        }
    }

    private boolean submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            SolutionRequest request = new SolutionRequest(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl, HttpMethod.POST, entity, String.class
            );

            logger.info("Response: {}", response.getBody());
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Submit error: {}", e.getMessage());
            return false;
        }
    }
}
