package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfig {

	@Value("${aws.acesskey}")
	private String acesskey;

	@Value("${aws.secrety}")
	private String secretKey;

	@Bean
	public AmazonS3 s3Cliente() {
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(acesskey, secretKey);

		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.withRegion(Regions.SA_EAST_1).build();
	}

}
