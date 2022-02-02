package com.oriolsoler.security.helper

import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.Wait.forListeningPort
import org.testcontainers.containers.wait.strategy.WaitAllStrategy
import java.io.File
import java.lang.System.setProperty

private const val POSTGRES = "postgres"
private const val POSTGRES_PORT = 5432

private const val MAIL_SERVER = "mailserver"
private const val MAIL_SERVER_PORT = 1025

class DockerComposeHelper {

    private val container: DockerComposeContainer<*>

    init {
        container = DockerComposeContainer<Nothing>(File("docker-compose-test.yml"))
            .apply { withLocalCompose(true) }
            .apply {
                withExposedService(
                    POSTGRES,
                    POSTGRES_PORT,
                    WaitAllStrategy(WaitAllStrategy.Mode.WITH_INDIVIDUAL_TIMEOUTS_ONLY)
                        .apply { withStrategy(forListeningPort()) }
                        .apply {
                            withStrategy(
                                Wait.forLogMessage(
                                    ".*database system is ready to accept connections.*",
                                    1
                                )
                            )
                        }
                )
            }.apply {
                withExposedService(
                    MAIL_SERVER,
                    MAIL_SERVER_PORT,
                    WaitAllStrategy(WaitAllStrategy.Mode.WITH_INDIVIDUAL_TIMEOUTS_ONLY)
                        .apply { withStrategy(forListeningPort()) }
                )
            }
    }

    fun start() {
        container.start()
        val postgresHost = container.getServiceHost(POSTGRES, POSTGRES_PORT)
        val postgresPort = container.getServicePort(POSTGRES, POSTGRES_PORT)
        setProperty("database.host", postgresHost)
        setProperty("database.port", postgresPort.toString())
    }

    fun stop() {
        container.stop()
    }
}