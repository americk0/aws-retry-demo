package com.example.retrydemo;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.ListRolesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolesResult;

/**
 * Hello world!
 *
 */
public class App {

	private static final Logger logger = Logger.getLogger(App.class.getName());

	private static final int MAX_RETRIES = 5;

	public static void main( String[] args ) {
		System.out.println( "Hello World!" );

		AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder
				.standard()
				.withClientConfiguration(new ClientConfiguration()
						.withRetryPolicy(getRetryPolicy()))
				.withCredentials(new ProfileCredentialsProvider("test123"))
				.build();

		for (int i=0; i<500; i++) {
			iam.listRoles(new ListRolesRequest());
		}
	}

	private static RetryPolicy getRetryPolicy() {
		RetryPolicy.RetryCondition retryCondition = (AmazonWebServiceRequest request, AmazonClientException exception,
				int retriesAttempted) -> {
			if (exception instanceof AmazonServiceException) {
				AmazonServiceException e = (AmazonServiceException) exception;
				if (e.getErrorCode().equals("Throttling")) {
					if (retriesAttempted < MAX_RETRIES) {
						logger.warning("Request throttled. Retrying...");
						return true;
					} else {
						logger.warning("Max retries reached");
						return false;
					}
				}
			}

			return PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION.shouldRetry(request, exception, retriesAttempted);
		};
		return new RetryPolicy(retryCondition, PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY, MAX_RETRIES,
				PredefinedRetryPolicies.DEFAULT.isMaxErrorRetryInClientConfigHonored());
	}
}
