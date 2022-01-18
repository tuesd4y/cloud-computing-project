package com.tuesd4y.routingdashboard.config

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class AwsCredentials(val environment: Environment) {
    val awsAccessKeyId: String
        get() = environment.getProperty("AWS_ACCESS_KEY_ID", "")
    val awsSecretAccessKey: String
        get() = environment.getProperty("AWS_SECRET_ACCESS_KEY", "")
}
