package com.sochka.onlinegamestore.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Native payment gateway integration service orchestrating secure LiqPay sandbox transactions.
 */
@Service
@Slf4j
public class LiqPayService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getPublicKey() {
        return System.getProperty("LIQPAY_PUBLIC_KEY", "");
    }

    private String getPrivateKey() {
        return System.getProperty("LIQPAY_PRIVATE_KEY", "");
    }

    /**
     * Generates a self-submitting HTML payment checkout form.
     */
    public String generateCheckoutHtml(String orderId, BigDecimal amount, String email) {
        log.info("Generating LiqPay checkout form for order: {}, amount: {}, user: {}", orderId, amount, email);
        try {
            String publicKey = getPublicKey();
            String privateKey = getPrivateKey();

            if (publicKey.isEmpty() || privateKey.isEmpty()) {
                throw new IllegalStateException("LiqPay API keys are not configured in your .env file.");
            }

            // Construct payment parameters map
            Map<String, Object> params = new HashMap<>();
            params.put("public_key", publicKey);
            params.put("version", 3);
            params.put("action", "pay");
            params.put("amount", amount.doubleValue());
            params.put("currency", "USD");
            params.put("description", "Online Game Store Wallet Top-up: " + email);
            params.put("order_id", orderId);
            params.put("sandbox", 1); // Enforce test sandbox mode!

            // Convert to JSON
            String json = objectMapper.writeValueAsString(params);
            
            // Encode to Base64
            String base64Data = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            
            // Generate SHA-1 Signature
            String signature = calculateSignature(privateKey, base64Data);

            // Construct self-submitting HTML
            return "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Redirecting to LiqPay Sandbox...</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div style=\"text-align: center; margin-top: 100px; font-family: sans-serif;\">\n" +
                    "        <h2>Connecting to LiqPay Secure Gateway...</h2>\n" +
                    "        <p>Please wait, you are being redirected to complete your payment.</p>\n" +
                    "    </div>\n" +
                    "    <form action=\"https://www.liqpay.ua/api/3/checkout\" method=\"POST\">\n" +
                    "        <input type=\"hidden\" name=\"data\" value=\"" + base64Data + "\" />\n" +
                    "        <input type=\"hidden\" name=\"signature\" value=\"" + signature + "\" />\n" +
                    "    </form>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "        document.forms[0].submit();\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";

        } catch (Exception e) {
            log.error("Failed to compile LiqPay checkout HTML: {}", e.getMessage());
            throw new RuntimeException("LiqPay form compilation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Contacts LiqPay API to verify if the payment was successful.
     */
    public boolean verifyPaymentStatus(String orderId) {
        log.info("Contacting LiqPay to verify transaction status for order: {}", orderId);
        try {
            String publicKey = getPublicKey();
            String privateKey = getPrivateKey();

            if (publicKey.isEmpty() || privateKey.isEmpty()) {
                throw new IllegalStateException("LiqPay credentials missing.");
            }

            // Construct status request parameters
            Map<String, Object> params = new HashMap<>();
            params.put("public_key", publicKey);
            params.put("version", 3);
            params.put("action", "status");
            params.put("order_id", orderId);

            String json = objectMapper.writeValueAsString(params);
            String base64Data = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            String signature = calculateSignature(privateKey, base64Data);

            // Construct HTTPS POST form body
            String formBody = "data=" + base64Data + "&signature=" + signature;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.liqpay.ua/api/request"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("LiqPay Response: {}", response.body());

            if (response.statusCode() == 200) {
                // Parse response JSON
                Map<?, ?> responseMap = objectMapper.readValue(response.body(), Map.class);
                String status = (String) responseMap.get("status");
                
                log.info("Order {} current LiqPay status resolved to: {}", orderId, status);
                
                // Allow "success" or "sandbox" (for test payments)
                return "success".equalsIgnoreCase(status) || "sandbox".equalsIgnoreCase(status);
            } else {
                log.warn("LiqPay responded with HTTP error code: {}", response.statusCode());
                return false;
            }

        } catch (Exception e) {
            log.error("Failed to verify transaction status with LiqPay: {}", e.getMessage(), e);
            return false;
        }
    }

    private String calculateSignature(String privateKey, String data) throws Exception {
        String signSource = privateKey + data + privateKey;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] rawSignature = md.digest(signSource.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawSignature);
    }
}
