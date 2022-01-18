package com.tuesd4y.routingdashboard.config

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class AwsCredentials(val environment: Environment) {
    val awsAccessKeyId: String
        get() = environment.getProperty("AWS_ACCESS_KEY_ID", "AKIAY6JW2ISLYDYB2WZC")
    val awsSecretAccessKey: String
        get() = environment.getProperty("AWS_SECRET_ACCESS_KEY", "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")
}
