package com.example.demo.repository;

import com.example.demo.entity.Mail;

public interface EmailRepository {

	void emailResetPassword(Mail mail);

}
