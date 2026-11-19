package org.example;

import org.example.cli.CliApplication;
import org.example.config.ApplicationFactory;
import org.example.registry.SourceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;

public class Main {
    private  static final Logger logger =  LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        int exitCode = 0;

        try (HttpClient client = HttpClient.newHttpClient()) {
            ApplicationFactory factory = new ApplicationFactory();
            SourceRegistry registry = factory.createRegistry(client);
            CliApplication application = factory.createCliApplication(registry);

            application.run(args);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid arguments: {}", e.getMessage());
            exitCode = 2;
        } catch (IOException e) {
            logger.error("I/O operation failed", e);
            exitCode = 1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Operation interrupted", e);
            exitCode = 130;
        }

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
